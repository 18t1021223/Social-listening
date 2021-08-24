package vn.com.sociallistening.manager.api.controllers;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vn.com.sociallistening.manager.api.Utils;
import vn.com.sociallistening.manager.api.pojos.user.CrawlProjectCreateRequest;
import vn.com.sociallistening.manager.api.pojos.user.FacebookAccountCreateRequest;
import vn.com.sociallistening.manager.api.pojos.user.ScenarioCreateRequest;
import vn.com.sociallistening.manager.api.services.UserService;

import javax.servlet.http.HttpServletRequest;


@RestController
@RequestMapping(value = "/user")
@PreAuthorize("customHasAuthority('vn.com.sociallistening.manager.roles.user')")
@Slf4j
public class UserController {
    @Autowired
    private UserService service;

    @GetMapping(value = "/profile")
    @ResponseBody
    public ResponseEntity<?> getProfile() {
        try {
            return ResponseEntity
                    .ok()
                    .body(
                            Utils.buildResponse(
                                    0,
                                    "SUCCESS",
                                    service.getProfile(SecurityContextHolder.getContext().getAuthentication().getName().trim())
                            )
                    );
        } catch (Exception e) {
            //e.printStackTrace();
            log.error("getProfile - service.getProfile error.", e);
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

    //region facebook account
    @GetMapping(value = "/facebook/accounts")
    @ResponseBody
    public ResponseEntity<?> getFacebookAccounts(HttpServletRequest servletRequest) {
        try {
            return ResponseEntity
                    .ok()
                    .body(
                            Utils.buildResponse(
                                    0,
                                    "SUCCESS",
                                    service.getFacebookAccounts(servletRequest)
                            )
                    );
        } catch (Exception e) {
            //e.printStackTrace();
            log.error("getFacebookAccounts - service.getFacebookAccounts error.", e);
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

    @PostMapping(value = "/facebook/accounts")
    @ResponseBody
    public ResponseEntity<?> createFacebookAccount(@Validated FacebookAccountCreateRequest request, BindingResult result) {
        if (log.isDebugEnabled())
            log.debug("createFacebookAccount - Request received:\r\n{}", request.toString());

        if (result.hasErrors()) {
            log.warn("createFacebookAccount - Some mandatory parameters is invalid.");
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
            service.createFacebookAccount(request);
            return ResponseEntity
                    .ok()
                    .body(
                            Utils.buildResponse(
                                    0,
                                    "SUCCESS",
                                    null
                            )
                    );
        } catch (Exception e) {
            //e.printStackTrace();
            log.error("createFacebookAccount - service.createFacebookAccount error.", e);
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
    //endregion

    //region scenarios
    @GetMapping(value = "/scenarios")
    @ResponseBody
    public ResponseEntity<?> getScenarios(HttpServletRequest servletRequest) {
        try {
            return ResponseEntity
                    .ok()
                    .body(
                            Utils.buildResponse(
                                    0,
                                    "SUCCESS",
                                    service.getScenarios(servletRequest)
                            )
                    );
        } catch (Exception e) {
            //e.printStackTrace();
            log.error("getScenarios - service.getScenarios error.", e);
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

    @PostMapping(value = "/scenarios")
    @ResponseBody
    public ResponseEntity<?> createScenario(@Validated ScenarioCreateRequest request, BindingResult result) {
        if (log.isDebugEnabled())
            log.debug("createScenario - Request received:\r\n{}", request.toString());

        if (result.hasErrors()) {
            log.warn("createScenario - Some mandatory parameters is invalid.");
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
            service.createScenario(request);
            return ResponseEntity
                    .ok()
                    .body(
                            Utils.buildResponse(
                                    0,
                                    "SUCCESS",
                                    null
                            )
                    );
        } catch (Exception e) {
            //e.printStackTrace();
            log.error("createScenario - service.createScenario error.", e);
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

    @GetMapping(value = "/scenarios/{id}")
    @ResponseBody
    public ResponseEntity<?> getScenario(@PathVariable(name = "id") long id) {
        try {
            return ResponseEntity
                    .ok()
                    .body(
                            Utils.buildResponse(
                                    0,
                                    "SUCCESS",
                                    service.getScenario(id)
                            )
                    );
        } catch (Exception e) {
            //e.printStackTrace();
            log.error("getScenario - service.getScenario error.", e);
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

    @PutMapping(value = "/scenarios/{id}")
    @ResponseBody
    public ResponseEntity<?> updateScenario(@PathVariable(name = "id") long id,
                                            @Validated ScenarioCreateRequest request,
                                            BindingResult result) {
        if (log.isDebugEnabled())
            log.debug("updateScenario - Request received:\r\n{}", request.toString());

        if (result.hasErrors()) {
            log.warn("updateScenario - Some mandatory parameters is invalid.");
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
            service.updateScenario(id, request);
            return ResponseEntity
                    .ok()
                    .body(
                            Utils.buildResponse(
                                    0,
                                    "SUCCESS",
                                    null
                            )
                    );
        } catch (Exception e) {
            //e.printStackTrace();
            log.error("updateScenario - service.updateScenario error.", e);
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
    //endregion

    //region crawl project
    @GetMapping(value = "/crawl-projects")
    @ResponseBody
    public ResponseEntity<?> getCrawlProjects(HttpServletRequest servletRequest) {
        try {
            return ResponseEntity
                    .ok()
                    .body(
                            Utils.buildResponse(
                                    0,
                                    "SUCCESS",
                                    service.getCrawlProjects(servletRequest)
                            )
                    );
        } catch (Exception e) {
            //e.printStackTrace();
            log.error("getCrawlProjects - service.getCrawlProjects error.", e);
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

    @PostMapping(value = "/crawl-projects")
    @ResponseBody
    public ResponseEntity<?> createCrawlProject(@Validated CrawlProjectCreateRequest request, BindingResult result) {
        if (log.isDebugEnabled())
            log.debug("createCrawlProject - Request received:\r\n{}", request.toString());

        if (result.hasErrors()) {
            log.warn("createCrawlProject - Some mandatory parameters is invalid.");
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
            service.createCrawlProject(request);
            return ResponseEntity
                    .ok()
                    .body(
                            Utils.buildResponse(
                                    0,
                                    "SUCCESS",
                                    null
                            )
                    );
        } catch (Exception e) {
            //e.printStackTrace();
            log.error("createCrawlProject - service.createCrawlProject error.", e);
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

    @PutMapping(value = "/crawl-projects/{id}/queue")
    @ResponseBody
    public ResponseEntity<?> putCrawlProjectInToQueue(@PathVariable(name = "id") long id) {
        if (log.isDebugEnabled())
            log.debug("putCrawlProjectInToQueue - Request received:\r\n{}", id);

        try {
            service.putCrawlProjectInToQueue(id);
            return ResponseEntity
                    .ok()
                    .body(
                            Utils.buildResponse(
                                    0,
                                    "SUCCESS",
                                    null
                            )
                    );
        } catch (Exception e) {
            //e.printStackTrace();
            if (StringUtils.equalsIgnoreCase(e.getMessage(), "ID_IS_INVALID") ||
                    StringUtils.equalsIgnoreCase(e.getMessage(), "PROJECT_WAS_IN_QUEUE")) {
                log.warn("putCrawlProjectInToQueue - Some mandatory parameters is invalid.");
                return ResponseEntity
                        .ok()
                        .body(
                                Utils.buildResponse(
                                        1,
                                        e.getMessage(),
                                        null
                                )
                        );
            } else {
                log.error("putCrawlProjectInToQueue - service.putCrawlProjectInToQueue error.", e);
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
    //endregion
}
