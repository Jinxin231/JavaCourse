package io.github.kimmking.gateway.outbound.okhttp;

import io.github.kimmking.gateway.filter.HttpRequestFilter;
import io.github.kimmking.gateway.outbound.httpclient4.NamedThreadFactory;
import io.github.kimmking.gateway.router.HttpEndpointRouter;
import io.github.kimmking.gateway.router.RandomHttpEndpointRouter;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import okhttp3.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static io.netty.handler.codec.http.HttpHeaderNames.CONNECTION;
import static io.netty.handler.codec.http.HttpResponseStatus.NO_CONTENT;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class OkhttpOutboundHandler {

    private ExecutorService proxyService;
    private List<String> backendUrls;
    HttpEndpointRouter router = new RandomHttpEndpointRouter();


    public OkhttpOutboundHandler(List<String> backends){
        this.backendUrls = backends.stream().map(this::formatUrl).collect(Collectors.toList());
        int cores = Runtime.getRuntime().availableProcessors();
        long keepAliveTime = 1000;
        int queueSize = 2048;
        RejectedExecutionHandler handler = new ThreadPoolExecutor.CallerRunsPolicy();//.DiscardPolicy();
        proxyService = new ThreadPoolExecutor(cores, cores,
                keepAliveTime, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(queueSize),
                new NamedThreadFactory("proxyService"), handler);
    }


    public void handle(final FullHttpRequest fullRequest, final ChannelHandlerContext ctx, HttpRequestFilter filter) {
        String backendUrl = router.route(this.backendUrls);
        final String url = backendUrl + fullRequest.uri();
        filter.filter(fullRequest, ctx);
        proxyService.submit(()->fetchGet(fullRequest, ctx, url));

    }

    private void fetchGet(final FullHttpRequest inbound, final ChannelHandlerContext ctx, final String url) {
        OkHttpClient client = new OkHttpClient.Builder().readTimeout(5, TimeUnit.SECONDS).build();
        Request request = new Request.Builder().url(url)
                .get().build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("Fail");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    handleResponse(inbound,ctx,response.body().string());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });


    }
    private String formatUrl(String backend) {
        return backend.endsWith("/")?backend.substring(0,backend.length()-1):backend;
    }

    private void handleResponse(FullHttpRequest fullHttpRequest, ChannelHandlerContext ctx,String body) {
        FullHttpResponse fullHttpResponse = null;

        try {
            fullHttpResponse = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(body.getBytes("UTF-8")));
            fullHttpResponse.headers().set("Content-Type", "application/json");
            fullHttpResponse.headers().setInt("Content-Length", fullHttpResponse.content().readableBytes());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            fullHttpResponse = new DefaultFullHttpResponse(HTTP_1_1, NO_CONTENT);
        } finally {
            if (fullHttpRequest != null){
                if (!HttpUtil.isKeepAlive(fullHttpRequest)){
                    ctx.write(fullHttpResponse).addListener(ChannelFutureListener.CLOSE);
                }else {
                    fullHttpResponse.headers().set(CONNECTION, HttpHeaderValues.KEEP_ALIVE);
                    ctx.write(fullHttpResponse);
                }
            }
            ctx.flush();
        }

    }


    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
