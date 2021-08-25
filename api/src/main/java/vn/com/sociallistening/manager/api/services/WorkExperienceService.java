package vn.com.sociallistening.manager.api.services;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.AfterSaveEvent;
import org.springframework.stereotype.Service;
import vn.com.sociallistening.manager.api.pojos.user.CrawlProjectCreateRequest;
import vn.com.sociallistening.manager.api.scenario.Processor;
import vn.com.sociallistening.manager.entity.mongodb.WorkExperience;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static vn.com.sociallistening.manager.api.Utils.getValue;


@Service
@Slf4j
public class WorkExperienceService extends AbstractMongoEventListener<WorkExperience> {
    @Autowired
    private UserService userService;
    @Autowired
    private ExistsService existsService;

    public List<WorkExperience> objectToEntity(Map<String, Object> map) {
        try {
            List<WorkExperience> response = new ArrayList<>();

            List<String> facebookIdList = (List<String>) map.get("facebookId");
            List<String> facebookUrlList = (List<String>) map.get("facebookUrl");
            List<String> avatarImageList = (List<String>) map.get("avatarImage");
            List<List<String>> infoList = (List<List<String>>) map.get("info");
            List<String> nameList = (List<String>) map.get("name");
            int size = 0;
            if (facebookIdList != null)
                size = facebookIdList.size();

            for (int i = 0; i < size; ++i) {
                WorkExperience work = new WorkExperience();
                work.setFacebookId((String) getValue(facebookIdList, i));
                work.setFacebookId((String) getValue(facebookUrlList, i));
                work.setAvatarImage((String) getValue(avatarImageList, i));
                work.setInfo((List<String>) getValue(infoList, i));
                work.setFacebookOwnerId((String) map.get("facebookOwnerId"));
                work.setName((String) getValue(nameList, i));
                response.add(work);
            }
            return response;
        } catch (Exception ex) {
            log.warn("convert WorkExperience fail {}", ex);
            return null;
        }
    }

    // url maybe null
    @SneakyThrows
    @Override
    public void onAfterSave(AfterSaveEvent<WorkExperience> event) {
        WorkExperience work = event.getSource();
        if (work.getFacebookUrl() != null) {
            if (!existsService.entityExists(work.getFacebookUrl(), work.getFacebookId())) {
                CrawlProjectCreateRequest request = new CrawlProjectCreateRequest();
                request.setType(Processor.CRAWL_PROFILE_TYPE);
                request.setUrl(work.getFacebookUrl());
                userService.createCrawlProject(request);
            }
        }
    }
}
