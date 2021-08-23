package vn.com.sociallistening.manager.entity.mongodb;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;

@Data
@NoArgsConstructor
@Document(collection = "likes")
public class Likes {
    public static final String LIKES = "likes";
    @Id
    private String id;

    @Indexed
    private String name;

    @Indexed
    private String facebookId;

    private String avatar;

    @Indexed
    private String facebookUrl;

    @Indexed
    private String facebookOwnerId;
}
