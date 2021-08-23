package vn.com.sociallistening.manager.entity.mongodb;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.util.List;

@Data
@NoArgsConstructor
@Document(collection = "contact_info")
public class ContactInfo {
    public static final String CONTACT_INFO_FANPAGE = "contact_info_fanpage";
    public static final String CONTACT_INFO_PROFILE = "contact_info_profile";

    @Id
    private String id;

    @Indexed
    private String facebookId;

    private String phone;

    private String address;

    private String email;

    private List<String> socialLinks;
}
