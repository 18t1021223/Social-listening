package vn.com.sociallistening.manager.api.pojos.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import vn.com.sociallistening.manager.api.constraints.order.A;
import vn.com.sociallistening.manager.api.constraints.order.B;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@GroupSequence({A.class, B.class, CrawlProjectCreateRequest.class})
public class CrawlProjectCreateRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotEmpty(message = "TYPE_CANNOT_BE_NULL_OR_EMPTY", groups = {A.class})
    private String type;

    @NotEmpty(message = "TYPE_CANNOT_BE_NULL_OR_EMPTY", groups = {B.class})
    private String url;
}
