package com.hbhb.cw.publicity.rpc;

import com.hbhb.cw.flowcenter.api.FlowNodePropApi;

import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author yzc
 * @since 2020-12-16
 */
@FeignClient(value = "${provider.flow-center}", url = "",contextId = "FlowNodePropApi", path = "/node/prop")
public interface FlowNodePropApiExp extends FlowNodePropApi {
}
