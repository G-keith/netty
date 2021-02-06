package com.keith;

import com.keith.demo.netty.GatewayServer;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = {"com.keith.demo.mapper"})
public class NettyApplication {

    public static void main(String[] args) {
        SpringApplication.run(NettyApplication.class, args);
        GatewayServer gatewayServer = new GatewayServer(9002);
        gatewayServer.run();
    }

}
