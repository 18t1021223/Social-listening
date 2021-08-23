package vn.com.sociallistening.manager.api.repository.mariadb;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.com.sociallistening.manager.entity.mariadb.FacebookAccount;

@Repository
public interface FacebookAccountRepository extends JpaRepository<FacebookAccount, Long> {
}
