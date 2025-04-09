package com.ruoyi.web.controller.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.config.RuoyiConfig;
import com.ruoyi.common.utils.StringUtils;

/**
 * 首页
 *
 * @author ruoyi
 */
@RestController
public class SysIndexController
{
    /** 系统基础配置 */
    @Autowired
    private RuoyiConfig ruoyiConfig;

    /**
     * 访问首页，提示语
     */
    @RequestMapping("/")
    public String index()
    {
        return StringUtils.format("欢迎使用{}后台管理框架，当前版本：v{}，请通过前端地址访问。", ruoyiConfig.getName(), ruoyiConfig.getVersion());
    }
    @RequestMapping("/getInfo")
    public String getInfo()
    {
        return "{\"name\":\"RuoYi\",\"version\":\"3.8.0\"}";
    }
    @RequestMapping("/getConfig")
    public String getConfig()
    {
        return "{\"addressEnabled\":\"false\",\"captchaEnabled\":\"false\",\"demoEnabled\":\"false\",\"name\":\"RuoYi\",\"version\":\"3.8.0\"}";
    }


}
