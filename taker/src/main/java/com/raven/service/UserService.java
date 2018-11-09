package com.raven.service;

import com.raven.dto.PointsDto;
import com.raven.dto.RespDto;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Created by daniel.luo on 2017/9/8.
 */
@FeignClient(name = "wallet")
public interface UserService {
    @GetMapping(value = "/points/{uid}")
    RespDto getPoints(@PathVariable("uid") String uid) throws Exception;

    @PutMapping("/points")
    RespDto changePoints(@RequestBody PointsDto pointsDto) throws Exception;

    @PutMapping("/frozen_points")
    RespDto freezePoints(@RequestBody PointsDto pointsDto) throws Exception;
}
