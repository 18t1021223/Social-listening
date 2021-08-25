package vn.com.sociallistening.manager.api.services;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.mapping.event.AfterDeleteEvent;
import org.springframework.data.mongodb.core.mapping.event.AfterSaveEvent;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.data.mongodb.core.mapping.event.BeforeDeleteEvent;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import vn.com.sociallistening.manager.api.configurations.CustomAbstractMongoEvent;
import vn.com.sociallistening.manager.api.constraints.mongo.CascadeDeleteCallback;
import vn.com.sociallistening.manager.api.constraints.mongo.CascadeSaveCallback;
import vn.com.sociallistening.manager.api.pojos.user.CrawlProjectCreateRequest;
import vn.com.sociallistening.manager.api.scenario.Processor;
import vn.com.sociallistening.manager.entity.mongodb.Post;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static vn.com.sociallistening.manager.api.Utils.getValue;

@Service
@Slf4j
public class PostService extends CustomAbstractMongoEvent<Post> {

    @Autowired
    private UserService userService;
    @Autowired
    private ExistsService existsService;
    @Autowired
    private MongoOperations mongoOperations;

    private List<Post> deletePosts = new ArrayList<>();

    public List<Post> objectToEntity(Map<String, Object> map) {
        try {
            List<Post> response = new ArrayList<>();

            List<String> feedOwnerFacebookIdList = (List<String>) map.get("feedOwnerFacebookId");
            List<String> likeCountList = (List<String>) map.get("likeCount");
            List<String> commentCountList = (List<String>) map.get("commentCount");
            List<String> shareCountList = (List<String>) map.get("shareCount");
            List<String> viewCountList = (List<String>) map.get("viewCount");
            List<String> feedUrlList = (List<String>) map.get("feedUrl");
            List<String> contentList = (List<String>) map.get("content");
            List<String> postDateList = (List<String>) map.get("postDate");
            List<String> postIdList = (List<String>) map.get("postId");
            List<String> facebookUrlList = (List<String>) map.get("facebookUrl");
            int size = 0;
            if (postIdList != null) size = postIdList.size();

            for (int i = 0; i < size; ++i) {
                Post post = new Post();
                post.setFacebookId((String) map.get("facebookId"));
                post.setFeedOwnerFacebookId((String) getValue(feedOwnerFacebookIdList, i));
                post.setFacebookUrl((String) getValue(facebookUrlList, i));
                post.setLikeCount((String) getValue(likeCountList, i));
                post.setCommentCount((String) getValue(commentCountList, i));
                post.setShareCount((String) getValue(shareCountList, i));
                post.setViewCount((String) getValue(viewCountList, i));
                post.setFeedUrl((String) getValue(feedUrlList, i));
                post.setContent((String) getValue(contentList, i));
                post.setPostDate((String) getValue(postDateList, i));
                post.setPostId((String) getValue(postIdList, i));
                response.add(post);
            }
            return response;
        } catch (Exception ex) {
            log.warn("convert post fail {}", ex);
        }
        return null;
    }


    @Override
    public void onBeforeConvert(BeforeConvertEvent<Post> event) {
        ReflectionUtils.doWithFields(event.getSource().getClass(),
                new CascadeSaveCallback(event.getSource(), mongoOperations));
    }

    // URL MAYBE NULL
    // USING ID CHECK
    @SneakyThrows
    @Override
    public void onAfterSave(AfterSaveEvent<Post> event) {
        Post post = event.getSource();
        if (!existsService.entityExists(post.getFacebookUrl(), post.getFeedOwnerFacebookId())) {
            CrawlProjectCreateRequest request = new CrawlProjectCreateRequest();
            request.setType(Processor.CRAWL_PROFILE_TYPE);
            request.setUrl(post.getFacebookUrl());
            userService.createCrawlProject(request);
        }
    }

    @Override
    public void onAfterDelete(AfterDeleteEvent<Post> event) {
        deletePosts.addAll((Collection<? extends Post>) this.getObject(event.getDocument()));
    }

    @Override
    public void onBeforeDelete(BeforeDeleteEvent<Post> event) {
        deletePosts.forEach(value -> ReflectionUtils.doWithFields(Post.class, new CascadeDeleteCallback(value, mongoOperations)));
    }
}
