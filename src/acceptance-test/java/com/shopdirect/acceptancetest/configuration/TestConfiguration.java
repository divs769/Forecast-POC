package com.shopdirect.acceptancetest.configuration;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.local.main.ServerRunner;
import com.amazonaws.services.dynamodbv2.local.server.DynamoDBProxyServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(scanBasePackages = {"com.shopdirect.acceptancetest"})
public class TestConfiguration {

    public static final String STOCK_DATA_TB = "forecast_stock";

    @Bean("testClient")
    public AmazonDynamoDB dynamoDB(@Value("${amazon.dynamodb.endpoint}") String endpoint,
                                   @Value("${amazon.dynamodb.region}") String region) throws Exception {
        final String[] localArgs = {"-inMemory"};
        final DynamoDBProxyServer server = ServerRunner.createServerFromCommandLineArgs(localArgs);
        server.start();
        AmazonDynamoDBClient client = (AmazonDynamoDBClient) AmazonDynamoDBClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint, region))
                .build();
        Runtime.getRuntime().addShutdownHook(new ServerShutdownHook(server));
        return client;
    }

    private static class ServerShutdownHook extends Thread {
        private final DynamoDBProxyServer server;

        private ServerShutdownHook(DynamoDBProxyServer server) {
            this.server = server;
        }

        @Override
        public void run() {
            try {
                server.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}