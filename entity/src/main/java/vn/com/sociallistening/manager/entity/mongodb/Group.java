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
@Document(collection = "group")
public class Group {
    public static final String GROUP = "group";

    @Id
    private String id;

    @Indexed
    private String facebookId;

    @Indexed
    private String facebookUrl;

    private String about;

    private int memberCount;

    private int photoCount;

    private int eventCount;

    private int fileCount;

    @Indexed
    private String name;

    private boolean publicGroup;

    @DBRef
    @Cascade
    @ImportValue(source = "facebookId", target = "facebookOwnerId")
    private List<Members> memberGroup;

    @DBRef
    @Cascade
    @ImportValue(source = "facebookId", target = "facebookId")
    private List<Post> posts;
}
