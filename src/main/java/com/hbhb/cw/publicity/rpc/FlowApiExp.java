package com.hbhb.cw.publicity.rpc;

import com.hbhb.cw.flowcenter.api.FlowApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author wangxiaogang
 */
@FeignClient(value = "${provider.flow-center}", url = "", contextId = "FlowApi", path = "/flow")
public interface FlowApiExp extends FlowApi {
}
