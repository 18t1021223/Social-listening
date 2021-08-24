package vn.com.sociallistening.manager.oauth.configurations;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.*;

import java.time.Duration;
import java.util.Arrays;

@Configuration
@EnableCaching
public class CustomRedisClusterCacheConfiguration {
    @Value("${spring.redis.cluster.nodes}")
    private String redisClusterNodes;

    @Value("${spring.redis.cluster.max-redirects}")
    private int redisClusterMaxRedirects;

    @Bean
    public JedisConnectionFactory redisConnectionFactory() {
        RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration(Arrays.asList(redisClusterNodes.split(",")));
        redisClusterConfiguration.setMaxRedirects(redisClusterMaxRedirects);

        JedisClientConfiguration clientConfiguration = JedisClientConfiguration
                .builder().usePooling().build();
        return new JedisConnectionFactory(redisClusterConfiguration, clientConfiguration);
    }

    @Bean
    @Primary
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new JdkSerializationRedisSerializer());

        template.setHashKeySerializer(new GenericToStringSerializer<>(Object.class));
        template.setHashValueSerializer(new JdkSerializationRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    @Primary
    public CacheManager redisCacheManagerJson() {
        RedisCacheConfiguration config = RedisCacheConfiguration
                .defaultCacheConfig()
                .entryTtl(Duration.ofDays(3))
                .disableCachingNullValues()
                .prefixCacheNameWith("vn.com.sociallistening.manager.caches.")
                .serializeValuesWith(RedisSerializationContext
                        .SerializationPair
                        .fromSerializer(new GenericJackson2JsonRedisSerializer()));
        return RedisCacheManager
                .builder(redisConnectionFactory())
                .cacheDefaults(config).build();
    }

    @Bean
    public CacheManager redisCacheManagerJdk() {
        RedisCacheConfiguration config = RedisCacheConfiguration
                .defaultCacheConfig()
                .entryTtl(Duration.ofDays(3))
                .disableCachingNullValues()
                .prefixCacheNameWith("vn.com.sociallistening.manager.caches.")
                .serializeValuesWith(RedisSerializationContext
                        .SerializationPair
                        .fromSerializer(new JdkSerializationRedisSerializer()));
        return RedisCacheManager
                .builder(redisConnectionFactory())
                .cacheDefaults(config).build();
    }
}
