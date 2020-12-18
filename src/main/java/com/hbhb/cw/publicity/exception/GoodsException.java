package com.hbhb.cw.publicity.Exception;


import com.hbhb.core.bean.MessageConvert;
import com.hbhb.cw.publicity.enums.GoodsErrorCode;
import com.hbhb.web.exception.BusinessException;
import lombok.Getter;

/**
 * @author yzc
 * @since 2020-11-25
 */
@Getter
public class GoodsException extends BusinessException {
    private static final long serialVersionUID = 8071162749850615442L;

    private final String code;

    public GoodsException(GoodsErrorCode errorCode) {
        super(errorCode.getCode(), MessageConvert.convert(errorCode.getMessage()));
        this.code = errorCode.getCode();
    }
}
