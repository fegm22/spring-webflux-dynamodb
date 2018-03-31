package com.fgm.webflux.poc.handlers;

import com.fgm.webflux.poc.exception.PathNotFoundException;
import com.fgm.webflux.poc.exception.TweetNotFoundException;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;

class ThrowableTranslator {

    private final HttpStatus httpStatus;
    private final String message;

    private ThrowableTranslator(final Throwable throwable) {
        this.httpStatus = getStatus(throwable);
        this.message = throwable.getMessage();
    }

    private HttpStatus getStatus(final Throwable error) {
        if (error instanceof PathNotFoundException) {
            return HttpStatus.BAD_REQUEST;
        } else if (error instanceof TweetNotFoundException) {
            return HttpStatus.NOT_FOUND;
        } else {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    HttpStatus getHttpStatus() {
        return httpStatus;
    }

    String getMessage() {
        return message;
    }

    static <T extends Throwable> Mono<ThrowableTranslator> translate(final Mono<T> throwable) {
        return throwable.flatMap(error -> Mono.just(new ThrowableTranslator(error)));
    }
}
