package vn.com.sociallistening.manager.entity.mongodb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import vn.com.sociallistening.manager.entity.mongodb.order.Cascade;
import vn.com.sociallistening.manager.entity.mongodb.order.ImportValue;

import javax.persistence.Id;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Document(collection = "profile")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Profile implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String PROFILE = "profile";

    @Id
    private String id;

    @Indexed
    private String facebookId;

    @Indexed
    private String facebookUrl;

    private String coverImage;

    private String avatarImage;

    @Indexed
    private String name;

    private String gender;

    private String maritalStatus;

    private String quote;

    private String about;

    private String birthDay;

    private String interestedIn;

    private String languages;

    private String religiousViews;

    private String politicalViews;

    private List<String> otherNames;

    private Map<String, Object> lifeEvents;

    private List<String> placesLived;

    @DBRef
    @Cascade
    @ImportValue(source = "facebookId", target = {"facebookId"})
    private ContactInfo contactInfo;

    @DBRef
    @Cascade
    @ImportValue(source = "facebookId", target = {"facebookOwnerId"})
    private List<Likes> likes;

    @DBRef
    @Cascade
    @ImportValue(source = "facebookId", target = {"familyOwnerId"})
    private List<Family> families;

    @DBRef
    @Cascade
    @ImportValue(source = "facebookId", target = {"friendOwnerId"})
    private List<Friend> friends;

    @DBRef
    @Cascade
    @ImportValue(source = "facebookId", target = {"facebookOwnerId"})
    private List<Education> educations;

    @DBRef
    @Cascade
    @ImportValue(source = "facebookId", target = {"facebookOwnerId"})
    private List<WorkExperience> workExperiences;

    @DBRef
    @Cascade
    @ImportValue(source = "facebookId", target = {"facebookId"})
    private List<Post> posts;
}
