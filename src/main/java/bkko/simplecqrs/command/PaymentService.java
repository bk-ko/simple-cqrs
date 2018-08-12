package bkko.simplecqrs.command;

import bkko.simplecqrs.common.ConfigProperties;
import bkko.simplecqrs.common.Converter;
import bkko.simplecqrs.domain.Order;
import bkko.simplecqrs.domain.Payment;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static bkko.simplecqrs.domain.EventType.PAYMENT_COMPLETED;
import static bkko.simplecqrs.domain.EventType.PAYMENT_REQUESTED;

@Service
public class PaymentService {

    private KafkaTemplate template;
    private ConfigProperties properties;
    private Converter converter;

    public PaymentService(KafkaTemplate template, ConfigProperties properties, Converter converter) {
        this.template = template;
        this.properties = properties;
        this.converter = converter;
    }

    @KafkaListener(topics = "${kafka.topic}", groupId = "PAYMENT_SERVICE")
    public void listen(String orderMessage) {
        Order order = converter.toOrder(orderMessage);
        handle(order);
    }

    private void handle(Order order) {
        if (order.getType() == PAYMENT_REQUESTED) {
            final Order processedOrder =
                Order.builder()
                     .id(order.getId())
                     .type(PAYMENT_COMPLETED)
                     .payment(new Payment(order.getId() + "_" + PAYMENT_COMPLETED.toString()))
                     .build();

            template.send(properties.getTopic(),
                          order.getId(),
                          converter.toJsonString(processedOrder));
        }
    }

}
