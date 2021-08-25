package vn.com.sociallistening.manager.api.services;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.mapping.event.AfterDeleteEvent;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.data.mongodb.core.mapping.event.BeforeDeleteEvent;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import vn.com.sociallistening.manager.api.configurations.CustomAbstractMongoEvent;
import vn.com.sociallistening.manager.api.constraints.mongo.CascadeDeleteCallback;
import vn.com.sociallistening.manager.api.constraints.mongo.CascadeSaveCallback;
import vn.com.sociallistening.manager.api.constraints.mongo.ImportValueCallback;
import vn.com.sociallistening.manager.api.repository.mongodb.GroupRepository;
import vn.com.sociallistening.manager.entity.mongodb.Comment;
import vn.com.sociallistening.manager.entity.mongodb.Group;
import vn.com.sociallistening.manager.entity.mongodb.Members;
import vn.com.sociallistening.manager.entity.mongodb.Post;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class GroupService extends CustomAbstractMongoEvent<Group> {
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private CommentService commentService;
    @Autowired
    private MongoOperations mongoOperations;

    private List<Group> deleteGroups = new ArrayList<>();

    public void save(Group group) {
        groupRepository.save(group);
    }

    public boolean existsByFacebookUrlContaining(String url) {
        return groupRepository.existsByFacebookUrlContaining(url);
    }

    public boolean existsByFacebookId(String id) {
        return groupRepository.existsByFacebookId(id);
    }

    public Group objectToEntity(Map<String, Object> map) {
        try {
            String memberCount = (String) map.get("memberCount");
            String photoCount = (String) map.get("photoCount");
            String eventCount = (String) map.get("eventCount");
            String fileCount = (String) map.get("fileCount");
            String publicGroup = (String) map.get("publicGroup");

            Group group = new Group();
            group.setFacebookId((String) map.get("facebookId"));
            group.setFacebookUrl((String) map.get("facebookUrl"));
            group.setMemberCount(memberCount == null ? 0 : Integer.parseInt(memberCount));
            group.setPhotoCount(photoCount == null ? 0 : Integer.parseInt(photoCount));
            group.setEventCount(eventCount == null ? 0 : Integer.parseInt(eventCount));
            group.setFileCount(fileCount == null ? 0 : Integer.parseInt(fileCount));
            group.setAbout((String) map.get("about"));
            group.setName((String) map.get("name"));
            group.setPublicGroup(publicGroup.startsWith("Public"));
            return group;
        } catch (Exception ex) {
            log.warn("convert group fail {}", ex);
            return null;
        }
    }

    public Group mapper(List<Object> data) {

        Group group = null;
        List<Members> Members = new ArrayList<>();
        List<Post> posts = new ArrayList<>();
        List<Comment> comments = new ArrayList<>();
        for (Object item : data) {
            if (item instanceof Group)
                group = (Group) item;
            else if (item instanceof List) {
                if (!((List) item).isEmpty() && ((List) item).get(0) instanceof Members)
                    Members.addAll((List<Members>) item);
                else if (!((List) item).isEmpty() && ((List) item).get(0) instanceof Post)
                    posts.addAll((List<Post>) item);
                else if (!((List) item).isEmpty() && ((List) item).get(0) instanceof Comment)
                    comments.addAll((List<Comment>) item);
            }
        }
        group.setMemberGroup(Members);
        /**
         * add comment to post
         */
        if (!posts.isEmpty() && !comments.isEmpty())
            posts.forEach((value) ->
                    value.setComments(commentService.getSubComment(comments, value.getPostId())));
        group.setPosts(posts);
        return group;
    }

    //CALL ON BEFORE FanpageRepository.save()
    @SneakyThrows
    @Override
    public void onBeforeConvert(BeforeConvertEvent<Group> event) {
        Group group = event.getSource();
        ReflectionUtils.doWithFields(group.getClass(), new ImportValueCallback(group));
        ReflectionUtils.doWithFields(group.getClass(), new CascadeSaveCallback(group, mongoOperations));
    }

    @Override
    public void onAfterDelete(AfterDeleteEvent<Group> event) {
        deleteGroups.addAll((Collection<? extends Group>) this.getObject(event.getDocument()));
    }

    @Override
    public void onBeforeDelete(BeforeDeleteEvent<Group> event) {
        deleteGroups.forEach(value -> ReflectionUtils.doWithFields(Group.class, new CascadeDeleteCallback(value, mongoOperations)));
    }
}
