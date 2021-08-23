package vn.com.sociallistening.manager.api.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import vn.com.sociallistening.manager.api.feign.OAuth2Client;
import vn.com.sociallistening.manager.api.repository.mariadb.UserRepository;
import vn.com.sociallistening.manager.api.pojos.guest.LoginRequest;
import vn.com.sociallistening.manager.entity.mariadb.User;

import java.io.Serializable;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class GuestService implements Serializable {
    private static final long serialVersionUID = 1L;

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    private final OAuth2Client oauth2Client;

    @Value("${feign.client.oauth2.client-id}")
    private String oauth2ClientId;

    @Value("${feign.client.oauth2.client-secret}")
    private String oauth2ClientSecret;

    @Autowired
    public GuestService(@Qualifier("passwordEncoder") PasswordEncoder passwordEncoder, UserRepository userRepository, OAuth2Client oauth2Client) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.oauth2Client = oauth2Client;
    }

    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE)
    public Map<String, Object> login(LoginRequest request) throws Exception {
        User user = userRepository.findByUsernameIgnoreCase(request.getUsername());
        if (user != null) {
            if (user.isEnabled()) {
                if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                    Map<String, Object> headers = new HashMap<>();
                    headers.put("Authorization", "Basic " + Base64.getEncoder().encodeToString((oauth2ClientId + ":" + oauth2ClientSecret).getBytes()));
                    Map<String, Object> oauth2Response = oauth2Client.login(headers, "password", request.getUsername().trim(), request.getPassword());

                    if (oauth2Response.containsKey("status"))
                        throw new Exception("USERNAME_OR_PASSWORD_IS_INVALID");
                    else {
                        Map<String, Object> map = new HashMap<>();
                        map.put("access_token", oauth2Response.get("access_token"));
                        map.put("token_type", oauth2Response.get("token_type"));
                        map.put("refresh_token", oauth2Response.get("refresh_token"));
                        map.put("expires_in", oauth2Response.get("expires_in"));
                        map.put("scope", oauth2Response.get("scope"));
                        map.put("organization", oauth2Response.get("organization"));
                        return map;
                    }
                } else
                    throw new Exception("USERNAME_OR_PASSWORD_IS_INVALID");
            } else
                throw new Exception("USERNAME_OR_PASSWORD_IS_INVALID");
        } else
            throw new Exception("USERNAME_OR_PASSWORD_IS_INVALID");
    }
}
