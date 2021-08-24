package vn.com.sociallistening.manager.api.repository.mongodb;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.com.sociallistening.manager.entity.mariadb.CrawlProject;

@Repository
public interface CrawlProjectRepository extends JpaRepository<CrawlProject, Long> {
    boolean existsByUrl(String url);
}
