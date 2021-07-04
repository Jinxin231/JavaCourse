package io.kimmking.rpcfx.proxy;

import io.kimmking.rpcfx.client.Rpcfx;

import java.lang.reflect.Proxy;

/**
 * @author 13041285
 * @Description TODO
 * @date 2021/7/2-10:25
 */
public class RpcClientJdk extends RpcProxy implements RpcClient{

    @Override
    public <T> T create(Class<T> serviceClass, String url) {
        // 查询是否之前生成过，存储的直接返回
        if (!isExit(serviceClass.getName())) {
            add(serviceClass.getName(), newProxy(serviceClass, url));
        }
        return (T) getProxy(serviceClass.getName());
    }

    private <T> T newProxy(Class<T> serviceClass, String url) {
        ClassLoader loader = RpcClientJdk.class.getClassLoader();

//        return (T) Proxy.newProxyInstance(loader, new Class[]{serviceClass}, new Rpcfx.RpcfxInvocationHandler(serviceClass, url, filters));
        return (T) Proxy.newProxyInstance(loader, new Class[]{serviceClass}, new RpcInvocationHandler(serviceClass, url));
    }
}
