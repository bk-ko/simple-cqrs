package bkko.simplecqrs.query.service;

import bkko.simplecqrs.common.Converter;
import bkko.simplecqrs.domain.Order;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class PersistenceService {
    private OrderRepository repository;
    private Converter converter;

    public PersistenceService(OrderRepository repository, Converter converter) {
        this.repository = repository;
        this.converter = converter;
    }

    @KafkaListener(topics = "${kafka.view-topic}", groupId = "ORDER_SERVICE")
    public void listen(String orderMessage) {
        Order order = converter.toOrder(orderMessage);
        handle(order);
    }

    private void handle(Order order) {
        repository.save(order);
    }
}
