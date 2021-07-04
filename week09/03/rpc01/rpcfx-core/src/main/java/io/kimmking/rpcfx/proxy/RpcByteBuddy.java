package io.kimmking.rpcfx.proxy;

import lombok.SneakyThrows;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.InvocationHandlerAdapter;

import static org.apache.el.lang.ELArithmetic.add;

/**
 * @author 13041285
 * @Description TODO
 * @date 2021/7/2-11:05
 */
//public class RpcByteBuddy extends RpcProxy implements RpcClient {
public class RpcByteBuddy extends RpcProxy implements RpcClient{

    @Override
    public <T> T create(Class<T> serviceClass, String url) {
        if (!isExit(serviceClass.getName())) {
            add(serviceClass.getName(), newProxy(serviceClass, url));
        }
        return (T) getProxy(serviceClass.getName());
    }


    @SneakyThrows
    private <T> T newProxy(Class<T> serviceClass, String url) {
        return (T) new ByteBuddy().subclass(Object.class)
                .implement(serviceClass)
                .intercept(InvocationHandlerAdapter.of(new RpcInvocationHandler(serviceClass, url)))
                .make()
                .load(RpcByteBuddy.class.getClassLoader())
                .getLoaded()
                .getDeclaredConstructor()
                .newInstance();
    }
}
