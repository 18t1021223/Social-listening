package vn.com.sociallistening.manager.api.repository.mariadb;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.com.sociallistening.manager.entity.mongodb.Profile;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Profile findByUsernameIgnoreCase(String username);

    boolean existsByFacebookUrlContaining(String url);

    boolean existsByFacebookId(String id);
}
