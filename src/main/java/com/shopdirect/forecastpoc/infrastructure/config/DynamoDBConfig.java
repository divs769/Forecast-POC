package com.shopdirect.forecastpoc.infrastructure.config;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.util.StringUtils;
import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class DynamoDBConfig {

    public static final String TABLE = "forecast_stock";

    @Bean
    @Primary
    public AmazonDynamoDB amazonDynamoDB(@Value("${amazon.dynamodb.endpoint}") String endpoint,
                                         @Value("${amazon.dynamodb.region}") String region) {
        System.out.println("@@@@@@@@@@@ DYNAMODB ENDPOINT " + endpoint + "  region: " + region);
        return AmazonDynamoDBClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint, region))
                .build();
    }

    @Bean
    public DynamoDBMapperConfig dynamoDBMapperConfig(@Value("${amazon.dynamodb.tablePrefix}") String tablePrefix) {
        DynamoDBMapperConfig.Builder builder = new DynamoDBMapperConfig.Builder();
        if(!StringUtils.isNullOrEmpty(tablePrefix)) {
            System.out.println("@@@@@@@@@@@ DYNAMODB TABLE PREFIX " + tablePrefix);
            builder.withTableNameOverride(DynamoDBMapperConfig.TableNameOverride.withTableNamePrefix(tablePrefix));
        }
        return builder.build();
    }
}
