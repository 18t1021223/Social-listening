package vn.com.sociallistening.manager.api.services;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.AfterSaveEvent;
import org.springframework.stereotype.Service;
import vn.com.sociallistening.manager.api.pojos.user.CrawlProjectCreateRequest;
import vn.com.sociallistening.manager.api.scenario.Processor;
import vn.com.sociallistening.manager.entity.mongodb.Members;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static vn.com.sociallistening.manager.api.Utils.getValue;

@Service
@Slf4j
public class MembersService extends AbstractMongoEventListener<Members> {

    @Autowired
    private ExistsService existsService;
    @Autowired
    private UserService userService;

    public List<Members> objectToEntity(Map<String, Object> map) {
        try {
            List<Members> response = new ArrayList<>();

            List<String> memberIdList = (List<String>) map.get("memberId");
            List<String> avatarList = (List<String>) map.get("avatar");
            List<String> nameList = (List<String>) map.get("name");
            List<String> facebookUrlList = (List<String>) map.get("facebookUrl");
            List<String> facebookIdList = (List<String>) map.get("facebookId");
            List<String> joinDateList = (List<String>) map.get("joinDate");

            int size = 0;
            if (memberIdList != null) size = memberIdList.size();
            for (int i = 0; i < size; ++i) {
                Members memberGroup = new Members();
                memberGroup.setMemberId((String) getValue(memberIdList, i));
                memberGroup.setAvatar((String) getValue(avatarList, i));
                memberGroup.setName((String) getValue(nameList, i));
                memberGroup.setFacebookUrl((String) getValue(facebookUrlList, i));
                memberGroup.setFacebookId((String) getValue(facebookIdList, i));
                memberGroup.setJoinDate((String) getValue(joinDateList, i));
                memberGroup.setFacebookOwnerId((String) map.get("facebookOwnerId"));
                response.add(memberGroup);
            }
            return response;
        } catch (Exception ex) {
            log.warn("convert memberGroup fail {}", ex);
            return null;
        }
    }

    /*
     member url not null
     type : fanpage , profile , group
    */
    @SneakyThrows
    @Override
    public void onAfterSave(AfterSaveEvent<Members> event) {
        Members member = event.getSource();
        if (!existsService.entityExists(member.getFacebookUrl(), member.getFacebookId())) {
            CrawlProjectCreateRequest request = new CrawlProjectCreateRequest();
            request.setType(Processor.CRAWL_PROFILE_TYPE);
            request.setUrl(member.getFacebookUrl());
            userService.createCrawlProject(request);
        }
    }
}
