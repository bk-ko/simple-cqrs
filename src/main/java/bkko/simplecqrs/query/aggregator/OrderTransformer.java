package bkko.simplecqrs.query.aggregator;

import bkko.simplecqrs.common.Converter;
import bkko.simplecqrs.domain.Order;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Transformer;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrderTransformer implements Transformer<String, String, KeyValue<String, String>> {

    private static final Logger log = LoggerFactory.getLogger(OrderTransformer.class);
    private final String stateStoreName;
    private final Converter converter;
    private KeyValueStore<String, String> stateStore;

    public OrderTransformer(String stateStoreName) {
        this.stateStoreName = stateStoreName;
        ObjectMapper mapper = new ObjectMapper();
        converter = new Converter(mapper);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void init(ProcessorContext context) {
        stateStore = (KeyValueStore<String, String>) context.getStateStore(stateStoreName);
    }

    @Override
    public KeyValue<String, String> transform(String key, String newValue) {
        String value;
        final String stored = stateStore.get(key);

        log.info("key={} / newValue ={}", key, newValue);

        value = mergeEvents(newValue, stored);

        stateStore.put(key, value);

        return KeyValue.pair(key, value);
    }

    @Override
    public void close() {

    }

    @Override
    @Deprecated
    public KeyValue<String, String> punctuate(long timestamp) {
        return null;
    }

    private String mergeEvents(String newValue, String stored) {
        if (stored != null) {
            Order o = converter.toOrder(stored);
            Order n = converter.toOrder(newValue);

            aggregateOrder(o, n);

            return converter.toJsonString(o);
        } else {
            Order n = converter.toOrder(newValue);
            n.addEvents(n.getType());
            return converter.toJsonString(n);
        }
    }

    private void aggregateOrder(Order oldOrder, Order newOrder) {
        if (newOrder.getDetail() != null) {
            oldOrder.setDetail(newOrder.getDetail());
        }
        if (newOrder.getPayment() != null) {
            oldOrder.setPayment(newOrder.getPayment());
        }
        if (newOrder.getMail() != null) {
            oldOrder.setMail(newOrder.getMail());
        }
        oldOrder.setType(newOrder.getType());
        oldOrder.addEvents(newOrder.getType());
    }
}
