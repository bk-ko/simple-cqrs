package bkko.simplecqrs;

import bkko.simplecqrs.common.CommonConfiguration;
import bkko.simplecqrs.common.ConfigProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@EnableMongoRepositories
@SpringBootApplication
@Import({CommonConfiguration.class, ConfigProperties.class})
public class SimpleCqrsApplication {

    public static void main(String[] args) {
        SpringApplication.run(SimpleCqrsApplication.class, args);
    }
}
