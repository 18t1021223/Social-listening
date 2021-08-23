package vn.com.sociallistening.manager.entity.mongodb;

import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;

@Data
@Document(collection = "Comment")
public class Comment {
    public static final String COMMENT_FANPAGE = "comment_fanpage";
    public static final String COMMENT_GROUP = "comment_group";
    public static final String COMMENT_PROFILE = "comment_profile";

    @Id
    private String id;

    @Indexed
    private String facebookOwnerId;

    @Indexed
    private String facebookOwnerUrl;

    @Indexed
    private String commentId;

    @Indexed
    private String parentCommentId;

    private String commentDate;

    private String likeCount;

    private String contentText;

    private String contentImage;

    private String contentTextImage;
}
