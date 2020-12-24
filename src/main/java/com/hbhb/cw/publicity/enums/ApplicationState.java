package com.hbhb.cw.publicity.enums;

/**
 * @author yzc
 * @since 2020-12-04
 */
public enum ApplicationState {
    /**
     * 未开始审批
     */
    NOT_APPROVED(10),
    /**
     * 正在审批
     */
    APPROVING(20),
    /**
     * 审批未通过
     */
    APPROVE_REJECTED(30),
    /**
     * 审批通过
     */
    APPROVED(31);

    private final Integer value;

    ApplicationState(Integer value) {
        this.value = value;
    }

    public Integer value() {
        return this.value;
    }
}
