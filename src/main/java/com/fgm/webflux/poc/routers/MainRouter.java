package com.fgm.webflux.poc.routers;

import com.fgm.webflux.poc.handlers.ApiHandler;
import com.fgm.webflux.poc.handlers.ErrorHandler;
import org.springframework.web.reactive.function.server.RouterFunction;

public class MainRouter {

    public static RouterFunction<?> doRoute(final ApiHandler handler, final ErrorHandler errorHandler) {
        return ApiRouter
                .doRoute(handler, errorHandler)
                .andOther(StaticRouter.doRoute());
    }
}
