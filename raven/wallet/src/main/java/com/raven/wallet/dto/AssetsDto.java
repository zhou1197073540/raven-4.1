package com.raven.wallet.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.raven.wallet.consts.Const;

@JsonIgnoreProperties
public class AssetsDto extends TaskDto {

    private static final long serialVersionUID = 8961415038430958791L;

    public AssetsDto() {
        super.setType(Const.ASSETS);
        super.setTypeStr(Const.ASSETS_STR);
    }
}
