package com.riddler.usr.feign;

import com.riddler.usr.dto.PointsDto;
import com.riddler.usr.dto.RespDto;
import feign.Param;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "wallet")
public interface WalletRemoteAPI {

    /**
     *跟据用户uid查找用户当前投入资产中的积分（相当于余额宝）
     */
    @GetMapping(value = "/assets/{uid}")
    RespDto getUsrPoints(@PathVariable(value = "uid") String uid);

    /**
     * 因为用户行为产生积分改变的接口
     * 修改积分
     */
    @PutMapping(value = "/points")
    RespDto usrSendPoint(PointsDto point);

    /**
     *查找当前可用用积分（相当于余额）
     */
    @GetMapping("/points/{uid}")
    RespDto getActivePoints(@PathVariable(value = "uid") String uid );

    /**
     * 查找用户当前人民币余额
     * @param uid
     */
    @GetMapping("/rmb/{uid}")
    RespDto getUsrRmb(@PathVariable(value = "uid") String uid);

    /**
     * 可用积分和投资积分之间的转换
     */
    @PutMapping("/points_assets_transfer")
    RespDto pointsTransfer(PointsDto assetsDto);
}
