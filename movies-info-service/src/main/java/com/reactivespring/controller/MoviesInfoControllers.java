package com.reactivespring.controller;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.service.MovieInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1")
public class MoviesInfoControllers {
    @Autowired
    private MovieInfoService movieInfoService;

    public MoviesInfoControllers(MovieInfoService movieInfoService) {
        this.movieInfoService = movieInfoService;
    }

    @GetMapping("/movieinfo")
    public Flux<MovieInfo> getAllMovies(){
        return movieInfoService.getAllMovies().log();
    }

    @GetMapping("/movieinfo/{id}")
    public Mono<MovieInfo> getMoviesInfoByID(@PathVariable String id){
        return movieInfoService.getMoviesInfoByID(id).log();
    }

    @PostMapping("/movieinfo")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<MovieInfo> addMovieInfo(@RequestBody @Valid MovieInfo movieInfo){
        return movieInfoService.addMovieInfo(movieInfo).log() ;
    }

    @PutMapping("/movieinfo/{id}")
    public Mono<ResponseEntity<MovieInfo>> updateMovieInfo(@RequestBody MovieInfo updateMovieInfo, @PathVariable String id){
        return movieInfoService.updateMovieInfo(updateMovieInfo, id)
                .map(movieInfo -> ResponseEntity.ok().body(movieInfo) )
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()))
                .log() ;
    }

    @DeleteMapping("/movieinfo/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteMovieInfo( @PathVariable String id){
        return movieInfoService.deleteMovieInfo( id).log() ;
    }
}

