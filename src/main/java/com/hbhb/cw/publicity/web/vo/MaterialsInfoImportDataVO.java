package com.hbhb.cw.publicity.web.vo;

import com.hbhb.cw.publicity.model.MaterialsInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author wangxiaogang
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MaterialsInfoImportDataVO implements Serializable {
    private static final long serialVersionUID = -6770972023059411168L;

    private String id;

    private List<MaterialsInfo> materialsInfo;
}
