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
import vn.com.sociallistening.manager.api.repository.mongodb.FanpageRepository;
import vn.com.sociallistening.manager.entity.mongodb.Comment;
import vn.com.sociallistening.manager.entity.mongodb.ContactInfo;
import vn.com.sociallistening.manager.entity.mongodb.Fanpage;
import vn.com.sociallistening.manager.entity.mongodb.Post;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class FanpageService extends CustomAbstractMongoEvent<Fanpage> {
    @Autowired
    private FanpageRepository fanpageRepository;
    @Autowired
    private CommentService commentService;
    @Autowired
    private MongoOperations mongoOperations;

    private List<Fanpage> deleteFanpages = new ArrayList<>();

    public Fanpage save(Fanpage fanpage) {
        return fanpageRepository.save(fanpage);
    }

    public boolean existsByFacebookUrlContaining(String url) {
        return fanpageRepository.existsByFacebookUrlContaining(url);
    }

    public boolean existsByFacebookId(String id) {
        return fanpageRepository.existsByFacebookId(id);
    }


    public Fanpage objectToEntity(Map<String, Object> map) {
        try {
            String workTime1 = (String) map.get("workTime1");
            String workTime2 = (String) map.get("workTime2");
            String followCount = (String) map.get("followCount");
            String likeCount = (String) map.get("likeCount");
            String checkedInCount = (String) map.get("checkedInCount");

            Fanpage fanpage = null;
            fanpage.setFacebookId((String) map.get("facebookId"));
            fanpage.setFacebookUrl((String) map.get("facebookUrl"));
            fanpage.setType((String) map.get("type"));
            fanpage.setAvatar((String) map.get("avatar"));
            fanpage.setImageCover((String) map.get("imageCover"));
            fanpage.setName((String) map.get("name"));
            fanpage.setAbout((String) map.get("about"));
            fanpage.setWorkTime(workTime2 == null ? workTime1 : workTime2);
            fanpage.setDescription((String) map.get("description"));
            fanpage.setImpressum((String) map.get("impressum"));
            fanpage.setLikeCount(Integer.parseInt(likeCount == null ?
                    "0" :
                    likeCount.substring(0, likeCount.indexOf(" ")).replaceAll("[,.]", "")));
            fanpage.setFollowCount(Integer.parseInt(followCount == null ?
                    "0" :
                    followCount.substring(0, followCount.indexOf(" ")).replaceAll("[,.]", "")));
            fanpage.setCheckedInCount(Integer.parseInt(checkedInCount == null ?
                    "0" :
                    checkedInCount.substring(0, checkedInCount.indexOf(" ")).replaceAll("[,.]", "")));
            return fanpage;
        } catch (Exception ex) {
            log.warn("convert fanpage fail {}", ex);
        }
        return null;
    }

    public Fanpage mapper(List<Object> data) {

        Fanpage fanpage = null;
        ContactInfo contact = null;
        List<Post> posts = new ArrayList<>();
        List<Comment> comments = new ArrayList<>();
        for (Object item : data) {
            if (item instanceof List) {
                if (!((List) item).isEmpty() && ((List) item).get(0) instanceof Post)
                    posts.addAll((List<Post>) item);
                else if (!((List) item).isEmpty() && ((List) item).get(0) instanceof Comment)
                    comments.addAll((List<Comment>) item);
            } else if (item instanceof Fanpage)
                fanpage = (Fanpage) item;
            else if (item instanceof ContactInfo)
                contact = (ContactInfo) item;
            else if (item instanceof List)
                posts.addAll((List<Post>) item);
        }
        fanpage.setContactInfo(contact);
        /**
         * add comment to post
         */
        if (!posts.isEmpty() && !comments.isEmpty())
            posts.forEach((value) ->
                    value.setComments(commentService.getSubComment(comments, value.getPostId())));
        fanpage.setPosts(posts);
        return fanpage;
    }


    @SneakyThrows
    @Override
    public void onBeforeConvert(BeforeConvertEvent<Fanpage> event) {
        Fanpage fanpage = event.getSource();
        ReflectionUtils.doWithFields(fanpage.getClass(), new ImportValueCallback(fanpage));
        ReflectionUtils.doWithFields(fanpage.getClass(), new CascadeSaveCallback(fanpage, mongoOperations));
    }

    @Override
    public void onAfterDelete(AfterDeleteEvent<Fanpage> event) {
        deleteFanpages.addAll((Collection<? extends Fanpage>) this.getObject(event.getDocument()));
    }

    @Override
    public void onBeforeDelete(BeforeDeleteEvent<Fanpage> event) {
        deleteFanpages.forEach(value -> ReflectionUtils.doWithFields(Fanpage.class, new CascadeDeleteCallback(value, mongoOperations)));
    }
}
