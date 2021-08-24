package vn.com.sociallistening.manager.api.repository.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import vn.com.sociallistening.manager.entity.mongodb.ContactInfo;

@Repository
public interface ContactInfoRepository extends MongoRepository<ContactInfo, String> {
}
