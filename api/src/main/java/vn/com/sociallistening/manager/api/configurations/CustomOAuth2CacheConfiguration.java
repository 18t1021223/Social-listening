package vn.com.sociallistening.manager.api.configurations;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource({"classpath:oauth2-cache.xml"})
public class CustomOAuth2CacheConfiguration {
}
