package com.keith.demo.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author keith
 * @version 1.0
 * @date 2021-02-06
 */
@Data
public class GatewayChannel {

    private String gatewayCode;

    private String channelId;

    private Date bindingTime;
}
