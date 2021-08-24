package vn.com.sociallistening.manager.api.repository.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import vn.com.sociallistening.manager.entity.mongodb.Family;

@Repository
public interface FamilyRepository extends MongoRepository<Family, String> {
}
