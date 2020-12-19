package com.hbhb.cw.publicity.enums;

/**
 * @author yzc
 * @since 2020-12-17
 */
public enum FlowRoleName {
    /**
     * 物料审核员
     */
    CHECKER("物料审核员"),
    ;

    private final String  value;

    FlowRoleName(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }
}
