package vn.com.sociallistening.manager.api.repository.mariadb;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.com.sociallistening.manager.entity.mariadb.Role;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    @Cacheable(cacheNames = "role.findByUsernameIgnoreCase", key = "#p0.toLowerCase()")
    List<Role> findByUsernameIgnoreCase(String s);

    @Cacheable(cacheNames = "role.findByNameIgnoreCaseAndUsernameIgnoreCase", unless = "#result == null", key = "#p0.toLowerCase() + ':' + #p1.toLowerCase()")
    Role findByNameIgnoreCaseAndUsernameIgnoreCase(String name, String username);
}
