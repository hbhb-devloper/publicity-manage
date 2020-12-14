package com.hbhb.cw.publicity.enums;

import lombok.Getter;

/**
 * @author wangxiaogang
 */
@Getter
public enum PublicityErrorCode {
    NOT_RELEVANT_FLOW("85000", "相关流程异常"),
    ;

    private String code;

    private String message;

    PublicityErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
