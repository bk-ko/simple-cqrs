package bkko.simplecqrs.common;

import bkko.simplecqrs.domain.Order;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class Converter {
    private ObjectMapper mapper;

    public Converter(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public String toJsonString(Order order) {
        try {
            return mapper.writeValueAsString(order);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "";
    }

    public Order toOrder(String jsonString) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(jsonString, Order.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
