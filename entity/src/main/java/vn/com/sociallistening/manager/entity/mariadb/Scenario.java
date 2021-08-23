package vn.com.sociallistening.manager.entity.mariadb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(
        name = "scenarios",
        indexes = {
                @Index(columnList = "type"),
                @Index(columnList = "name")
        }
)
@EntityListeners(AuditingEntityListener.class)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Scenario implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String TYPE_FACEBOOK = "facebook";
    public static final String TYPE_TWITTER = "twitter";
    public static final String TYPE_YOUTUBE = "youtube";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String type;

    private String name;

    private String language;

    @Column(length = Integer.MAX_VALUE)
    private String script;

    private boolean enabled;

    @Temporal(value = TemporalType.TIMESTAMP)
    @CreatedDate
    private Date createdDate;

    @CreatedBy
    private String createdBy;

    @Temporal(value = TemporalType.TIMESTAMP)
    @LastModifiedDate
    private Date lastModifiedDate;

    @LastModifiedBy
    private String lastModifiedBy;
}
