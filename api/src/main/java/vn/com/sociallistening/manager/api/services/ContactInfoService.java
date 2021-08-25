package vn.com.sociallistening.manager.api.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import vn.com.sociallistening.manager.entity.mongodb.ContactInfo;

import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class ContactInfoService {

    public ContactInfo objectToEntity(Map<String, Object> map) {
        try {
            List<String> socialLinks = (List<String>) map.get("socialLinks");
            ContactInfo info = new ContactInfo();
            info.setAddress((String) map.get("address"));
            info.setEmail((String) map.get("email"));
            info.setPhone((String) map.get("phone"));
            info.setFacebookId((String) map.get("fanpageId"));
            info.setSocialLinks(socialLinks);
            return info;
        } catch (Exception ex) {
            log.warn("convert Contact info fail {}", ex);
        }
        return null;
    }
}
