package vn.com.sociallistening.manager.api.repository.mariadb;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.com.sociallistening.manager.entity.mariadb.Scenario;

@Repository
public interface ScenarioRepository extends JpaRepository<Scenario, Long> {
}
