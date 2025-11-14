package com.projects.ecommerce.Config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;

@Aspect
@Component
@EnableCaching
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class AutoCacheConfig {

    @Autowired
    private CacheManager cacheManager;

    @Bean
    public Caffeine<Object, Object> caffeine() {
        return Caffeine.newBuilder()
                .maximumSize(3000)
                .recordStats();
    }

    @Bean
    public CacheManager caffeineCacheManager(Caffeine<Object, Object> caffeine) {
        CaffeineCacheManager manager = new CaffeineCacheManager();
        manager.setCaffeine(caffeine);
        return manager;
    }

    @Around("execution(* com.projects.ecommerce..service..*(..)) && " +
            "!execution(* com.projects.ecommerce.Auth..*(..)) && " +
            "!execution(* org.springframework.security..*(..))")
    public Object autoCache(ProceedingJoinPoint pjp) throws Throwable {

        String cacheName = pjp.getTarget().getClass().getSimpleName();
        Cache cache = cacheManager.getCache(cacheName);

        if (cache == null) {
            return pjp.proceed();
        }

        String methodName = pjp.getSignature().getName();

        // ✅ Detect write methods
        boolean isWriteOperation = methodName.startsWith("add") ||
                methodName.startsWith("update") ||
                methodName.startsWith("delete");

        Object result = pjp.proceed();

        if (isWriteOperation) {
            // Clear the whole cache for this service
            cache.clear();
            System.out.println("⚡ Cache cleared due to write operation: " + methodName);
        } else {
            // Read operation: cache result
            String key = buildSafeKey(pjp);
            Cache.ValueWrapper cachedValue = cache.get(key);
            if (cachedValue == null) {
                cache.put(key, result);
                System.out.println("✅ Cached new value for: " + key);
            } else {
                System.out.println("✅ Returning cached value for: " + key);
            }
        }

        return result;
    }

    private String buildSafeKey(ProceedingJoinPoint pjp) {
        StringBuilder keyBuilder = new StringBuilder();
        keyBuilder.append(pjp.getSignature().toShortString());
        for (Object arg : pjp.getArgs()) {
            if (arg == null) keyBuilder.append(":null");
            else if (arg instanceof Pageable pageable) keyBuilder.append(":page=")
                    .append(pageable.getPageNumber())
                    .append(",size=")
                    .append(pageable.getPageSize());
            else if (arg instanceof Collection<?> col) keyBuilder.append(":list=").append(col.toString());
            else if (arg instanceof Boolean || arg instanceof Number || arg instanceof String)
                keyBuilder.append(":").append(arg.toString());
            else keyBuilder.append(":obj=").append(arg.hashCode());
        }
        return keyBuilder.toString();
    }
}