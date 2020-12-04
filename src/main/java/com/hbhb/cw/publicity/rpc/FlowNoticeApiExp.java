package com.hbhb.cw.publicity.rpc;

import com.hbhb.cw.flowcenter.api.FlowNoticeApi;

import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author yzc
 * @since 2020-12-04
 */
@FeignClient(value = "${provider.flow-center}", url = "", path = "/flow/notice")
public interface FlowNoticeApiExp extends FlowNoticeApi {
}
