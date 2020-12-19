package com.hbhb.cw.publicity.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.beetl.sql.annotation.entity.AutoID;

import java.io.Serializable;
import java.util.Date;

/**
 * @author wangxiaogang
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PictureFile implements Serializable {
    private static final long serialVersionUID = -3325070566732162335L;
    /**
     * id
     */
    @AutoID
    private Long id;
    /**
     * 附件id
     */
    private Long fileId;
    /**
     * 上传时间
     */
    private Date createTime;
    /**
     * 上传人
     */
    private String createBy;
    /**
     * 宣传画面id
     */
    private Long pictureId;
}
