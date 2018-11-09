package com.raven.message_center.service;

import com.raven.message_center.dto.RespDto;
import com.raven.message_center.dto.TaskDto;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "wallet",fallback = WalletHystrixService.class)
public interface WalletRemoteService {

    @PutMapping("/points")
    RespDto changePoints(@RequestBody TaskDto pointsDto);
}
