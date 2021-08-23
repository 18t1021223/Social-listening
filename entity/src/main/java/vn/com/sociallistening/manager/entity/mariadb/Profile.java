package vn.com.sociallistening.manager.entity.mariadb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(
        name = "profiles",
        indexes = {
                @Index(columnList = "username")
        }
)
@EntityListeners(AuditingEntityListener.class)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Profile implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    private long id;

    private String username;

    private String avatar;

    private String fullName;

    private String emailAddress;

    private String contactNumber;

    private boolean gAuthEnabled;

    private String gAuthSecretKey;

    @Temporal(value = TemporalType.TIMESTAMP)
    @LastModifiedDate
    private Date lastModifiedDate;
}
