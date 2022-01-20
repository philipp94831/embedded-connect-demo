package com.bakdata.kafka;

import static net.mguenther.kafka.junit.EmbeddedConnectConfig.kafkaConnect;
import static net.mguenther.kafka.junit.EmbeddedKafkaClusterConfig.newClusterConfig;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import net.mguenther.kafka.junit.EmbeddedKafkaCluster;
import net.mguenther.kafka.junit.KeyValue;
import net.mguenther.kafka.junit.SendKeyValues;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.connect.file.FileStreamSinkConnector;
import org.apache.kafka.connect.runtime.ConnectorConfig;
import org.apache.kafka.connect.sink.SinkConnector;
import org.apache.kafka.connect.storage.StringConverter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ConnectTest {

    private static final String INPUT = "input";
    @TempDir
    File tempDir;
    private EmbeddedKafkaCluster kafkaCluster;

    @BeforeEach
    void setUp() {
        this.kafkaCluster = this.createCluster();
        this.kafkaCluster.start();
    }

    @AfterEach
    void tearDown() {
        this.kafkaCluster.stop();
    }

    @Test
    void test1() throws InterruptedException, IOException {
        this.runTest();
    }

    @Test
    void test2() throws InterruptedException, IOException {
        this.runTest();
    }

    private void runTest() throws InterruptedException, IOException {
        this.kafkaCluster.send(SendKeyValues.to(INPUT, Collections.singletonList(new KeyValue<>("key", "value")))
                .with(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class)
                .with(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class)
                .build());
        // makes sure that records are processed
        Thread.sleep(Duration.ofSeconds(2).toMillis());
        final List<String> output = Files.readAllLines(this.getFile().toPath());
        assertThat(output).containsExactly("value");
    }

    private File getFile() {
        return new File(this.tempDir, "out.txt");
    }

    private EmbeddedKafkaCluster createCluster() {
        return EmbeddedKafkaCluster.provisionWith(newClusterConfig()
                .configure(
                        kafkaConnect()
                                .deployConnector(this.config())
                                .build()
                ).build());
    }

    private Properties config() {
        final Properties properties = new Properties();
        properties.put(ConnectorConfig.NAME_CONFIG, "test");
        properties.put(ConnectorConfig.CONNECTOR_CLASS_CONFIG, FileStreamSinkConnector.class.getName());
        properties.put(SinkConnector.TOPICS_CONFIG, INPUT);
        properties.put(FileStreamSinkConnector.FILE_CONFIG, this.getFile().getAbsolutePath());
        properties.put(ConnectorConfig.KEY_CONVERTER_CLASS_CONFIG, StringConverter.class.getName());
        properties.put(ConnectorConfig.VALUE_CONVERTER_CLASS_CONFIG, StringConverter.class.getName());
        return properties;
    }
}
