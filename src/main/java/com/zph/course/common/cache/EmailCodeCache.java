package com.zph.course.common.cache;

import com.google.common.base.Strings;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author zhaopenghui
 */
@Slf4j
public class EmailCodeCache {
    private static LoadingCache<String, String> localCache = CacheBuilder.newBuilder()
            .initialCapacity(1000)
            .maximumSize(10000)
            .expireAfterAccess(2, TimeUnit.MINUTES)
            .concurrencyLevel(10)
            .recordStats()
            .build(new CacheLoader<String, String>() {
                // 这个方法是默认的数据加载实现,get的时候，如果key没有对应的值，就调用这个方法进行加载、
                @Override
                public String load(String key) throws Exception {
                    return "null";
                }
            });


    public static void setKey(String key, String value) {
        localCache.put(key, value);
    }

    public static String getKey(String key) {
        if (Strings.isNullOrEmpty(key)) {
            return null;
        }
        try {
            return localCache.get(key);
        } catch (ExecutionException e) {
            log.error("getKey()方法错误", e);
        }
        return null;
    }
}
