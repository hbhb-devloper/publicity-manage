package com.hbhb.cw.publicity.enums;


import lombok.Getter;

/**
 * @author yzc
 * @since 2020-11-25
 */
@Getter
public enum GoodsErrorCode {
    NOT_SERVICE_HALL("800","无营业厅!"),
    ALREADY_CLOSE("801","本次已截止"),
    PARENT_NOT_ACTIVITY("802","货物只能在活动之下！"),
    NOT_FILLED_IN("803","必填项未填"),
    NOT_RELEVANT_FLOW("804","相关流程异常"),
    ;

    private String code;

    private String message;

    GoodsErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
