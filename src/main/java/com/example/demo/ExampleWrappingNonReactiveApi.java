package com.example.demo;

import io.socket.client.IO;
import io.socket.client.Socket;

import java.net.URISyntaxException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.Disposable;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.publisher.SynchronousSink;

import static java.lang.System.exit;

public class ExampleWrappingNonReactiveApi {

	static final Logger logger =
			LoggerFactory.getLogger(ExampleWrappingNonReactiveApi.class);

	static Flux<String> source = Flux.interval(Duration.ofMillis(100))
	                                 .map(String::valueOf)
	                                 .log("source");
	/*Flux
	.<String>create(sink -> {

		Socket socket;
		try {
			socket = IO.socket("https://streamer.cryptocompare.com");
			logger.info("[EXTERNAL-SERVICE] Connecting to CryptoCompare.com ...");
		}
		catch (URISyntaxException e) {
			return;
		}

		Runnable closeSocket = () -> {
			socket.close();
			logger.info("[EXTERNAL-SERVICE] Connection to CryptoCompare.com closed");
		};

		socket.on(Socket.EVENT_CONNECT, args -> {
			      String[] subscription =
					      {"5~CCCAGG~BTC~USD", "0~Coinbase~BTC~USD", "0~Cexio~BTC~USD"};
			      Map<String, Object> subs = new HashMap<>();
			      subs.put("subs", subscription);
			      socket.emit("SubAdd", subs);

		      })
		      .on("m", args -> {
			      String message = args[0].toString();
//			      String messageType = message.substring(0, message.indexOf("~"));

			      logger.info("Handled message {}", message);
			      sink.next(message);
		      })
		      .on(Socket.EVENT_ERROR, args -> {
			      // TODO: handle error
			      logger.error("Handled error {}", args);
			      sink.error((Throwable) args[0]);
		      })
		      .on(Socket.EVENT_DISCONNECT, args -> {
			      // TODO: handle disconnection
			      logger.error("Handled disconnection {}", args);
			      sink.complete();
		      });

		sink.onDispose(() -> {
			logger.info("disconnecting");
			socket.close();
		});

	})*/;

	public static void main(String[] argsss) throws InterruptedException {

		Mono.when(
				Mono.empty(),
				    Mono.just(1)
				        .delayElement(Duration.ofMillis(3000))
				        .log("inner2"))
			.log()
			.block();

//		final Flux<String> flux = source
//				.publish()
//				.refCount(5, Duration.ofMillis(10));

//
//		Sinks.Many<String> sinkMany;
//
//		sinkMany.tryEmitNext("11");


//		sinkMany.asFlux()
//				.subscribe();

//
//		Thread.sleep(1000);
//
//		final Disposable subscribe = flux.log("1")
//		                                 .subscribe();
//
//		Thread.sleep(1000);
//
//		final Disposable subscribe1 = flux.log("2")
//		                                  .subscribe();
//
//		Thread.sleep(1000);
//
//
//		subscribe.dispose();
//		Thread.sleep(1000);
//		subscribe1.dispose();
//
//		Thread.sleep(100000);
	}

}
