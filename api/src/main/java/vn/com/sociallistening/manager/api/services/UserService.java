package vn.com.sociallistening.manager.api.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import vn.com.sociallistening.manager.api.Utils;
import vn.com.sociallistening.manager.api.pojos.MetaData;
import vn.com.sociallistening.manager.api.pojos.user.CrawlProjectCreateRequest;
import vn.com.sociallistening.manager.api.pojos.user.FacebookAccountCreateRequest;
import vn.com.sociallistening.manager.api.pojos.user.ScenarioCreateRequest;
import vn.com.sociallistening.manager.api.repository.mariadb.*;
import vn.com.sociallistening.manager.entity.mariadb.CrawlProject;
import vn.com.sociallistening.manager.entity.mariadb.FacebookAccount;
import vn.com.sociallistening.manager.entity.mariadb.Profile;
import vn.com.sociallistening.manager.entity.mariadb.Scenario;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class UserService implements Serializable {
    private final ProfileRepository profileRepository;
    private final RoleRepository roleRepository;
    private final FacebookAccountRepository facebookAccountRepository;
    private final ScenarioRepository scenarioRepository;
    private final CrawlProjectRepository crawlProjectRepository;

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    public UserService(ProfileRepository profileRepository,
                       RoleRepository roleRepository,
                       FacebookAccountRepository facebookAccountRepository,
                       ScenarioRepository scenarioRepository,
                       CrawlProjectRepository crawlProjectRepository,
                       KafkaTemplate<String, String> kafkaTemplate) {
        this.profileRepository = profileRepository;
        this.roleRepository = roleRepository;
        this.facebookAccountRepository = facebookAccountRepository;
        this.scenarioRepository = scenarioRepository;
        this.crawlProjectRepository = crawlProjectRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Cacheable(cacheNames = "user_service.getProfile", key = "#p0.toLowerCase()")
    public Map<String, Object> getProfile(String username) throws Exception {
        Profile profile = profileRepository.findByUsernameIgnoreCase(username);
        if (profile != null) {
            Map<String, Object> map = Utils.convertToMapExcludes(
                    profile,
                    new String[]{}
            );

            List<Map<String, Object>> items = new ArrayList<>();
            roleRepository.findByUsernameIgnoreCase(username).forEach(role -> {
                try {
                    Map<String, Object> item = Utils.convertToMapIncludes(
                            role,
                            new String[]{"name"}
                    );
                    items.add(item);
                } catch (Exception e) {
                    log.error("getProfile - Utils.convertToMapIncludes error.", e);
                }
            });
            map.put("roles", items);
            return map;
        } else
            return new HashMap<>();
    }

    //region facebook account
    @Cacheable(cacheNames = "user_service.getFacebookAccounts", key = "#p0.queryString.toLowerCase()")
    public Map<String, Object> getFacebookAccounts(HttpServletRequest request) throws Exception {
        Map<String, Object> map = new HashMap<>();
        MetaData meta = Utils.getMetaData(request);
        Page<FacebookAccount> page = facebookAccountRepository.findAll(
                PageRequest.of(
                        meta.getPage() - 1,
                        meta.getSize(),
                        meta.getDirection() == null ? Sort.Direction.DESC : meta.getDirection(),
                        StringUtils.isEmpty(meta.getField()) ? "createdDate" : meta.getField().trim()
                )
        );
        map.put("items", page.getContent());
        meta.setTotalElements(page.getTotalElements());
        meta.setTotalPages(page.getTotalPages());
        map.put("meta", meta);
        return map;
    }

    @CacheEvict(cacheNames = "user_service.getFacebookAccounts", allEntries = true)
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE)
    public void createFacebookAccount(FacebookAccountCreateRequest request) throws Exception {
        FacebookAccount facebookAccount = new FacebookAccount();
        facebookAccount.setUsername(request.getUsername().trim());
        facebookAccount.setPassword(request.getPassword());
        facebookAccount.setAvailable(true);
        facebookAccount.setLocked(false);
        facebookAccountRepository.save(facebookAccount);
    }
    //endregion

    //region scenarios
    @Cacheable(cacheNames = "user_service.getScenarios", key = "#p0.queryString.toLowerCase()")
    public Map<String, Object> getScenarios(HttpServletRequest request) throws Exception {
        Map<String, Object> map = new HashMap<>();
        MetaData meta = Utils.getMetaData(request);
        Page<Scenario> page = scenarioRepository.findAll(
                PageRequest.of(
                        meta.getPage() - 1,
                        meta.getSize(),
                        meta.getDirection() == null ? Sort.Direction.DESC : meta.getDirection(),
                        StringUtils.isEmpty(meta.getField()) ? "createdDate" : meta.getField().trim()
                )
        );
        map.put("items", page.getContent());
        meta.setTotalElements(page.getTotalElements());
        meta.setTotalPages(page.getTotalPages());
        map.put("meta", meta);
        return map;
    }

    @Cacheable(cacheNames = "user_service.getScenario", key = "#p0.toString()")
    public Scenario getScenario(long id) {
        return scenarioRepository.findById(id).orElse(null);
    }

    @CacheEvict(cacheNames = "user_service.getScenarios", key = "#p0.toString()")
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE)
    public void createScenario(ScenarioCreateRequest request) throws Exception {
        Scenario scenario = new Scenario();
        scenario.setType(request.getType().trim());
        scenario.setName(request.getName().trim());
        scenario.setEnabled(request.isEnabled());
        scenario.setScript(request.getScript().trim());
        scenarioRepository.save(scenario);
    }

    @CacheEvict(cacheNames = "user_service.getScenario", key = "#p0.toString()")
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE)
    public void updateScenario(long id, ScenarioCreateRequest request) throws Exception {
        Scenario scenario = scenarioRepository.findById(id).orElseGet(null);
        if (scenario != null) {
            scenario.setEnabled(request.isEnabled());
            scenario.setScript(request.getScript().trim());
            scenarioRepository.save(scenario);
        }
    }
    //endregion

    //region crawl project
    @Cacheable(cacheNames = "user_service.getCrawlProjects", condition = "#p0.queryString != null", key = "#p0.queryString")
    public Map<String, Object> getCrawlProjects(HttpServletRequest request) throws Exception {
        Map<String, Object> map = new HashMap<>();
        MetaData meta = Utils.getMetaData(request);
        Page<CrawlProject> page = crawlProjectRepository.findAll(
                PageRequest.of(
                        meta.getPage() - 1,
                        meta.getSize(),
                        meta.getDirection() == null ? Sort.Direction.DESC : meta.getDirection(),
                        StringUtils.isEmpty(meta.getField()) ? "createdDate" : meta.getField().trim()
                )
        );
        map.put("items", page.getContent());
        meta.setTotalElements(page.getTotalElements());
        meta.setTotalPages(page.getTotalPages());
        map.put("meta", meta);
        return map;
    }

    @CacheEvict(cacheNames = "user_service.getCrawlProjects", allEntries = true)
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE)
    public void createCrawlProject(CrawlProjectCreateRequest request) throws Exception {
        CrawlProject crawlProject = new CrawlProject();
        crawlProject.setType(request.getType().trim());
        crawlProject.setUrl(request.getUrl().trim());
        crawlProject.setInQueue(false);
        crawlProject.setStatus(CrawlProject.STATUS_PENDING);
        crawlProjectRepository.save(crawlProject);
    }

    @CacheEvict(cacheNames = "user_service.getCrawlProjects", allEntries = true)
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE)
    public void putCrawlProjectInToQueue(long id) throws Exception {
        CrawlProject crawlProject = crawlProjectRepository.findById(id).orElse(null);
        if (crawlProject == null) {
            log.warn("{} is invalid.", id);
            throw new Exception("ID_IS_INVALID");
        }
        if (crawlProject.isInQueue()) {
            log.warn("{} was in queue.", id);
            throw new Exception("PROJECT_WAS_IN_QUEUE");
        }
        crawlProject.setInQueue(true);
        crawlProject.setStatus(CrawlProject.STATUS_PENDING);
        crawlProjectRepository.save(crawlProject);
        kafkaTemplate.send("vn.com.sociallistening.topics.crawl_project", mapper.writeValueAsString(crawlProject));
    }
    //endregion
}
