package io.kimmking.rpcfx.demo.consumer;

import com.alibaba.fastjson.parser.ParserConfig;
import io.kimmking.rpcfx.api.Filter;
import io.kimmking.rpcfx.api.LoadBalancer;
import io.kimmking.rpcfx.api.Router;
import io.kimmking.rpcfx.api.RpcfxRequest;
import io.kimmking.rpcfx.client.Rpcfx;
import io.kimmking.rpcfx.demo.api.Order;
import io.kimmking.rpcfx.demo.api.OrderService;
import io.kimmking.rpcfx.demo.api.User;
import io.kimmking.rpcfx.demo.api.UserService;
import io.kimmking.rpcfx.proxy.RpcByteBuddy;
import io.kimmking.rpcfx.proxy.RpcClient;
import io.kimmking.rpcfx.proxy.RpcClientCglib;
import io.kimmking.rpcfx.proxy.RpcClientJdk;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class RpcfxClientApplication {

	// 二方库
	// 三方库 lib
	// nexus, userserivce -> userdao -> user
	//

	public static void main(String[] args) {

		// UserService service = new xxx();
		// service.findById

//		UserService userServicefx = Rpcfx.create(UserService.class, "http://localhost:8080/");
//		User userfx = userServicefx.findById(1);
//		System.out.println("find user id=1 from server: " + userfx.getName());

		RpcClient jdk = new RpcClientJdk();
		UserService userService = jdk.create(UserService.class, "http://localhost:9091/");
		User user = userService.findById(1);
		System.out.println("find user id=1 from server: " + user.getName());



		RpcClientCglib cglib = new RpcClientCglib();
		OrderService orderService = cglib.create(OrderService.class,"http://localhost:9091/");
		Order order = orderService.findOrderById(19922129);
		System.out.println(String.format("find order name=%s, amount=%f",order.getName(),order.getAmount()));


//		OrderService orderService = Rpcfx.create(OrderService.class, "http://localhost:8080/");
//		Order order = orderService.findOrderById(1992129);
//		System.out.println(String.format("find order name=%s, amount=%f",order.getName(),order.getAmount()));
//
//		//
//		UserService userService2 = Rpcfx.createFromRegistry(UserService.class, "localhost:2181", new TagRouter(), new RandomLoadBalancer(), new CuicuiFilter());

//		SpringApplication.run(RpcfxClientApplication.class, args);
	}

	private static class TagRouter implements Router {
		@Override
		public List<String> route(List<String> urls) {
			return urls;
		}
	}

	private static class RandomLoadBalancer implements LoadBalancer {
		@Override
		public String select(List<String> urls) {
			return urls.get(0);
		}
	}

	@Slf4j
	private static class CuicuiFilter implements Filter {
		@Override
		public boolean filter(RpcfxRequest request) {
			log.info("filter {} -> {}", this.getClass().getName(), request.toString());
			return true;
		}
	}
}



