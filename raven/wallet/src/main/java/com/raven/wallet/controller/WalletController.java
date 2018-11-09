package com.raven.wallet.controller;

import com.raven.wallet.consts.Const;
import com.raven.wallet.dto.*;
import com.raven.wallet.service.ThreadPoolService;
import com.raven.wallet.service.WalletService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@Controller
public class WalletController {

    @Autowired
    WalletService walletService;

    @Autowired
    RestTemplate restTemplate;

    private static final Logger log = LoggerFactory.getLogger(WalletController.class);


    /**
     * 更改积分
     *
     * @param pointsDto
     * @return
     */
    @PutMapping("/points")
    @ResponseBody
    public RespDto changePoints(@RequestBody PointsDto pointsDto) throws Exception {
        if (walletService.checkEnoughMoneyForChange(pointsDto)) {
            Thread t = walletService.createChangeWalletThread(pointsDto);
            ThreadPoolService.getThreadPoolExecutor().execute(t);
            return new RespDto(Const.SUCCESS, Const.SUCCESS_MSG);
        }
        return new RespDto(Const.NOT_ENOUGH, Const.NOT_ENOUGH_MSG);
    }


    /**
     * 获取用户积分
     *
     * @param uid
     * @return
     * @throws Exception
     */
    @GetMapping("/points/{uid}")
    @ResponseBody
    public RespDto getPoints(@PathVariable String uid) throws Exception {
        PointsDto dto = new PointsDto();
        dto.setUid(uid);
        return walletService.getMoney(dto);
    }


    /**
     * 获取用户积分列表
     *
     * @param uid
     * @return
     * @throws Exception
     */
    @GetMapping("/points_list/{uid}")
    @ResponseBody
    public RespDto getPointsList(@PathVariable String uid) throws Exception {
        PointsDto dto = new PointsDto();
        dto.setUid(uid);
        return walletService.getMoneyList(dto);
    }

    @PutMapping("/assets")
    @ResponseBody
    public RespDto changeAssets(@RequestBody AssetsDto assetsDto) throws Exception {
        if (walletService.checkEnoughMoneyForChange(assetsDto)) {
            Thread t = walletService.createChangeWalletThread(assetsDto);
            ThreadPoolService.getThreadPoolExecutor().execute(t);
            return new RespDto(Const.SUCCESS, Const.SUCCESS_MSG);
        }
        return new RespDto(Const.NOT_ENOUGH, Const.NOT_ENOUGH_MSG);
    }

    /**
     * 获取用户资产
     *
     * @param uid
     * @return
     * @throws Exception
     */
    @GetMapping("/assets/{uid}")
    @ResponseBody
    public RespDto getAssets(@PathVariable String uid) throws Exception {
        AssetsDto dto = new AssetsDto();
        dto.setUid(uid);
        return walletService.getMoney(dto);
    }

    @GetMapping("/assets/{uid}/{startDate}/{endDate}")
    @ResponseBody
    public RespDto getAssetsByDate(@PathVariable String uid, @PathVariable String startDate,
                                   @PathVariable String endDate) throws Exception {
        AssetsQueryDto dto = new AssetsQueryDto();
        dto.setUid(uid);
        dto.setStartDate(startDate);
        dto.setEndDate(endDate);
        return walletService.getMoneyByDate(dto);
    }

    /**
     * 获取用户积分列表
     *
     * @param uid
     * @return
     * @throws Exception
     */
    @GetMapping("/assets_list/{uid}")
    @ResponseBody
    public RespDto getAssetsList(@PathVariable String uid) throws Exception {
        AssetsDto dto = new AssetsDto();
        dto.setUid(uid);
        return walletService.getMoneyList(dto);
    }

    /**
     * 积分转资产
     *
     * @param assetsDto
     * @return
     */
    @PutMapping("/points_assets_transfer")
    @ResponseBody
    public RespDto pointsAssetsTransfer(@RequestBody AssetsDto assetsDto) throws Exception {
        if (assetsDto.getChange() < 0) {
            if (walletService.checkEnoughAssetsForTransfer(assetsDto)) {
                Thread t = walletService.createAssetsTransferThread(assetsDto);
                ThreadPoolService.getThreadPoolExecutor().execute(t);
                return new RespDto(Const.SUCCESS, Const.SUCCESS_MSG);
            } else {
                return new RespDto(Const.NOT_ENOUGH, Const.NOT_ENOUGH_MSG);
            }
        } else {
            if (walletService.checkEnoughPointsForTransfer(assetsDto)) {
                Thread t = walletService.createAssetsTransferThread(assetsDto);
                ThreadPoolService.getThreadPoolExecutor().execute(t);
                return new RespDto(Const.SUCCESS, Const.SUCCESS_MSG);
            } else {
                return new RespDto(Const.NOT_ENOUGH, Const.NOT_ENOUGH_MSG);
            }
        }
    }

    /**
     * 更改rmb
     *
     * @param rmbDto
     * @return
     */
    @PutMapping("/rmb")
    @ResponseBody
    public RespDto changeRMB(@RequestBody RMBDto rmbDto) throws Exception {
        log.info(rmbDto.toString());
        if (walletService.checkEnoughMoneyForChange(rmbDto)) {
            Thread t = walletService.createChangeWalletThread(rmbDto);
            ThreadPoolService.getThreadPoolExecutor().execute(t);
            return new RespDto(Const.SUCCESS, Const.SUCCESS_MSG);
        }
        return new RespDto(Const.NOT_ENOUGH, Const.NOT_ENOUGH_MSG);
    }


    /**
     * 获取用户rmb
     *
     * @param uid
     * @return
     * @throws Exception
     */
    @GetMapping("/rmb/{uid}")
    @ResponseBody
    public RespDto getRMB(@PathVariable String uid) throws Exception {
        RMBDto dto = new RMBDto();
        dto.setUid(uid);
        return walletService.getMoney(dto);
    }

    @PutMapping("/frozen_points")
    @ResponseBody
    public RespDto freezePoints(@RequestBody PointsDto dto) throws Exception {
        if (walletService.checkEnoughPointsForTransfer(dto)) {
            Thread t = walletService.createFreezeWalletThread(dto);
            ThreadPoolService.getThreadPoolExecutor().execute(t);
            return new RespDto(Const.SUCCESS, Const.SUCCESS_MSG);
        }
        return new RespDto(Const.NOT_ENOUGH, Const.NOT_ENOUGH_MSG);
    }

    @PutMapping("/unfrozen_points")
    @ResponseBody
    public RespDto unfreezePoints(@RequestBody PointsDto dto) throws Exception {
        Thread t = walletService.createUnfreezeWalletThread(dto);
        ThreadPoolService.getThreadPoolExecutor().execute(t);
        return new RespDto(Const.SUCCESS, Const.SUCCESS_MSG);
    }
}
