package com.example.demo;

import java.util.List;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ExampleAPI {

  public static void main(String[] args1) throws InterruptedException {

//    Flux.range(0, 100)
//        .map(String::valueOf)
//        .flatMap(value ->
//                validateIsNumber(value)
//                        .map(values -> )
//                        .thenReturn(value)
//        );
//    }
  }


  static Mono<List<Integer>> validateIsNumber(String value) {

    // some check here
    return null;
  }
}
