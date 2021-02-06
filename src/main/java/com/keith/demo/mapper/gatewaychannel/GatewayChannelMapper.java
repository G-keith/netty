package com.keith.demo.mapper.gatewaychannel;

import com.keith.demo.entity.GatewayChannel;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @author keith
 * @version 1.0
 * @date 2021-02-06
 */
@Repository
public interface GatewayChannelMapper {

    /**
     * 插入网关和通道关系
     * @param channelId 通道id
     * @param gatewayCode 网关编号
     */
    void insertGatewayChannel(@Param("channelId") String channelId, @Param("gatewayCode") String gatewayCode);

    /**
     * 删除网关和通道关系
     * @param channelId 通道id
     */
    void deleteGatewayChannel(String channelId);

    /**
     * 查询网关和通道关系
     * @param gatewayCode 网关编号
     * @return 网关和通道绑定信息
     */
    GatewayChannel findGatewayChannel(String gatewayCode);

    /**
     * 更新网关和通道关系
     * @param channelId 通道id
     * @param gatewayCode 网关编号
     */
    void updateGatewayCode(@Param("channelId") String channelId, @Param("gatewayCode") String gatewayCode);
}
