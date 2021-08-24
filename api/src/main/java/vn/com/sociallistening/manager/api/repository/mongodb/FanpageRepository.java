package vn.com.sociallistening.manager.api.repository.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import vn.com.sociallistening.manager.entity.mongodb.Fanpage;

@Repository
public interface FanpageRepository extends MongoRepository<Fanpage, String> {

    boolean existsByFacebookUrlContaining(String url);

    boolean existsByFacebookId(String id);
}
