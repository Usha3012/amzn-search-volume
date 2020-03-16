package com.sellics.client;

import com.sellics.dto.AmazonSearchVolume;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "amznclient", url = "https://completion.amazon.com/api/2017")
public interface AmazonSearchVolumeClient {

    @RequestMapping(method = RequestMethod.GET, value = "/suggestions?mid=ATVPDKIKX0DER&alias=aps")
    AmazonSearchVolume getSearchVolumeByPrefix(@RequestParam("prefix") String prefix);
}
