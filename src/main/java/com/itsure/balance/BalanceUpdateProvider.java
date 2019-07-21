package com.itsure.balance;

/**
 * @author itsure
 * @date 2019/07/18
 */
public interface BalanceUpdateProvider {

    public boolean addBalance(Integer step);

    public boolean reduceBalance(Integer step);
}
