package io.kimmking.rpcfx.netty.common;

import lombok.Data;

/**
 * @author 13041285
 * @Description TODO
 * @date 2021/7/2-14:10
 */
@Data
public class RpcProtocol {

    /**
     * 数据大小
     */
    private int len;

    /**
     * 数据内容
     */
    private byte[] content;
}
