package bkko.simplecqrs.query.aggregator;

import bkko.simplecqrs.common.ConfigProperties;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class OrderSimpleStream {

    private ConfigProperties properties;

    public OrderSimpleStream(ConfigProperties properties) {
        this.properties = properties;
        createStream();
    }

    private static KafkaStreams streams;

    public void createStream() {

        final String topic = properties.getTopic();
        final String viewTopic = properties.getViewTopic();
        final String bootstrapServers = properties.getBroker();
        final Properties streamsConfiguration = new Properties();

        streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, "QUERY-SIMPLE-STREAM");
        streamsConfiguration.put(StreamsConfig.CLIENT_ID_CONFIG, "QUERY-SIMPLE-STREAM-CLIENT");
        streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        streamsConfiguration.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        streamsConfiguration.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        streamsConfiguration.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 1 * 1000);
        streamsConfiguration.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);
        streamsConfiguration.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");

        final StreamsBuilder builder = new StreamsBuilder();
        final KStream<String, String> source = builder.stream(topic);

        final String storeName = "QUERY-SIMPLE-STORE";
        StoreBuilder<KeyValueStore<String, String>> orderStore =
            Stores.keyValueStoreBuilder(Stores.persistentKeyValueStore(storeName),
                                        Serdes.String(),
                                        Serdes.String());

        builder.addStateStore(orderStore);

        final KStream<String, String> output =
            source.transform(new OrderTransformerSupplier(storeName), storeName);

        output.to(viewTopic);

        final Topology topology = builder.build();

        streams = new KafkaStreams(topology, streamsConfiguration);

        streams.cleanUp();
        streams.start();

        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }
}
