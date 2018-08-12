package bkko.simplecqrs.common;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "kafka")
public class ConfigProperties {

    private String broker;
    private String topic;
    private String viewTopic;
}
