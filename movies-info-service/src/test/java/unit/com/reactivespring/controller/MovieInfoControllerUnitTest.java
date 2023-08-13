package com.reactivespring.controller;


import com.reactivespring.MoviesInfoServiceApplication;
import com.reactivespring.domain.MovieInfo;
import com.reactivespring.service.MovieInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

//import static reactor.core.publisher.Mono.when;

@WebFluxTest(controllers = MoviesInfoControllers.class)
@AutoConfigureWebTestClient
@ContextConfiguration(classes= MoviesInfoServiceApplication.class)
public class MovieInfoControllerUnitTest {
    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private MovieInfoService movieInfoService;

    static String Url = "/v1/movieinfo";
    static List movieinfos = List.of(
            new MovieInfo(null, "Fight Club",
                    1999, List.of("Edward Norton", "Brad pitt"),  LocalDate.parse("2005-06-15")),
            new MovieInfo(null, "Gone Girl",
                    2008, List.of("pike", "Emily", "Ben Affleck"), LocalDate.parse("2008-07-18")),
            new MovieInfo("abc", "pulp friction",
                    2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20")));


    @Test
    void getAllMovies(){

        when(movieInfoService.getAllMovies()).thenReturn(Flux.fromIterable(movieinfos));

        webTestClient.get().uri(Url)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .hasSize(3);
    }

    @Test
    void getMovieByID(){
        String movieId = "abc";

        when(movieInfoService.getMoviesInfoByID(isA(String.class))).thenReturn(Mono.just(
                new MovieInfo("abc", "pulp friction",
                        2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20"))
        ));

        webTestClient.get().uri(Url+"/{id}",movieId)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var obj = movieInfoEntityExchangeResult.getResponseBody();
                    assert obj != null;
                    assertNotNull(obj);
                    assertEquals(obj.getName(), "pulp friction");

                });
    }

    @Test
    void addMovieInfo() {

        when(movieInfoService.addMovieInfo(isA(MovieInfo.class))).thenReturn(Mono.just(
                new MovieInfo("abc", "pulp friction",
                        2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20"))
        ));

        var movieData = new MovieInfo("abc", "pulp friction",
                2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20"));
        webTestClient.post()
                .uri("/v1/movieinfo")
                .bodyValue(movieData)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var obj = movieInfoEntityExchangeResult.getResponseBody();
                    assert obj != null;
                    assert obj.getMovieInfoId() != null ;

                })
        ;

    }


    @Test
    void addMovieInfo_valid() {

        when(movieInfoService.addMovieInfo(isA(MovieInfo.class))).thenReturn(Mono.just(
                new MovieInfo("abc", "pulp friction",
                        2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20"))
        ));

        var movieData = new MovieInfo("abc", "pulp friction",
                -2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20"));
        webTestClient.post()
                .uri("/v1/movieinfo")
                .bodyValue(movieData)
                .exchange()
                .expectStatus()
                .isBadRequest()
        ;

    }

    @Test
    void updateMovieInfo() {
        String movieId = "abc";
        when(movieInfoService.updateMovieInfo(isA(MovieInfo.class), isA(String.class))).thenReturn(Mono.just(
                new MovieInfo(movieId, "pulp friction",
                        2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20"))
        ));

        var movieData = new MovieInfo(null, "pulp friction",
                2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20"));

        webTestClient.put()
                .uri(Url+"/{id}",movieId)
                .bodyValue(movieData)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var obj = movieInfoEntityExchangeResult.getResponseBody();
                    assert obj != null;
                    assert obj.getMovieInfoId() != null ;
                    assertEquals("pulp friction", obj.getName());

                })
        ;

    }
    
}
