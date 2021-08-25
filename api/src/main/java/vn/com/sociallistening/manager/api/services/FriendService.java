package vn.com.sociallistening.manager.api.services;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.AfterSaveEvent;
import org.springframework.stereotype.Service;
import vn.com.sociallistening.manager.api.pojos.user.CrawlProjectCreateRequest;
import vn.com.sociallistening.manager.api.scenario.Processor;
import vn.com.sociallistening.manager.entity.mongodb.Friend;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static vn.com.sociallistening.manager.api.Utils.getValue;

@Service
@Slf4j
public class FriendService extends AbstractMongoEventListener<Friend> {

    @Autowired
    private ExistsService existsService;
    @Autowired
    private UserService userService;

    public List<Friend> objectToEntity(Map<String, Object> map) {
        try {
            List<Friend> response = new ArrayList<>();
            String friendOwnerUid = (String) map.get("friendOwnerUid");
            List<String> nameList = (List<String>) map.get("name");
            List<String> facebookUrlList = (List<String>) map.get("facebookUrl");
            List<String> facebookIdList = (List<String>) map.get("facebookId");
            int size = 0;
            if (facebookUrlList != null)
                size = facebookUrlList.size();
            for (int i = 0; i < size; ++i) {
                Friend friend = new Friend();
                friend.setName((String) getValue(nameList, i));
                friend.setFacebookUrl((String) getValue(facebookUrlList, i));
                friend.setFacebookId((String) getValue(facebookIdList, i));
                friend.setFriendOwnerId(friendOwnerUid);
                response.add(friend);
            }
            return response;
        } catch (Exception ex) {
            log.warn("convert friend fail {}", ex);
            return null;
        }
    }

    // url not null
    @SneakyThrows
    @Override
    public void onAfterSave(AfterSaveEvent<Friend> event) {
        Friend friend = event.getSource();
        if (!existsService.entityExists(friend.getFacebookUrl(), friend.getFacebookId())) {
            CrawlProjectCreateRequest request = new CrawlProjectCreateRequest();
            request.setType(Processor.CRAWL_PROFILE_TYPE);
            request.setUrl(friend.getFacebookUrl());
            userService.createCrawlProject(request);
        }

    }
}
