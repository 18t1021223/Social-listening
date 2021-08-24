package vn.com.sociallistening.manager.api.repository.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import vn.com.sociallistening.manager.entity.mongodb.Group;

@Repository
public interface GroupRepository extends MongoRepository<Group, String> {

    boolean existsByFacebookUrlContaining(String url);

    boolean existsByFacebookId(String id);
}
