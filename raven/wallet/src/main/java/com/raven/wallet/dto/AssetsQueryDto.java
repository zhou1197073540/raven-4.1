package com.raven.wallet.dto;

import com.raven.wallet.consts.Const;

public class AssetsQueryDto extends QueryDto {

    private static final long serialVersionUID = 7532578081852913104L;

    public AssetsQueryDto() {
        super.setType(Const.ASSETS);
        super.setTypeStr(Const.ASSETS_STR);
    }
}
