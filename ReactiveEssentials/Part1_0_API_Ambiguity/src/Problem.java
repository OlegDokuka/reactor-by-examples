import io.netty.channel.Channel;
import org.reactivestreams.Publisher;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.Connection;

public class Problem {

	public static void main(String[] args) {
		connect("http://test.test")
				.subscribe(new BaseSubscriber<Connection>() {
					@Override
					protected void hookOnNext(Connection value) {
						System.out.println(value);
					}
				});
	}


	static Publisher<Connection> connect(String endoint) {
		return Mono.just(new Connection() {
			@Override
			public Channel channel() {
				return null;
			}

			@Override
			public String toString() {
				return "Connection[address=" + endoint + "]";
			}
		});
	}
}
