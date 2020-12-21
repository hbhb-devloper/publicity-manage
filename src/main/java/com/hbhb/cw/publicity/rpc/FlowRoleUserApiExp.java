package com.hbhb.cw.publicity.rpc;

import com.hbhb.cw.flowcenter.api.FlowRoleUserApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author yzc
 * @since 2020-12-03
 */
@FeignClient(value = "${provider.flow-center}", url = "", contextId = "FlowRoleApi", path = "/role/user")
public interface FlowRoleUserApiExp extends FlowRoleUserApi {
}
