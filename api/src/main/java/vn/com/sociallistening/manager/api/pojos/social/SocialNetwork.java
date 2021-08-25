package vn.com.sociallistening.manager.api.pojos.social;

import java.util.List;
import java.util.Map;

public interface SocialNetwork {

    List<Object> dataFilter(Map<Object, String> data);

    void saveCrawlProfile(List<Object> converted, String typeConvert);
}
