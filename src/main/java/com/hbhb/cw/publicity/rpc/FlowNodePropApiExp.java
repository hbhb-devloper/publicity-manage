package com.hbhb.cw.publicity.rpc;

import com.hbhb.cw.flowcenter.api.FlowNodePropApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "${provider.flow-center}", url = "", contextId = "FlowNodeProApi", path = "/flow/list")
public interface FlowNodePropApiExp extends FlowNodePropApi {
}
