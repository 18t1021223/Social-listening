package vn.com.sociallistening.manager.entity.mariadb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "oauth_client_details")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OAuthClientDetail implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(columnDefinition = "char(50)")
    private String clientId;

    private String clientSecret;

    private String resourceIds;

    private String scope;

    private String authorizedGrantTypes;

    private String webServerRedirectUri;

    private String authorities;

    private int accessTokenValidity;

    private int refreshTokenValidity;

    private String additionalInformation;

    private String autoapprove;
}
