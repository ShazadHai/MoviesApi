package com.reactivespring.repository;

import com.reactivespring.domain.MovieInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import java.util.List;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;


@DataMongoTest
@ActiveProfiles("test")
class MovieInfoRepositoryIntgTest {

    @Autowired
    MovieInfoRepository movieInfoRepository;

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
    void findAll(){
        var movieInfoFlux  = movieInfoRepository.findAll().log();
        StepVerifier.create(movieInfoFlux).expectNextCount(3).verifyComplete();
    }
    @Test
    void findById(){
        var movieInfoMono  = movieInfoRepository.findById("abc").log();
        StepVerifier.create(movieInfoMono).assertNext(movieInfo -> {
            assertEquals("Dark Knight Rises", movieInfo.getName());
        }).verifyComplete();

    }

    @Test
    void saveMovieInfo(){

        var moiveInfo = new MovieInfo("23d", "salo",
                2007, List.of("nadeem", "ali"),LocalDate.parse("2001-03-04"));

        var movieInfoMono  = movieInfoRepository.save(moiveInfo).log();
        StepVerifier.create(movieInfoMono).assertNext(movieInfo -> {
            assertNotNull(movieInfo.getMovieInfoId());
            assertEquals("salo", movieInfo.getName());
        }).verifyComplete();

    }
    @Test
    void updateMovieInfo(){

        var moiveInfo = movieInfoRepository.findById("abc").block();
        moiveInfo.setYear(2021);

        var movieInfoMono  = movieInfoRepository.save(moiveInfo).log();
        StepVerifier.create(movieInfoMono).assertNext(movieInfo -> {

            assertEquals(2021, movieInfo.getYear());
        }).verifyComplete();

    }


}