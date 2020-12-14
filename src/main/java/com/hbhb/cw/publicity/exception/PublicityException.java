package com.hbhb.cw.publicity.Exception;

import com.hbhb.core.bean.MessageConvert;
import com.hbhb.cw.publicity.enums.PublicityErrorCode;
import com.hbhb.web.exception.BusinessException;
import lombok.Getter;

/**
 * @author wangxiaogang
 */
@Getter
public class PublicityException extends BusinessException {
    private static final long serialVersionUID = 8071162749850615442L;

    private final String code;

    public PublicityException(PublicityErrorCode errorCode) {
        super(errorCode.getCode(), MessageConvert.convert(errorCode.getMessage()));
        this.code = errorCode.getCode();
    }
}
