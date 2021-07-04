package io.kimmking.rpcfx.proxy;

/**
 * @author jinx
 * @Description TODO
 * @date 2021/7/2-10:08
 */
public interface RpcClient {

    /**
     * create proxy
     * @param serviceClass service class
     * @param url server url
     * @param <T> T
     * @return proxy class
     */
    <T> T create(final Class<T> serviceClass, final String url);
}
