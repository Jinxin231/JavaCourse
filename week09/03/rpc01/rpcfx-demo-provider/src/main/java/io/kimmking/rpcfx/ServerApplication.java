package io.kimmking.rpcfx;

import io.kimmking.rpcfx.api.RpcfxRequest;
import io.kimmking.rpcfx.api.RpcfxResolver;
import io.kimmking.rpcfx.api.RpcfxResponse;
import io.kimmking.rpcfx.api.ServiceProviderDesc;
import io.kimmking.rpcfx.demo.api.OrderService;
import io.kimmking.rpcfx.demo.api.UserService;
import io.kimmking.rpcfx.demo.provider.DemoResolver;
import io.kimmking.rpcfx.demo.provider.OrderServiceImpl;
import io.kimmking.rpcfx.demo.provider.UserServiceImpl;
import io.kimmking.rpcfx.server.RpcNettyServer;
import io.kimmking.rpcfx.server.RpcfxInvoker;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.net.InetAddress;

/**
 * @author 13041285
 * @Description TODO
 * @date 2021/7/2-19:16
 */
@Slf4j
@SpringBootApplication
public class ServerApplication implements ApplicationRunner {

    private final RpcNettyServer rpcNettyServer;

    public ServerApplication(RpcNettyServer rpcNettyServer) {
        this.rpcNettyServer = rpcNettyServer;
    }

    public static void main(String[] args) throws Exception {

//		// start zk client
//		RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
//		CuratorFramework client = CuratorFrameworkFactory.builder().connectString("localhost:2181").namespace("rpcfx").retryPolicy(retryPolicy).build();
//		client.start();


        // register service
        // xxx "io.kimmking.rpcfx.demo.api.UserService"
//
//		String userService = "io.kimking.rpcfx.demo.api.UserService";
//		registerService(client, userService);
//		String orderService = "io.kimking.rpcfx.demo.api.OrderService";
//		registerService(client, orderService);


        // ???????????????????????????spring????????????????????????????????????????????????bean??????????????????zk

        SpringApplication.run(ServerApplication.class, args);
    }


//    @Autowired
//    RpcfxInvoker invoker;
//
//    @PostMapping("/")
//    public RpcfxResponse invoke(@RequestBody RpcfxRequest request) {
//        return invoker.invoke(request);
//    }
//
//    @Bean
//    public RpcfxInvoker createInvoker(@Autowired RpcfxResolver resolver){
//        return new RpcfxInvoker(resolver);
//    }
//
//    @Bean
//    public RpcfxResolver createResolver(){
//        return new DemoResolver();
//    }


    // ????????????name
    //

    // annotation


    @Bean(name = "io.kimmking.rpcfx.demo.api.UserService")
    public UserService createUserService(){
        return new UserServiceImpl();
    }

    @Bean(name = "io.kimmking.rpcfx.demo.api.OrderService")
    public OrderService createOrderService(){
        return new OrderServiceImpl();
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        try {
            rpcNettyServer.run();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            rpcNettyServer.destroy();
        }
    }

    private static void registerService(CuratorFramework client, String service) throws Exception {
//        ServiceProviderDesc userServiceSesc = ServiceProviderDesc.builder()
//                .host(InetAddress.getLocalHost().getHostAddress())
//                .port(8080).serviceClass(service).build();
//        // String userServiceSescJson = JSON.toJSONString(userServiceSesc);
//
//        try {
//            if ( null == client.checkExists().forPath("/" + service)) {
//                client.create().withMode(CreateMode.PERSISTENT).forPath("/" + service, "service".getBytes());
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//
//        client.create().withMode(CreateMode.EPHEMERAL).
//                forPath( "/" + service + "/" + userServiceSesc.getHost() + "_" + userServiceSesc.getPort(), "provider".getBytes());
    }
}
