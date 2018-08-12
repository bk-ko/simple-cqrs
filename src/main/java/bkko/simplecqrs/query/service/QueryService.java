package bkko.simplecqrs.query.service;

import bkko.simplecqrs.domain.Order;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class QueryService {

    private OrderRepository repository;

    public QueryService(OrderRepository repository) {
        this.repository = repository;
    }

    public Optional<Order> getOrderById(String id) {
        return repository.findById(id);
    }
}
