package vn.com.sociallistening.manager.api.feign;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "oauth2Client", url = "${feign.client.oauth2.url}")
public interface OAuth2Client {
    @RequestMapping(value = "/oauth/token", method = RequestMethod.POST)
    Map<String, Object> login(@RequestHeader Map<String, Object> headers,
                              @RequestParam("grant_type") String grantType,
                              @RequestParam("username") String username,
                              @RequestParam("password") String password);
}
