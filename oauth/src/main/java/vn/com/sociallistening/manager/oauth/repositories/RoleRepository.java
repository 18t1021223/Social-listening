package vn.com.sociallistening.manager.oauth.repositories;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.com.sociallistening.manager.entity.mariadb.Role;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    @Cacheable(cacheNames = "role.findByUsernameIgnoreCase", key = "#p0.toLowerCase()")
    List<Role> findByUsernameIgnoreCase(String s);
}
