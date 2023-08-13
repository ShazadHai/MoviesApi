package com.reactivespring.routes;

import com.reactivespring.MoviesReviewServiceApplication;
import com.reactivespring.domain.Review;
import com.reactivespring.repository.ReviewReactiveRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test1")
@AutoConfigureWebTestClient
@ContextConfiguration(classes= MoviesReviewServiceApplication.class)
public class ReviewsIntgTest {
    @Autowired
    ReviewReactiveRepository reviewReactiveRepository;

    @Autowired
    WebTestClient webTestClient;
    static public String  REVIEW_URL = "/v1/reviews";

    @BeforeEach
    void setUp() {

        var reviewList = List.of(
                new Review(null, 1L,"Awesome Movie",9.0 ),
                new Review(null, 2L,"goood ",8.0 ),
                new Review(null, 3L,"great",7.0 )
        );

        reviewReactiveRepository.saveAll(reviewList)
                .blockLast();
    }

    @AfterEach
    void tearDown() {
        reviewReactiveRepository.deleteAll().block();
    }

    @Test
    void addReview() {
        var review =  new Review(null, 1L,"Awesome Movie",9.0 );

        webTestClient
                .post()
                .uri(REVIEW_URL)
                .bodyValue(review)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(Review.class)
                .consumeWith(reviewEntityExchangeResult -> {
                    var savedReview = reviewEntityExchangeResult.getResponseBody();
                    assert savedReview != null;
                    assert savedReview.getReviewId() != null;

                });


    }

    @Test
    void getAllMovies(){
        webTestClient.get().uri(REVIEW_URL)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(Review.class)
                .hasSize(3);
    }
}
