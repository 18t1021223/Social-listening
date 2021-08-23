package vn.com.sociallistening.manager.api.pojos.guest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import vn.com.sociallistening.manager.api.constraints.order.A;
import vn.com.sociallistening.manager.api.constraints.order.B;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@GroupSequence({A.class, B.class, LoginRequest.class})
public class LoginRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotEmpty(message = "USERNAME_CANNOT_BE_NULL_OR_EMPTY", groups = {A.class})
    private String username;

    @NotEmpty(message = "PASSWORD_CANNOT_BE_NULL_OR_EMPTY", groups = {B.class})
    private String password;
}
