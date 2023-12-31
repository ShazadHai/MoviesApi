package com.reactivespring.service;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.repository.MovieInfoRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class MovieInfoService {
    MovieInfoRepository movieInfoRepository;
    public MovieInfoService(MovieInfoRepository movieInfoRepository) {
        this.movieInfoRepository = movieInfoRepository;
    }



    public Mono<MovieInfo> addMovieInfo(MovieInfo movieInfo) {
       return movieInfoRepository.save(movieInfo);
    }

    public Flux<MovieInfo> getAllMovies() {
        return movieInfoRepository.findAll();
    }

    public Mono<MovieInfo> getMoviesInfoByID(String id) {
        return movieInfoRepository.findById(id);
    }

    public Mono<MovieInfo> updateMovieInfo(MovieInfo updateMovieInfo, String id) {
        return movieInfoRepository.findById(id).flatMap(
                moviedata -> {
                    moviedata.setName(updateMovieInfo.getName());
                    moviedata.setYear(updateMovieInfo.getYear());
                    moviedata.setCast(updateMovieInfo.getCast());
                    moviedata.setRelease_date(updateMovieInfo.getRelease_date());
                    return movieInfoRepository.save(moviedata);
                }
        );
    }

    public Mono<Void> deleteMovieInfo(String id) {
        return movieInfoRepository.deleteById(id);
    }
}
