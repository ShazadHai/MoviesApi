package com.reactivespring.handler;

import com.reactivespring.domain.Review;
import com.reactivespring.repository.ReviewReactiveRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class ReviewHandler {

    public ReviewReactiveRepository reviewReactiveRepository;

    public ReviewHandler(ReviewReactiveRepository reviewReactiveRepository) {
        this.reviewReactiveRepository = reviewReactiveRepository;
    }

    public Mono<ServerResponse> addReview(ServerRequest request) {
        return request.bodyToMono(Review.class)
                .flatMap(review -> reviewReactiveRepository.save(review))
                .flatMap(result ->
                        ServerResponse.status(HttpStatus.CREATED).bodyValue(result).log("results: "+result));
    }

    public Mono<ServerResponse> getAllReview(ServerRequest request) {
        var reviewFlux = reviewReactiveRepository.findAll();

        return  ServerResponse.status(HttpStatus.OK).body(reviewFlux, Review.class).log();
    }


    public Mono<ServerResponse> upadteReview(ServerRequest request) {
        var id = request.pathVariable("id");
        var oldReview = reviewReactiveRepository.findById(id);
        return oldReview.flatMap(oreview -> request.bodyToMono(Review.class).map(review ->
        {
            oreview.setComment(review.getComment());
            oreview.setRating(review.getRating());
            return oreview;
        })
        ).flatMap(reviewReactiveRepository::save)
                .flatMap(savedReviw -> ServerResponse.ok().bodyValue(savedReviw) );

    }
}
