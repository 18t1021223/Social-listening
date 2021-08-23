package vn.com.sociallistening.manager.entity.mongodb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.io.Serializable;
import java.util.List;

@Document(collection = "work_experience")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WorkExperience implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String WORK = "work";
    @Id
    private String id;

    @Indexed
    private String facebookId;

    @Indexed
    private String facebookUrl;

    private List<String> info;

    private String avatarImage;

    @Indexed
    private String name;

    @Indexed
    private String facebookOwnerId;
}
