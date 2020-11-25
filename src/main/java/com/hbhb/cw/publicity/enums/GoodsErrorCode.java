package com.hbhb.cw.publicity.enums;


import lombok.Getter;

/**
 * @author yzc
 * @since 2020-11-25
 */
@Getter
public enum GoodsErrorCode {
    NOT_SERVICE_HALL("800","无营业厅!"),
    ;

    private String code;

    private String message;

    GoodsErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
