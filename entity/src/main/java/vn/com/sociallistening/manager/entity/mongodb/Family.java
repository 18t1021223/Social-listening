package vn.com.sociallistening.manager.entity.mongodb;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;

@Data
@NoArgsConstructor
@Document(collection = "family")
public class Family {
    public static final String FAMILY = "family";

    @Id
    private String id;

    @Indexed
    private String familyOwnerId;

    @Indexed
    private String name;

    private String relation;

    @Indexed
    private String facebookUrl;

    private String avatar;
}
