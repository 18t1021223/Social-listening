package vn.com.sociallistening.manager.api.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.com.sociallistening.manager.api.repository.mariadb.CrawlProjectRepository;
import vn.com.sociallistening.manager.entity.mariadb.CrawlProject;

@Service
@Slf4j
public class ExistsService {
    @Autowired
    private ProfileService profileService;
    @Autowired
    private GroupService groupService;
    @Autowired
    private FanpageService fanpageService;
    @Autowired
    private CrawlProjectRepository crawlProjectRepository;

    /**
     * duplicate issue
     *
     * @param url
     * @param id
     * @return
     */
    public boolean entityExists(String url, String id) {
        if (log.isDebugEnabled()) {
            log.info("url: {} ,id: {}", url, id);
        }
        if (url.toLowerCase().contains(CrawlProject.TYPE_FACEBOOK)) {
            if (crawlProjectRepository.existsByUrl(url))
                return true;
            if (id != null) {
                return profileService.existsByFacebookId(id) ||
                        groupService.existsByFacebookId(id) ||
                        fanpageService.existsByFacebookId(id);

            }
            if (url.contains("/profile.php")) {
                if (url.contains("&")) {
                    url = url.substring(0, url.indexOf("&"));
                }
            } else if (url.contains("?"))
                url = url.substring(0, url.indexOf("?"));

            url = url.substring(url.indexOf(".com") + 4);
            return profileService.existsByFacebookUrlContaining(url) ||
                    groupService.existsByFacebookUrlContaining(url) ||
                    fanpageService.existsByFacebookUrlContaining(url);

        } else if (url.toLowerCase().contains(CrawlProject.TYPE_TWITTER)) {
            //todo
            return true;
        } else if (url.toLowerCase().contains(CrawlProject.TYPE_INSTAGRAM)) {
            //todo
            return true;
        } else {
            throw new UnsupportedOperationException("UnKnow type of url " + url);
        }
    }
}
