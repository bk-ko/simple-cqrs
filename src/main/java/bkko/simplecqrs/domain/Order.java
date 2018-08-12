package bkko.simplecqrs.domain;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class Order {

    @Id
    private String id;
    private EventType type;
    private OrderDetail detail;
    private Payment payment;
    private Mail mail;
    private List<EventType> events;

    public void addEvents(EventType type) {
        if (events == null) {
            events = new ArrayList<>();
        }
        events.add(type);
    }
}