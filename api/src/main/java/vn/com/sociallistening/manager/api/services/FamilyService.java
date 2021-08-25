package vn.com.sociallistening.manager.api.services;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.AfterSaveEvent;
import org.springframework.stereotype.Service;
import vn.com.sociallistening.manager.api.pojos.user.CrawlProjectCreateRequest;
import vn.com.sociallistening.manager.api.scenario.Processor;
import vn.com.sociallistening.manager.entity.mongodb.Family;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static vn.com.sociallistening.manager.api.Utils.getValue;

@Service
@Slf4j
public class FamilyService extends AbstractMongoEventListener<Family> {

    @Autowired
    private ExistsService existsService;
    @Autowired
    private UserService userService;

    public List<Family> objectToEntity(Map<String, Object> map) {
        try {
            List<Family> response = new ArrayList<>();
            String familyOwnerUid = (String) map.get("familyOwnerUid");
            List<String> nameList = (List<String>) map.get("name");
            List<String> facebookUrlList = (List<String>) map.get("facebookUrl");
            List<String> avatarList = (List<String>) map.get("avatar");
            List<String> relationList = (List<String>) map.get("relation");

            int size = 0;
            if (facebookUrlList != null)
                size = facebookUrlList.size();

            for (int i = 0; i < size; ++i) {
                Family family = new Family();
                family.setAvatar((String) getValue(avatarList, i));
                family.setName((String) getValue(nameList, i));
                family.setRelation((String) getValue(relationList, i));
                family.setFacebookUrl((String) getValue(facebookUrlList, i));
                family.setFamilyOwnerId(familyOwnerUid);
                response.add(family);
            }
            return response;
        } catch (Exception ex) {
            log.warn("convert family fail {}", ex);
            return null;
        }
    }

    // URL MAYBE NULL
    @SneakyThrows
    @Override
    public void onAfterSave(AfterSaveEvent<Family> event) {
        Family family = event.getSource();
        if (family.getFacebookUrl() != null) {
            if (!existsService.entityExists(family.getFacebookUrl(), null)) {
                CrawlProjectCreateRequest request = new CrawlProjectCreateRequest();
                request.setUrl(family.getFacebookUrl());
                request.setType(Processor.CRAWL_PROFILE_TYPE);
                userService.createCrawlProject(request);
            }
        }
    }
}
