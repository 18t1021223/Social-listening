package vn.com.sociallistening.manager.api.repository.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import vn.com.sociallistening.manager.entity.mongodb.Likes;

@Repository
public interface LikesRepository extends MongoRepository<Likes, String> {
}
