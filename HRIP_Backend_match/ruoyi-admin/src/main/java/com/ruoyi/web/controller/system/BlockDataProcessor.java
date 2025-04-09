package com.ruoyi.web.controller.system;


import com.alibaba.fastjson2.JSONObject;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.client.protocol.response.BcosBlock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigInteger;
import java.util.List;

@Controller
@RequestMapping("/block")
public class BlockDataProcessor {


    @Autowired
    private Client client;

    /**
     * 最新高度和最新交易Hash
     * @return
     */
//    @GetMapping("/blockchainInfo")
//    public String getBlockchainInfo() {
//        JSONObject requestObject = new JSONObject();
//	         <!--+++++++++++++++++++++++++++++++++++++++++++++++++-->
//			 <!--子任务3-2-1：获取区块链交易信息-->
//			 <!--+++++++++++++++++++++++++++++++++++++++++++++++++-->
//        return requestObject;
//
//    }
}
