package com.raven.feign_consumer;


import com.raven.feign_consumer.dto.PointsDto;
import com.raven.feign_consumer.dto.RespDto;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "wallet")
public interface WalletRemoteService {

    @GetMapping(value = "/points/{uid}")
    RespDto getPoints(@PathVariable("uid") String uid) throws Exception;

    @GetMapping(value = "/points_list/{uid}")
    RespDto getPointsList(@PathVariable("uid") String uid) throws Exception;

    @PutMapping(value = "/points")
    RespDto changePoints(@RequestBody PointsDto pointsDto) throws Exception;


}
