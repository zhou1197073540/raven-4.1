package com.raven.wallet.controller;

import com.raven.wallet.consts.Const;
import com.raven.wallet.dto.PointsDto;
import com.raven.wallet.dto.RespDto;
import com.raven.wallet.service.ThreadPoolService;
import com.raven.wallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class WalletTakerController {

    @Autowired
    WalletService walletService;

    @PutMapping("/taker_balance")
    @ResponseBody
    public RespDto takerBalance(@RequestBody PointsDto dto) throws Exception {
        Thread t = walletService.createTakerBalanceThread(dto);
        ThreadPoolService.getThreadPoolExecutor().execute(t);
        return new RespDto(Const.SUCCESS, Const.SUCCESS_MSG);
    }
}
