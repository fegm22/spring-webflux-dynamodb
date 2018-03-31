package com.fgm.webflux.poc.services;

import com.fgm.webflux.poc.model.Tweet;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public interface AWSDynamoService {

    Mono<Tweet> save(Tweet tweet);

    Mono<Tweet> findById(String id);

    Mono<Void> delete(String id);

    Flux<List<Tweet>> findAll();

}
