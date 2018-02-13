package com.shopdirect.forecastpoc.infrastructure.config;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.util.StringUtils;
import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableDynamoDBRepositories(dynamoDBMapperConfigRef = "dynamoDBMapperConfig",
        basePackages = "com.shopdirect.forecastpoc.infrastructure.repository")
public class DynamoDBConfig {

    public static String TABLE = "products";

    @Bean
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
