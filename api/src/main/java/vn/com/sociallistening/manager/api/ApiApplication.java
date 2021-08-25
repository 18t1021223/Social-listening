package vn.com.sociallistening.manager.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EntityScan(basePackages = {"vn.com.sociallistening.manager.entity.mariadb"})
@EnableJpaRepositories(basePackages = {"vn.com.sociallistening.manager.api.repository"})
@EnableMongoRepositories(basePackages = {"vn.com.sociallistening.manager.api.repository"})
@EnableFeignClients
public class ApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
    }
}
