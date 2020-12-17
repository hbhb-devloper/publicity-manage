package com.hbhb.cw.publicity.enums;

/**
 * @author wangxiaogang
 */

public enum TemplateContent {
    /**
     * 内容
     */
    TITLE("{title}"),
    /**
     * 审批人
     */
    APPROVE("{approve}"),
    /**
     * 意见
     */
    CAUSE("{cause}"),

    ;

    public String getValue() {
        return value;
    }

    private final String value;

    TemplateContent(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }
}
