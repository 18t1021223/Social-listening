package vn.com.sociallistening.manager.api.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class ConsumerService {

    /**
     * receive data from kafka
     * continue crawl profile
     * @param message
     * @throws IOException
     */
    @KafkaListener(topics = "vn.com.sociallistening.topics.crawl_project",groupId = "crawl_project")
    public void consumer(String message) throws IOException {
        // String.format("consume handle: %s", new ObjectMapper().readValue(message, Profile.class))
        log.info(message);
    }
}
