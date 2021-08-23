package vn.com.sociallistening.manager.entity.mongodb;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;

@Data
@NoArgsConstructor
@Document(collection = "friend")
public class Friend {
    public static final String FRIEND = "friend";
    @Id
    private String id;

    @Indexed
    private String name;

    @Indexed
    private String facebookUrl;

    @Indexed
    private String facebookId;

    @Indexed
    private String friendOwnerId;
}
