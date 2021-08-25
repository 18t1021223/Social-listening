package vn.com.sociallistening.manager.api.services;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.AfterSaveEvent;
import org.springframework.stereotype.Service;
import vn.com.sociallistening.manager.api.pojos.user.CrawlProjectCreateRequest;
import vn.com.sociallistening.manager.api.scenario.Processor;
import vn.com.sociallistening.manager.entity.mongodb.Likes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static vn.com.sociallistening.manager.api.Utils.getValue;

@Service
@Slf4j
public class LikesService extends AbstractMongoEventListener<Likes> {

    @Autowired
    private UserService userService;
    @Autowired
    private ExistsService existsService;

    public List<Likes> objectToEntity(Map<String, Object> map) {
        try {
            List<Likes> response = new ArrayList<>();
            String facebookOwnerId = (String) map.get("facebookOwnerId");
            int size = 0;
            List<String> facebookIdList = (List<String>) map.get("facebookId");
            List<String> nameList = (List<String>) map.get("name");
            List<String> avatarList = (List<String>) map.get("avatar");
            List<String> facebookUrlList = (List<String>) map.get("facebookUrl");

            if (facebookUrlList != null)
                size = facebookUrlList.size();

            for (int i = 0; i < size; ++i) {
                Likes like = new Likes();
                like.setAvatar((String) getValue(avatarList, i));
                like.setName((String) getValue(nameList, i));
                like.setFacebookUrl((String) getValue(facebookUrlList, i));
                like.setFacebookId((String) getValue(facebookIdList, i));
                like.setFacebookOwnerId(facebookOwnerId);
                response.add(like);
            }
            return response;
        } catch (Exception ex) {
            log.warn("convert likes fail {}", ex);
            return null;
        }
    }

    // URL NOT NULL
    @SneakyThrows
    @Override
    public void onAfterSave(AfterSaveEvent<Likes> event) {
        Likes like = event.getSource();
        if (!existsService.entityExists(like.getFacebookUrl(), like.getFacebookId())) {
            CrawlProjectCreateRequest request = new CrawlProjectCreateRequest();
            request.setType(Processor.CRAWL_PROFILE_TYPE);
            request.setUrl(like.getFacebookUrl());
            userService.createCrawlProject(request);
        }
    }
}
