package com.itsure.balance.server.impl;


import com.itsure.balance.IServer;
import com.itsure.balance.RegisterProvider;
import com.itsure.balance.ServerData;
import com.itsure.balance.ZooKeeperRegistContext;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.SerializableSerializer;

public class ServerImpl implements IServer {
	
	private EventLoopGroup bossGroup = new NioEventLoopGroup();
	private EventLoopGroup workGroup = new NioEventLoopGroup();
	private ServerBootstrap bootStrap = new ServerBootstrap();
	private ChannelFuture cf;
	private String zkAddress;
	private String serversPath;
	private String currentServerPath;
	private ServerData serverData;
	
	private volatile boolean binded = false;
	
	private final ZkClient zkClient;
	private final RegisterProvider registerProvider;
	
	private static final Integer SESSION_TIME_OUT = 5000;
	private static final Integer CONNECT_TIME_OUT = 5000;
		
	
	
	public String getCurrentServerPath() {
		return currentServerPath;
	}

	public String getZkAddress() {
		return zkAddress;
	}

	public String getServersPath() {
		return serversPath;
	}

	public ServerData getServerData() {
		return serverData;
	}

	public void setServerData(ServerData serverData) {
		this.serverData = serverData;
	}

	public ServerImpl(String zkAddress, String serversPath, ServerData serverData){
		this.zkAddress = zkAddress;
		this.serversPath = serversPath;
		this.zkClient = new ZkClient(this.zkAddress,SESSION_TIME_OUT, CONNECT_TIME_OUT, new SerializableSerializer());
		this.registerProvider = new DefaultRegistProvider();
		this.serverData = serverData;
	}	
	
	//初始化服务端
	private void initRunning() throws Exception {
		
		String mePath = serversPath.concat("/").concat(serverData.getPort().toString());
		//注册到zookeeper
		registerProvider.regist(new ZooKeeperRegistContext(mePath, zkClient, serverData));
		currentServerPath = mePath;
	}

	public void bind() {
		
		if (binded){
			return;
		}
		
		System.out.println(serverData.getPort()+":binding...");
		
		try {
			initRunning();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		bootStrap.group(bossGroup,workGroup)
		.channel(NioServerSocketChannel.class)
		.option(ChannelOption.SO_BACKLOG, 1024)
		.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline p = ch.pipeline();
                p.addLast(new ServerHandler(new DefaultBalanceUpdateProvider(currentServerPath, zkClient)));
            }
        });
		
		try {
			cf =  bootStrap.bind(serverData.getPort()).sync();
			binded = true;
			System.out.println(serverData.getPort()+":binded...");
			cf.channel().closeFuture().sync();			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}finally{
			bossGroup.shutdownGracefully();
			workGroup.shutdownGracefully();
		}
		
	}
	
}
