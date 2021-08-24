package vn.com.sociallistening.manager.api.repository.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import vn.com.sociallistening.manager.entity.mongodb.WorkExperience;

@Repository
public interface WorkExperienceRepository extends MongoRepository<WorkExperience, String> {
}
