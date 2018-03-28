package com.ryanair.webflux.poc;

import com.ryanair.webflux.poc.model.Tweet;
import com.ryanair.webflux.poc.services.AWSDynamoService;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Collections;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WebfluxApplicationTests {

	@Autowired
	private WebTestClient webTestClient;

	@Autowired
	private AWSDynamoService awsDynamoService;

	@Test
	public void testCreateTweet() {
		Tweet tweet = new Tweet("This is a Test Tweet");

		webTestClient.post().uri("/tweets")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just(tweet), Tweet.class)
				.exchange()
				.expectStatus().isOk();
	}

	@Test
    public void testGetAllTweets() {
	    webTestClient.get().uri("/tweets")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    public void testGetSingleTweet() {
	    Tweet tweet = new Tweet("Hello, World!");
		Mono<Tweet> tweetMono = awsDynamoService.save(tweet);

        webTestClient.get()
                .uri("/tweets/{id}", Collections.singletonMap("id", tweet.getId()) )
				.accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
				.expectStatus().isOk()
				.expectBody()
				.consumeWith(response ->
						Assertions.assertThat(response.getResponseBody()).isNotNull());
    }


    @Test
    public void testDeleteTweet() {
		Tweet tweet = new Tweet("Tweet to delete!");
		Mono<Tweet> tweetMono = awsDynamoService.save(tweet);

	    webTestClient.delete()
                .uri("/tweets/{id}", Collections.singletonMap("id",  tweet.getId()))
                .exchange()
                .expectStatus().isOk();
    }
}
