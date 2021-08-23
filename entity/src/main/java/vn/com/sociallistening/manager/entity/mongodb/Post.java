package vn.com.sociallistening.manager.entity.mongodb;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import vn.com.sociallistening.manager.entity.mongodb.order.Cascade;

import javax.persistence.Id;
import java.util.List;

@Data
@NoArgsConstructor
@Document(collection = "post")
public class Post {

    public static final String POST_GROUP = "post_group";
    public static final String POST_PROFILE = "post_profile";
    public static final String POST_FANPAGE = "post_fanpage";
    @Id
    private String id;

    @Indexed
    private String facebookId;

    @Indexed
    private String feedOwnerFacebookId;

    @Indexed
    private String facebookUrl;

    @Indexed
    private String postId;

    private String likeCount;

    private String commentCount;

    private String shareCount;

    private String viewCount;

    private String feedUrl;

    private String content;

    private String postDate;

    @DBRef
    @Cascade
    private List<Comment> comments;
}
