package vn.com.sociallistening.manager.oauth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = {"vn.com.sociallistening.manager.entity.mariadb"})
@EnableJpaRepositories(basePackages = {"vn.com.sociallistening.manager.oauth.repositories"})
public class OauthApplication {

    public static void main(String[] args) {
        SpringApplication.run(OauthApplication.class, args);
    }

}
