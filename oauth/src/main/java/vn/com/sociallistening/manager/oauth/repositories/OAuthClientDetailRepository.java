package vn.com.sociallistening.manager.oauth.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.com.sociallistening.manager.entity.mariadb.OAuthClientDetail;

@Repository
public interface OAuthClientDetailRepository extends JpaRepository<OAuthClientDetail, String> {
}
