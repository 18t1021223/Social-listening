package vn.com.sociallistening.manager.api.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.Serializable;

@Service
@Slf4j
public class CacheService implements Serializable {
    private static final long serialVersionUID = 1L;

    private final CacheManager cacheManager;

    @Autowired
    public CacheService(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public synchronized void evict(String cacheName, String pattern) throws Exception {
        RedisTemplate<String, Object> redisTemplate = (RedisTemplate<String, Object>) cacheManager.getCache(cacheName).getNativeCache();
        redisTemplate.keys(pattern).forEach(s -> {
            redisTemplate.delete(s);
        });
    }
}
