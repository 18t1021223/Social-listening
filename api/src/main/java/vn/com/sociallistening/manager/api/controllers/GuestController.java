package vn.com.sociallistening.manager.api.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import vn.com.sociallistening.manager.api.Utils;
import vn.com.sociallistening.manager.api.pojos.guest.LoginRequest;
import vn.com.sociallistening.manager.api.services.GuestService;

@RestController
@RequestMapping(value = "/guest")
@Slf4j
public class GuestController {
    private final GuestService service;
    private final Validator validator;

    @Autowired
    public GuestController(GuestService service, @Qualifier("mvcValidator") Validator validator) {
        this.service = service;
        this.validator = validator;
    }

    @PostMapping(value = "/login")
    @ResponseBody
    public ResponseEntity<?> login(LoginRequest request, BindingResult result) {
        if (log.isDebugEnabled())
            log.debug("login - Request received:\r\n{}", request.toString());

        validator.validate(request, result);
        if (result.hasErrors()) {
            log.warn("login - Some mandatory parameters is invalid.");
            return ResponseEntity
                    .ok()
                    .body(
                            Utils.buildResponse(
                                    1,
                                    result.getFieldErrors().get(0).getDefaultMessage(),
                                    null
                            )
                    );
        }

        try {
            return ResponseEntity
                    .ok()
                    .body(
                            Utils.buildResponse(
                                    0,
                                    "SUCCESS",
                                    service.login(request)
                            )
                    );
        } catch (Exception e) {
            log.error("login - service.login error.", e);
            if (e.getMessage().equalsIgnoreCase("USERNAME_OR_PASSWORD_IS_INVALID"))
                return ResponseEntity
                        .ok()
                        .body(
                                Utils.buildResponse(
                                        1,
                                        e.getMessage(),
                                        null
                                )
                        );
            else
                return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(
                                Utils.buildResponse(
                                        99,
                                        "INTERNAL_SERVER_ERROR",
                                        null
                                )
                        );
        }
    }
}
