package bkko.simplecqrs.command;

import bkko.simplecqrs.common.ConfigProperties;
import bkko.simplecqrs.common.Converter;
import bkko.simplecqrs.domain.Mail;
import bkko.simplecqrs.domain.Order;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static bkko.simplecqrs.domain.EventType.MAIL_COMPLETED;
import static bkko.simplecqrs.domain.EventType.MAIL_REQUESTED;

@Service
public class MailService {

    private KafkaTemplate template;
    private ConfigProperties properties;
    private Converter converter;

    public MailService(KafkaTemplate template, ConfigProperties properties, Converter converter) {
        this.template = template;
        this.properties = properties;
        this.converter = converter;
    }

    @KafkaListener(topics = "${kafka.topic}", groupId = "MAIL_SERVICE")
    public void listen(String orderMessage) {
        Order order = converter.toOrder(orderMessage);
        handle(order);
    }

    private void handle(Order order) {
        if (order.getType() == MAIL_REQUESTED) {
            final Order processedOrder =
                Order.builder()
                     .id(order.getId())
                     .type(MAIL_COMPLETED)
                     .mail(new Mail(order.getId() + "_" + MAIL_COMPLETED.toString()))
                     .build();

            template.send(properties.getTopic(),
                          order.getId(),
                          converter.toJsonString(processedOrder));
        }
    }

}
