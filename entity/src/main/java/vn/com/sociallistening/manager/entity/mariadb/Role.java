package vn.com.sociallistening.manager.entity.mariadb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(
        name = "roles",
        indexes = {
                @Index(columnList = "name"),
                @Index(columnList = "username"),
                @Index(columnList = "userId")
        }
)
@EntityListeners(AuditingEntityListener.class)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Role implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String[] NAMES = {
            "vn.com.sociallistening.manager.roles.user"
    };
    public static final String NAME_USER = "vn.com.sociallistening.manager.roles.user";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    @Temporal(value = TemporalType.TIMESTAMP)
    @CreatedDate
    private Date createdDate;

    private String username;

    private long userId;
}
