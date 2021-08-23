package vn.com.sociallistening.manager.entity.mongodb;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;

@Data
@NoArgsConstructor
@Document(collection = "members")
public class Members {
    public static final String MEMBER_GROUP = "member_group";
    @Id
    private String id;

    private String memberId;

    private String avatar;

    @Indexed
    private String name;

    private String joinDate;

    @Indexed
    private String facebookId;

    @Indexed
    private String facebookUrl;

    @Indexed
    private String facebookOwnerId;
}
