package com.hbhb.cw.publicity.common;

/**
 * @author yzc
 * @since 2020-11-24
 */
public enum GoodsType {


    /**
     * 业务单式
     *
     */
    BUSINESS_SIMPLEX(0),

    /**
     * 宣传单页
     */
    FLYER_PAGE(1);

    private final Integer value;

    public Integer value() {
        return this.value;
    }

    public Integer getValue() {
        return value;
    }

    GoodsType(Integer value) {
        this.value = value;
    }
}
