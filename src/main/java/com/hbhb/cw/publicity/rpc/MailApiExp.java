package com.hbhb.cw.publicity.rpc;

import com.hbhb.cw.messagehub.api.MailApi;

import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author wangxiaogang
 */
@FeignClient(value = "message-hub", path = "mail")
public interface MailApiExp extends MailApi {

}