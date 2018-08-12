package bkko.simplecqrs.command;

import bkko.simplecqrs.common.ConfigProperties;
import bkko.simplecqrs.common.Converter;
import bkko.simplecqrs.domain.Order;
import bkko.simplecqrs.domain.OrderDetail;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static bkko.simplecqrs.domain.EventType.ORDER_COMPLETED;
import static bkko.simplecqrs.domain.EventType.ORDER_REQUESTED;

@Service
public class OrderService {

    private KafkaTemplate template;
    private ConfigProperties properties;
    private Converter converter;

    public OrderService(KafkaTemplate template, ConfigProperties properties, Converter converter) {
        this.template = template;
        this.properties = properties;
        this.converter = converter;
    }

    @KafkaListener(topics = "${kafka.topic}", groupId = "ORDER_SERVICE")
    public void listen(String orderMessage) {
        Order order = converter.toOrder(orderMessage);
        handle(order);
    }

    private void handle(Order order) {
        if (order.getType() == ORDER_REQUESTED) {
            final Order processedOrder =
                Order.builder()
                     .id(order.getId())
                     .type(ORDER_COMPLETED)
                     .detail(new OrderDetail(order.getId() + "_" + ORDER_COMPLETED.toString()))
                     .build();

            template.send(properties.getTopic(),
                          order.getId(),
                          converter.toJsonString(processedOrder));
        }
    }

}
