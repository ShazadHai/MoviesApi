package com.reactivespring.router;

import com.reactivespring.handler.ReviewHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class ReviewRouter {
    @Bean
    public RouterFunction<ServerResponse> reviewRoute(ReviewHandler reviewHandler){
        return route().nest(path("/v1/reviews"),builder -> {
                builder.PUT("/{id}", request -> reviewHandler.upadteReview(request));
                }
        )
                .GET("/v1/hey", (request -> ServerResponse.ok().bodyValue("hello earth").log("in hello")))
                .POST("v1/reviews", request -> reviewHandler.addReview(request))
                .GET("v1/reviews", request -> reviewHandler.getAllReview(request) )
                .build();
    }
}
