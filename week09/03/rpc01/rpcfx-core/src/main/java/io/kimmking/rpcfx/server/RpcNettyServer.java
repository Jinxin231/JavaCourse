package io.kimmking.rpcfx.server;

import io.kimmking.rpcfx.netty.common.RpcDecoder;
import io.kimmking.rpcfx.netty.common.RpcEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @author 13041285
 * @Description TODO
 * @date 2021/7/2-16:03
 */
@Slf4j
@Component
public class RpcNettyServer {
    private final ApplicationContext context;

    private EventLoopGroup boss;
    private EventLoopGroup worker;

    public RpcNettyServer(ApplicationContext context) {
        this.context = context;
    }

    public void destroy() {
        worker.shutdownGracefully();
        boss.shutdownGracefully();
    }

    public void run() throws Exception {
        boss = new NioEventLoopGroup(1);
        worker = new NioEventLoopGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer() {
                    @Override
                    protected void initChannel(Channel channel) throws Exception {
                        ChannelPipeline pipeline = channel.pipeline();
                        pipeline.addLast("Message Encoder", new RpcEncoder());
                        pipeline.addLast("Message Decoder", new RpcDecoder());
                        pipeline.addLast("Message Handler", new RpcServerHandler(context));
                    }
                });

        int port = 9091;
        Channel channel = serverBootstrap.bind(port).sync().channel();
        log.info("Netty server listen in port: " + port);
        channel.closeFuture().sync();
    }
}
