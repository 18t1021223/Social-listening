package vn.com.sociallistening.manager.api.configurations;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomWebDriverConfiguration {
    @Bean
    public ChromeOptions options() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-notifications");
        return options;
    }

    @Bean(value = "Chrome")
    public WebDriver Chrome(ChromeOptions options) {
        System.setProperty("webdriver.chrome.driver", "D:\\training\\chromedriver.exe");
        return new ChromeDriver(options);
    }

//    @Bean(value = "Firefox")
//    public WebDriver Firefox(){
//       // System.setProperty("webdriver.chrome.driver", "D:\\training\\chromedriver.exe");
//        return new FirefoxDriver();
//    }
}
