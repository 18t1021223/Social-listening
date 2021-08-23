package vn.com.sociallistening.manager.entity.mongodb;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.util.List;

@Data
@NoArgsConstructor
@Document(collection = "education")
public class Education {
    public static final String EDUCATION = "education";
    @Id
    private String id;

    @Indexed
    private String facebookId;

    @Indexed
    private String facebookUrl;

    @Indexed
    private String name;

    private String avatarImage;

    @Indexed
    private String facebookOwnerId;

    private List<String> info;
}
