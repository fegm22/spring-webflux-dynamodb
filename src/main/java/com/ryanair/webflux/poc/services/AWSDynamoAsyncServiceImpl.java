package com.ryanair.webflux.poc.services;

import com.ryanair.webflux.poc.model.Tweet;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.dynamodb.DynamoDBAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ComparisonOperator;
import software.amazon.awssdk.services.dynamodb.model.Condition;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class AWSDynamoAsyncServiceImpl implements AWSDynamoService {

    private static final String tableName = "Tweet";
    private static DynamoDBAsyncClient client;

    public AWSDynamoAsyncServiceImpl(final DynamoDBAsyncClient client) {
        this.client = client;
    }

    @Override
    public Mono<Tweet> save(final Tweet tweet) {
        Map<String, AttributeValue> attributeValueHashMap = new HashMap<>();
        attributeValueHashMap.put("Text", AttributeValue.builder().s(tweet.getText()).build());
        attributeValueHashMap.put("Id", AttributeValue.builder().s(tweet.getId()).build());
        attributeValueHashMap.put("CreatedAt", AttributeValue.builder().s(tweet.getCreatedAt()).build());

        PutItemRequest putItemRequest = PutItemRequest.builder().tableName(tableName)
                .item(attributeValueHashMap)
                .build();

        CompletableFuture<PutItemResponse> completableFuture = client.putItem(putItemRequest);

        CompletableFuture<Tweet> tweetCompletableFuture = completableFuture.thenApplyAsync(PutItemResponse::attributes)
                .thenApplyAsync(map -> new Tweet(map.get("Id").s(), map.get("Text").s(), map.get("CreatedAt").s()));

        return Mono.fromFuture(tweetCompletableFuture);
    }

    @Override
    public Mono<Tweet> findById(final String id) {
        Map<String, AttributeValue> attributeValueHashMap = new HashMap<>();
        attributeValueHashMap.put("Id", AttributeValue.builder().s(id).build());

        GetItemRequest getItemRequest = GetItemRequest.builder()
                .tableName(tableName)
                .key(attributeValueHashMap)
                .build();

        CompletableFuture<GetItemResponse> completableFuture = client.getItem(getItemRequest);

        CompletableFuture<Tweet> tweetCompletableFuture = completableFuture.thenApplyAsync(GetItemResponse::item)
                .thenApplyAsync(map -> createTweet(map));

        return Mono.fromFuture(tweetCompletableFuture);
    }

    private Tweet createTweet(Map<String, AttributeValue> map) {
        if (map != null) {
            return new Tweet(map.get("Id").s(), map.get("Text").s(), map.get("CreatedAt").s());
        }
        return null;
    }

    @Override
    public Flux<List<Tweet>> findAll() {
        Map<String, Condition> conditionHashMap = new HashMap<>();
        Condition condition = Condition.builder()
                .comparisonOperator(ComparisonOperator.LT)
                .attributeValueList(AttributeValue.builder().s(LocalDateTime.now().toString()).build())
                .build();

        conditionHashMap.put("CreatedAt", condition);

        ScanRequest scanRequest = ScanRequest.builder().tableName(tableName)
                .scanFilter(conditionHashMap)
                .build();

        CompletableFuture<ScanResponse> future = client.scan(scanRequest);

        CompletableFuture<List<Tweet>> response =
                future.thenApplyAsync(ScanResponse::items)
                        .thenApplyAsync(list -> list.parallelStream()
                                .map(map -> new Tweet(map.get("Id").s(), map.get("Text").s(), map.get("CreatedAt").s()
                                )).collect(Collectors.toList())
                        );

        return Flux.from(Mono.fromFuture(response));
    }

    @Override
    public Mono<Void> delete(final String id) {
        Map<String, AttributeValue> attributeValueHashMap = new HashMap<>();
        attributeValueHashMap.put("Id", AttributeValue.builder().s(id).build());

        DeleteItemRequest deleteItemRequest = DeleteItemRequest.builder()
                .tableName(tableName)
                .key(attributeValueHashMap)
                .build();

        client.deleteItem(deleteItemRequest);

        return Mono.empty();
    }
}

