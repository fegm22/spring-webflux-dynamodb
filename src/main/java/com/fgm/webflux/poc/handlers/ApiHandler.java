package com.fgm.webflux.poc.handlers;

import com.fgm.webflux.poc.model.Tweet;
import com.fgm.webflux.poc.services.AWSDynamoService;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.BodyInserters.fromObject;

public class ApiHandler {

    private static final String id = "id";

    private final ErrorHandler errorHandler;

    private final AWSDynamoService awsDynamoService;

    public ApiHandler(final AWSDynamoService awsDynamoService, final ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
        this.awsDynamoService = awsDynamoService;
    }

    public Mono<ServerResponse> getTweet(final ServerRequest request) {
        String tweetId = request.pathVariable(id);
        Mono<Tweet> tweetResponseMono = awsDynamoService.findById(tweetId);
        return tweetResponseMono
                .flatMap(tweet -> ServerResponse.ok().contentType(APPLICATION_JSON).body(fromObject(tweet)))
                .switchIfEmpty(ServerResponse.notFound().build())
                .onErrorResume(errorHandler::throwableError);
    }

    public Mono<ServerResponse> createTweet(final ServerRequest request) {
        Mono<Tweet> tweetMono = request.bodyToMono(Tweet.class);
        return tweetMono.doOnNext(awsDynamoService::save)
                .flatMap(tweet -> ServerResponse.ok().contentType(APPLICATION_JSON).body(fromObject(tweet)))
                .switchIfEmpty(ServerResponse.notFound().build())
                .onErrorResume(errorHandler::throwableError);
    }

    public Mono<ServerResponse> findAll(final ServerRequest request) {
        Flux<List<Tweet>> people = this.awsDynamoService.findAll();
        ParameterizedTypeReference<List<Tweet>> typeRef = new ParameterizedTypeReference<List<Tweet>>() {
        };
        return ServerResponse.ok().contentType(APPLICATION_JSON).body(people, typeRef);
    }

    public Mono<ServerResponse> delete(final ServerRequest request) {
        String tweetId = request.pathVariable(id);

        return ServerResponse.ok().build(awsDynamoService.delete(tweetId))
                .onErrorResume(errorHandler::throwableError);

    }
}