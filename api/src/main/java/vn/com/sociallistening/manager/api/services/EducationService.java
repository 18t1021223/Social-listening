package vn.com.sociallistening.manager.api.services;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.AfterSaveEvent;
import org.springframework.stereotype.Service;
import vn.com.sociallistening.manager.api.pojos.user.CrawlProjectCreateRequest;
import vn.com.sociallistening.manager.api.scenario.Processor;
import vn.com.sociallistening.manager.entity.mongodb.Education;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static vn.com.sociallistening.manager.api.Utils.getValue;

@Service
@Slf4j
public class EducationService extends AbstractMongoEventListener<Education> {
    @Autowired
    private ExistsService existsService;
    @Autowired
    private UserService userService;

    public List<Education> objectToEntity(Map<String, Object> map) {
        try {
            List<Education> response = new ArrayList<>();

            List<String> facebookIdList = (List<String>) map.get("facebookId");
            List<String> facebookUrlList = (List<String>) map.get("facebookUrl");
            List<String> avatarImageList = (List<String>) map.get("avatarImage");
            /**
             *   value (List) infoList
             */
            List<List<String>> infoList = (List<List<String>>) map.get("info");
            List<String> nameList = (List<String>) map.get("name");
            int size = 0;
            if (facebookIdList != null)
                size = facebookIdList.size();

            for (int i = 0; i < size; ++i) {
                Education education = new Education();
                education.setFacebookId((String) getValue(facebookIdList, i));
                education.setFacebookId((String) getValue(facebookUrlList, i));
                education.setAvatarImage((String) getValue(avatarImageList, i));
                education.setInfo((List<String>) getValue(infoList, i));
                education.setFacebookOwnerId((String) map.get("facebookOwnerId"));
                education.setName((String) getValue(nameList, i));
                response.add(education);
            }

            return response;
        } catch (Exception ex) {
            log.warn("convert education fail {}", ex);
            return null;
        }
    }

    // URL MAYBE NULL
    @SneakyThrows
    @Override
    public void onAfterSave(AfterSaveEvent<Education> event) {
        Education education = event.getSource();
        if (education.getFacebookUrl() != null) {
            if (!existsService.entityExists(education.getFacebookUrl(), education.getId())) {
                CrawlProjectCreateRequest request = new CrawlProjectCreateRequest();
                request.setUrl(education.getFacebookUrl());
                request.setType(Processor.CRAWL_PROFILE_TYPE);
                userService.createCrawlProject(request);
            }
        }
    }
}
