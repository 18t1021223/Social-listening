package vn.com.sociallistening.manager.entity.mongodb;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import vn.com.sociallistening.manager.entity.mongodb.order.Cascade;
import vn.com.sociallistening.manager.entity.mongodb.order.ImportValue;

import javax.persistence.Id;
import java.util.List;

@Data
@NoArgsConstructor
@Document(collection = "fanpage")
public class Fanpage {
    public static final String FANPAGE = "fanpage";

    @Id
    private String id;

    @Indexed
    private String facebookId;

    @Indexed
    private String facebookUrl;

    @Indexed
    private String name;

    private String avatar;

    private String imageCover;

    private String about;

    private String workTime;

    private String description;

    @Indexed
    private String type;

    private String impressum;

    private int checkedInCount;

    private int likeCount;

    private int followCount;

    @DBRef
    @Cascade
    @ImportValue(source = "facebookId", target = {"facebookId"})
    private ContactInfo contactInfo;

    @DBRef
    @Cascade
    @ImportValue(source = "facebookId", target = {"facebookId"})
    private List<Post> posts;
}
