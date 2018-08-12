package bkko.simplecqrs.edge;

import bkko.simplecqrs.common.ConfigProperties;
import bkko.simplecqrs.common.Converter;
import bkko.simplecqrs.domain.Order;
import bkko.simplecqrs.domain.OrderDetail;
import bkko.simplecqrs.query.service.QueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static bkko.simplecqrs.domain.EventType.ORDER_REQUESTED;

@RestController
public class EdgeController {

    private KafkaTemplate template;
    private ConfigProperties properties;
    private Converter converter;
    private QueryService queryService;

    public EdgeController(KafkaTemplate template,
                          ConfigProperties properties,
                          Converter converter,
                          QueryService queryService) {
        this.template = template;
        this.properties = properties;
        this.converter = converter;
        this.queryService = queryService;
    }

    // Maybe POST
    @GetMapping("/command")
    public ResponseEntity<Map> OrderCommand() {
        final String id = sendRequest();
        Map rMap = new LinkedHashMap();
        rMap.put("id", id);
        rMap.put("_query", "http://localhost:8080/query/" + id);
        return ResponseEntity.ok(rMap);
    }

    @GetMapping("/query/{id}")
    public ResponseEntity<?> OrderQuery(@PathVariable String id) {
        Optional<Order> order = queryService.getOrderById(id);

        if (order.isPresent()) {
            return ResponseEntity.ok(order.get());
        } else {
            return ResponseEntity.status(404)
                                 .body("{\"error\":\"no order found for " + id + "\"}");
        }
    }

    private String sendRequest() {
        final String uuid = "ORD-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();

        final Order order =
            Order.builder()
                 .id(uuid)
                 .type(ORDER_REQUESTED)
                 .detail(new OrderDetail(uuid + "_" + ORDER_REQUESTED.toString()))
                 .build();

        template.send(properties.getTopic(),
                      order.getId(),
                      converter.toJsonString(order));

        return uuid;
    }
}
