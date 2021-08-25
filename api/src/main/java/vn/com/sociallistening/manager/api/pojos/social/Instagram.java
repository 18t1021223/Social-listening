package vn.com.sociallistening.manager.api.pojos.social;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("instagram")
@Slf4j
public class Instagram implements SocialNetwork {

    @Override
    public List<Object> dataFilter(Map<Object, String> data) {
        return null;
    }

    @Override
    public void saveCrawlProfile(List<Object> converted, String typeConvert) {

    }
}
