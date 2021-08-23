package vn.com.sociallistening.manager.oauth.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import vn.com.sociallistening.manager.entity.mariadb.Role;
import vn.com.sociallistening.manager.entity.mariadb.User;
import vn.com.sociallistening.manager.oauth.repositories.RoleRepository;
import vn.com.sociallistening.manager.oauth.repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service("userDetailsService")
public class CustomUserDetailsService implements UserDetailsService {
    private static final Integer MAX_ATTEMPTS = 5;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private RoleRepository roleRepo;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user;
        try {
            user = userRepo.findByUsernameIgnoreCase(s);
        } catch (Exception e) {
            throw new RuntimeException("Database error.");
        }
        if (user == null) {
            throw new UsernameNotFoundException("Invalid username or password.");
        }
        return buildUserFromUserEntity(user);
    }

    private org.springframework.security.core.userdetails.User buildUserFromUserEntity(User user) {
        // convert model user to spring security user
        String username = user.getUsername();
        String password = user.getPassword();
        boolean enable = user.isEnabled();
        boolean accountNonExpired = true;
        boolean credentialsNonExpired = true;
        //boolean accountNonLocked      = true;
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (Role role : roleRepo.findByUsernameIgnoreCase(username)) {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        }

        org.springframework.security.core.userdetails.User springUser = new org.springframework.security.core.userdetails.User(username,
                password,
                enable,
                accountNonExpired,
                credentialsNonExpired,
                user.isEnabled(),
                authorities);
        return springUser;
    }
}
