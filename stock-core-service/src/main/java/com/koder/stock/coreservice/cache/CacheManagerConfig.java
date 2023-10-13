package com.koder.stock.coreservice.cache;

import com.google.common.cache.CacheBuilder;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.guava.GuavaCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@EnableCaching
@Configuration
public class CacheManagerConfig {
    @Bean
    public CacheManager getCacheManager() {
        CacheBuilder<Object, Object> ObjectCacheBuilder = CacheBuilder.newBuilder()
                // 存活时间（30秒内没有被访问则被移除）
                .expireAfterAccess(30, TimeUnit.SECONDS)
                // 存活时间（写入5分钟后会自动移除）
                .expireAfterWrite(1, TimeUnit.MINUTES)
                // 最大size
                .maximumSize(1000)
                // 最大并发量同时修改
                .concurrencyLevel(6)
                // 初始化大小为100个键值对
                .initialCapacity(100)
                // 变成软引用模式（在jvm内存不足时会被回收）
                .softValues();
        GuavaCacheManager cacheManager = new GuavaCacheManager();
        cacheManager.setCacheBuilder(ObjectCacheBuilder);
        return cacheManager;
    }


}
