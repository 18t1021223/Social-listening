package vn.com.sociallistening.manager.api.pojos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.springframework.data.domain.Sort;

import java.io.Serializable;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MetaData implements Serializable {
    static final long serialVersionUID = 1L;

    private int page;

    private int size;

    private long totalElements;

    private int totalPages;

    private Sort.Direction direction;

    private String field;
}
