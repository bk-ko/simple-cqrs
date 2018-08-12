package bkko.simplecqrs.query.aggregator;

import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Transformer;
import org.apache.kafka.streams.kstream.TransformerSupplier;

public class OrderTransformerSupplier implements TransformerSupplier<String, String, KeyValue<String, String>> {

    private final String stateStoreName;

    public OrderTransformerSupplier(String stateStoreName) {
        this.stateStoreName = stateStoreName;
    }

    @Override
    public Transformer<String, String, KeyValue<String, String>> get() {
        return new OrderTransformer(stateStoreName);
    }
}