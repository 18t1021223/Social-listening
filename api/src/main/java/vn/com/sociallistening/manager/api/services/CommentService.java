package vn.com.sociallistening.manager.api.services;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.AfterSaveEvent;
import org.springframework.stereotype.Service;
import vn.com.sociallistening.manager.api.pojos.user.CrawlProjectCreateRequest;
import vn.com.sociallistening.manager.api.scenario.Processor;
import vn.com.sociallistening.manager.entity.mongodb.Comment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static vn.com.sociallistening.manager.api.Utils.getValue;

@Service
@Slf4j
public class CommentService extends AbstractMongoEventListener<Comment> {
    @Autowired
    private ExistsService existsService;
    @Autowired
    private UserService userService;

    @SuppressWarnings("unchecked")
    public List<Comment> objectToEntity(Map<String, Object> map) {
        try {
            List<Comment> response = new ArrayList<>();
            List<String> commentDateList = (List<String>) map.get("commentDate");
            List<String> contentTextList = (List<String>) map.get("contentText");
            List<String> contentImageList = (List<String>) map.get("contentImage");
            List<String> contentTextImageList = (List<String>) map.get("contentTextImage");
            List<String> likeCountList = (List<String>) map.get("likeCount");
            List<String> facebookOwnerUrlList = (List<String>) map.get("facebookOwnerUrl");
            List<String> facebookOwnerIdList = (List<String>) map.get("facebookOwnerId");
            List<String> commentIdList = (List<String>) map.get("commentId");
            List<String> parentCommentIdList = (List<String>) map.get("parentCommentId");
            String postId = (String) map.get("postId");

            int size = 0;
            if (facebookOwnerUrlList != null)
                size = facebookOwnerUrlList.size();

            for (int i = 0; i < size; i++) {
                Comment comment = new Comment();
                comment.setCommentDate((String) getValue(commentDateList, i));
                comment.setContentText((String) getValue(contentTextList, i));
                comment.setContentImage((String) getValue(contentImageList, i));
                comment.setContentTextImage((String) getValue(contentTextImageList, i));
                comment.setLikeCount((String) getValue(likeCountList, i));
                comment.setFacebookOwnerUrl((String) getValue(facebookOwnerUrlList, i));
                comment.setFacebookOwnerId((String) getValue(facebookOwnerIdList, i));
                comment.setCommentId((String) getValue(commentIdList, i));
                comment.setParentCommentId((String) getValue(parentCommentIdList, i));
                if (comment.getCommentId().equals(comment.getParentCommentId()))
                    comment.setParentCommentId(postId);
                response.add(comment);
            }
            return response;
        } catch (Exception exception) {
            log.warn("cannot convert comment {}", exception);
        }
        return null;
    }

    public List<Comment> getSubComment(List<Comment> comments, String id) {
        List<Comment> subComments = new ArrayList<>();
        for (Comment item : comments) {
            if (item.getParentCommentId().equals(id)) {
                subComments.add(item);
                subComments.addAll(getSubComment(comments, item.getCommentId()));
            }
        }
        return subComments;
    }

    // comment url not null
    @SneakyThrows
    @Override
    public void onAfterSave(AfterSaveEvent<Comment> event) {
        Comment comment = event.getSource();
        if (!existsService.entityExists(comment.getFacebookOwnerUrl(), comment.getFacebookOwnerId())) {
            CrawlProjectCreateRequest request = new CrawlProjectCreateRequest();
            request.setUrl(comment.getFacebookOwnerUrl());
            request.setType(Processor.CRAWL_PROFILE_TYPE);
            userService.createCrawlProject(request);
        }
    }
}
