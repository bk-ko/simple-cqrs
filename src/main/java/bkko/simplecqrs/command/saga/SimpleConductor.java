package bkko.simplecqrs.command.saga;

import bkko.simplecqrs.common.ConfigProperties;
import bkko.simplecqrs.common.Converter;
import bkko.simplecqrs.domain.Mail;
import bkko.simplecqrs.domain.Order;
import bkko.simplecqrs.domain.Payment;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static bkko.simplecqrs.domain.EventType.MAIL_REQUESTED;
import static bkko.simplecqrs.domain.EventType.ORDER_COMPLETED;
import static bkko.simplecqrs.domain.EventType.PAYMENT_COMPLETED;
import static bkko.simplecqrs.domain.EventType.PAYMENT_REQUESTED;

@Service
public class SimpleConductor {

    private KafkaTemplate template;
    private ConfigProperties properties;
    private Converter converter;

    public SimpleConductor(KafkaTemplate template, ConfigProperties properties, Converter converter) {
        this.template = template;
        this.properties = properties;
        this.converter = converter;
    }

    @KafkaListener(topics = "${kafka.topic}", groupId = "CONDUCTOR")
    public void listen(String orderMessage) {
        Order order = converter.toOrder(orderMessage);
        simpleDecide(order);
    }

    private void simpleDecide(Order order) {
        if (order.getType() == ORDER_COMPLETED) {
            final Order processedOrder =
                Order.builder()
                     .id(order.getId())
                     .type(PAYMENT_REQUESTED)
                     .payment(new Payment(order.getId() + PAYMENT_REQUESTED.toString()))
                     .build();

            sendMessage(order, processedOrder);

        } else if (order.getType() == PAYMENT_COMPLETED) {
            final Order processedOrder =
                Order.builder()
                     .id(order.getId())
                     .type(MAIL_REQUESTED)
                     .mail(new Mail(order.getId() + MAIL_REQUESTED.toString()))
                     .build();

            sendMessage(order, processedOrder);
        }
    }

    private void sendMessage(Order order, Order processedOrder) {
        template.send(properties.getTopic(),
                      order.getId(),
                      converter.toJsonString(processedOrder));
    }
}
