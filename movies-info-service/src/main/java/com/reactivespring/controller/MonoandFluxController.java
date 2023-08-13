package com.reactivespring.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
public class MonoandFluxController {
    @GetMapping("/flux")
    public Flux<Integer> simpleFlux(){
        return Flux.just(1,2,3,4).log();
    }
    @GetMapping("/mono")
    public Mono<String> helloWorld(){
        return Mono.just("hello world").log();
    }
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Long> stream(){
        return Flux.interval(Duration.ofSeconds(1)).log();
    }
}
