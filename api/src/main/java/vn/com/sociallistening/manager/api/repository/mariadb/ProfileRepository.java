package vn.com.sociallistening.manager.api.repository.mariadb;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.com.sociallistening.manager.entity.mariadb.Profile;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Profile findByUsernameIgnoreCase(String username);
}
