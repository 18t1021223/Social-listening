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
        name = "facebook_accounts",
        indexes = {}
)
@EntityListeners(AuditingEntityListener.class)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FacebookAccount implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(columnDefinition = "varchar(50)")
    private String username;

    private String password;

    private boolean locked;

    private boolean available;

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
