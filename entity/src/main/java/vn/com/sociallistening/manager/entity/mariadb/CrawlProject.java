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
        name = "crawl_projects",
        indexes = {
                @Index(columnList = "type"),
                @Index(columnList = "status")
        }
)
@EntityListeners(AuditingEntityListener.class)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CrawlProject implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String TYPE_FACEBOOK = "facebook";
    public static final String TYPE_TWITTER = "twitter";
    public static final String TYPE_YOUTUBE = "youtube";
    public static final String TYPE_INSTAGRAM = "instagram";

    public static final int STATUS_PENDING = 0;
    public static final int STATUS_RUNNING = 1;
    public static final int STATUS_FINISHED = 2;
    public static final int STATUS_ERROR = 3;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String type;

    @Column(length = 1500)
    private String url;

    private boolean inQueue;

    @Temporal(value = TemporalType.TIMESTAMP)
    @CreatedDate
    private Date createdDate;

    @CreatedBy
    private String createdBy;

    private int status;

    @Temporal(value = TemporalType.TIMESTAMP)
    @LastModifiedDate
    private Date lastModifiedDate;

    @LastModifiedBy
    private String lastModifiedBy;
}
