package com.koder.stock.coreservice.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class LockUtil {
    // Key: 锁类型, {Key : 锁对象}
    private static final Map<String, AtomicInteger> concurrentControlMap = new ConcurrentHashMap<>();
    public static final String CREATE_ORDER_LOCKED = "createOrders";
    public static final String EN_TRUSTED_ORDER = "entrustedOrders";
    public static final String QUOTATION_LISTEN = "quotationListen";
    public static final String CALCULATE_ORDER = "calculateOrder";


    public static boolean getLock(String methodName, String lockName) {
        String lockKey = methodName + "-" + lockName;
        AtomicInteger stockLock = concurrentControlMap.get(lockKey);
        if (stockLock == null) {
            synchronized (lockKey) {
                stockLock = concurrentControlMap.get(lockKey);
                if (stockLock == null) {
                    stockLock = new AtomicInteger();
                }
                concurrentControlMap.putIfAbsent(lockKey, stockLock);
            }
        }
        return stockLock.compareAndSet(0, 1);
    }

    public static boolean releaseLock(String methodName, String lockName, boolean removeLock) {
        String lockKey = methodName + "-" + lockName;
        AtomicInteger stockLock = concurrentControlMap.get(lockKey);
        if (removeLock) {
            concurrentControlMap.remove(lockKey);
            return true;
        }
        if (stockLock != null) {
            return stockLock.compareAndSet(1, 0);
        }
        return false;
    }
}
