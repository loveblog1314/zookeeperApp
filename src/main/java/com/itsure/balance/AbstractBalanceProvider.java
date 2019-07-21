package com.itsure.balance;

import java.util.List;

public abstract class AbstractBalanceProvider<T> implements BalanceProvider {

    protected abstract T balanceAlgorithm(List<T> items);

    protected abstract List<T> getBalanceItems();

    @Override
    public Object getBalanceItem() {
        return balanceAlgorithm(getBalanceItems());
    }
}
