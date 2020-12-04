package com.hbhb.cw.publicity.rpc;


import com.hbhb.cw.flowcenter.api.FlowTypeApi;

import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author wangxiaogang
 */
@FeignClient(value = "${provider.flow-center}", url = "", path = "/flow/type")
public interface FlowTypeApiExp extends FlowTypeApi {
}
