package vn.com.sociallistening.manager.api.repository.mariadb;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.com.sociallistening.manager.entity.mariadb.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Cacheable(cacheNames = "user.findByUsernameIgnoreCase", key = "#p0.toLowerCase()")
    User findByUsernameIgnoreCase(String s);
}
