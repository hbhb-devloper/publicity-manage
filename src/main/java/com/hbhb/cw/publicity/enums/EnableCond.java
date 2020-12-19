package com.hbhb.cw.publicity.enums;

/**
 * @author wangxiaogang
 */

public enum EnableCond {
    /**
     * 大于
     */
    GREATER(20, "大于"),
    /**
     * 大于等于
     */
    EQUAL_GREATER(10, "大于等于"),
    /**
     * 小于
     */
    LESS(40, "小于"),
    /**
     * 小于等于
     */
    EQUAL_LESS(50, "小于等于"),
    /**
     * 等于
     */
    EQUAL(30, "等于");

    private final Integer key;
    private final String value;

    EnableCond(Integer key, String value) {
        this.key = key;
        this.value = value;
    }


    public String value() {
        return this.value;
    }

    public Integer key() {
        return this.key;
    }
}
