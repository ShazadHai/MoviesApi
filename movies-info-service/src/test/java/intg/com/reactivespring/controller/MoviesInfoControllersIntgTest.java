package com.reactivespring.controller;

import com.reactivespring.MoviesInfoServiceApplication;
import com.reactivespring.domain.MovieInfo;
import com.reactivespring.repository.MovieInfoRepository;
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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test1")
@AutoConfigureWebTestClient
@ContextConfiguration(classes= MoviesInfoServiceApplication.class)

class MoviesInfoControllersIntgTest {

    String Url = "/v1/movieinfo";

    @Autowired
    MovieInfoRepository movieInfoRepository;
    @Autowired
    WebTestClient webTestClient;

    @BeforeEach
    void setUp() {

        var movieinfos = List.of(new MovieInfo(null, "Batman Begins",
                        2005, List.of("Christian Bale", "Michael Cane"),  LocalDate.parse("2005-06-15")),
                new MovieInfo(null, "The Dark Knight",
                        2008, List.of("Christian Bale", "HeathLedger"), LocalDate.parse("2008-07-18")),
                new MovieInfo("abc", "Dark Knight Rises",
                        2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20")));

        movieInfoRepository.saveAll(movieinfos)
                .blockLast();
    }

    @AfterEach
    void tearDown() {
        movieInfoRepository.deleteAll().block();
    }

    @Test
    void getAllMovies(){
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
        webTestClient.get().uri(Url+"/{id}",movieId)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
            var obj = movieInfoEntityExchangeResult.getResponseBody();
            assert obj != null;
            assertNotNull(obj);

        });
    }

    @Test
    void addMovieInfo() {
        var movieData = new MovieInfo("a", "Dark Knight Risesq",
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
    void updateMovieInfo() {
        String movieId = "sdd";
        var movieData = new MovieInfo("asd", "Dark Knight Rise123",
                2012, List.of("Christian Bale", "Tom Hardy", "shazzad"), LocalDate.parse("2012-07-20"));
        webTestClient.put()
                .uri(Url+"/"+movieId)
                .bodyValue(movieData)
                .exchange()
                .expectStatus()
                .isNotFound()
//                .expectBody(MovieInfo.class)
//                .consumeWith(movieInfoEntityExchangeResult -> {
//                    var obj = movieInfoEntityExchangeResult.getResponseBody();
//                    assert obj != null;
//                    assert obj.getMovieInfoId() != null ;
//                    assertEquals("Dark Knight Rise123", obj.getName());
//
//                })
        ;

    }

    @Test
    void DeleteMovieinfoByID() {
        String movieId = "abc";
        webTestClient.get().uri(Url + "/{id}", movieId)
                .exchange()
                .expectStatus()
                .isNoContent()
                .expectBody(Void.class);
    }
}