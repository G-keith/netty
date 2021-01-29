package com.keith.netty.protocoltcp;

import lombok.Data;

/**
 *协议包
 * @author keith
 * @version 1.0
 * @date 2021-01-29
 */
@Data
public class MessageProtocol {

    private int len;

    private byte[] content;
}
