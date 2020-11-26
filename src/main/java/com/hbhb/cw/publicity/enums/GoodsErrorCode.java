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
    ;

    private String code;

    private String message;

    GoodsErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
