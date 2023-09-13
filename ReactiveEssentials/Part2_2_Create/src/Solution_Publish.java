import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import io.socket.client.IO;
import io.socket.client.Socket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

public class Solution_Publish {
	static final Logger logger =
			LoggerFactory.getLogger(Solution_Publish.class);

	public static void main(String[] args) throws InterruptedException {
		Flux<String> stringFlux = listenRemote();

//		ConnectableFlux<String> sharedStringFlux = stringFlux
//				.log("before_publish")
//				.publish();

//		 rule 1: make sure you are connecting
//		sharedStringFlux.connect();
//		Thread.sleep(10000);

		Flux<String> sharedStringFlux = stringFlux
				.log("before_publish")
				.publish()
				.autoConnect(1);

		sharedStringFlux
				.log("1")
				.subscribe();

		Thread.sleep(10000);

		sharedStringFlux
				.log("2")
				.subscribe();

		sharedStringFlux
				.log("3")
				.subscribe();

		Thread.sleep(1000000);
	}



	static Flux<String> listenRemote() {

		return Flux.create(sink -> {

			Socket socket;
			try {
				socket = IO.socket("https://streamer.cryptocompare.com");
				logger.info("[EXTERNAL-SERVICE] Connecting to CryptoCompare.com ...");
			} catch (URISyntaxException e) {
				sink.error(e);
				return;
			}

			socket.on(Socket.EVENT_CONNECT, args -> {
						String[] subscription =
								{"5~CCCAGG~BTC~USD", "0~Coinbase~BTC~USD", "0~Cexio~BTC~USD"};
						Map<String, Object> subs = new HashMap<>();
						subs.put("subs", subscription);
						socket.emit("SubAdd", subs);

						logger.info("[EXTERNAL-SERVICE] Connected to CryptoCompare.com ...");
					})
					.on("m", args -> {
						String message = args[0].toString();
						sink.next(message);
						logger.info("Handled message {}", message);
					})
					.on(Socket.EVENT_ERROR, args -> {
						logger.error("Handled error {}", args);
						sink.error((Throwable) args[0]);
					})
					.on(Socket.EVENT_DISCONNECT, args -> {
						logger.error("Handled disconnection {}", args);
						sink.complete();
					});


			Runnable closeSocket = () -> {
				socket.close();
				logger.info("[EXTERNAL-SERVICE] Connection to CryptoCompare.com closed");
			};

			sink.onCancel(closeSocket::run);
			socket.connect();
		});
	}
}
