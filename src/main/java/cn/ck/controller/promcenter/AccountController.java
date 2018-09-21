package cn.ck.controller.promcenter;


import cn.ck.controller.AbstractController;
import cn.ck.entity.Account;
import cn.ck.entity.Alluser;
import cn.ck.entity.Promulgator;
import cn.ck.service.AccountService;
import cn.ck.service.PromulgatorService;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/prom")
public class AccountController {
    @Autowired
    private PromulgatorService promulgatorService;
    @Autowired
    private AccountService accountService;

    @RequestMapping("/tiao")
    public String tiao(){
        return "/promulgator/prom_Account";
    }
    @RequestMapping("/account")
    @ResponseBody
    public Map<String,Object> account() {
        Alluser user = (Alluser) SecurityUtils.getSubject().getPrincipal();
        Promulgator promulgator=new Promulgator();
        promulgator=promulgatorService.selectID(user.getAllId());
        Account account=new Account();
        account=accountService.selectOne(new EntityWrapper<Account>().eq("acc_foreid", user.getAllId()));
        Map<String,Object> prommap=new HashMap<String, Object>();
        prommap.put("num","1");
        prommap.put("prom",promulgator);
        prommap.put("account",account);
        String json = JSON.toJSONString(prommap,true);
        System.out.println(json);
        System.out.println(prommap);
        return prommap;
    }
}
