package vn.com.sociallistening.manager.oauth;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import vn.com.sociallistening.manager.entity.mariadb.OAuthClientDetail;
import vn.com.sociallistening.manager.entity.mariadb.Profile;
import vn.com.sociallistening.manager.entity.mariadb.Role;
import vn.com.sociallistening.manager.entity.mariadb.User;
import vn.com.sociallistening.manager.oauth.repositories.OAuthClientDetailRepository;
import vn.com.sociallistening.manager.oauth.repositories.ProfileRepository;
import vn.com.sociallistening.manager.oauth.repositories.RoleRepository;
import vn.com.sociallistening.manager.oauth.repositories.UserRepository;

import java.util.Date;

@Component
@Slf4j
public class Initialization implements CommandLineRunner {
    private final PasswordEncoder passwordEncoder;
    private final OAuthClientDetailRepository oAuthClientDetailRepo;
    private final UserRepository userRepo;
    private final ProfileRepository profileRepo;
    private final RoleRepository roleRepo;

    @Autowired
    public Initialization(PasswordEncoder passwordEncoder, OAuthClientDetailRepository oAuthClientDetailRepo, UserRepository userRepo, ProfileRepository profileRepo, RoleRepository roleRepo) {
        this.passwordEncoder = passwordEncoder;
        this.oAuthClientDetailRepo = oAuthClientDetailRepo;
        this.userRepo = userRepo;
        this.profileRepo = profileRepo;
        this.roleRepo = roleRepo;
    }

    @Override
    public void run(String... strings) {
        if (oAuthClientDetailRepo.count() <= 0L) {
            String clientId = "0ZS4RIXTWcf9a0S9a059Ux42VppRpmLG"/*RandomStringUtils.randomAlphanumeric(32)*/,
                    clientSecret = "rk81o1jobMBiATh153CUY92st6PmB0AW"/*RandomStringUtils.randomAlphanumeric(32)*/;
            OAuthClientDetail oAuthClientDetail = new OAuthClientDetail();
            oAuthClientDetail.setClientId(clientId);
            oAuthClientDetail.setClientSecret("{noop}" + clientSecret);
            oAuthClientDetail.setScope("read,write");
            oAuthClientDetail.setAuthorizedGrantTypes("password,refresh_token,client_credentials,implicit,authorization_code");
            oAuthClientDetail.setAccessTokenValidity(2592000);
            oAuthClientDetail.setRefreshTokenValidity(2592000);
            oAuthClientDetail.setAutoapprove("true");
            oAuthClientDetailRepo.save(oAuthClientDetail);
            log.debug("clientId {}\r\nclientSecret {}", clientId, clientSecret);
        }

        if (userRepo.count() <= 0L) {
            User user = new User();
            user.setUsername("admin");
            user.setPassword(passwordEncoder.encode("1234567890"));
            user.setEnabled(true);
            user.setCreatedDate(new Date());
            userRepo.save(user);

            Profile profile = new Profile();
            profile.setId(user.getId());
            profile.setUsername(user.getUsername());
            profileRepo.save(profile);

            for (String s : Role.NAMES) {
                Role role = new Role();
                role.setName(s);
                role.setUsername(user.getUsername());
                role.setUserId(user.getId());
                role.setCreatedDate(new Date());
                roleRepo.save(role);
            }
        }
    }
}
