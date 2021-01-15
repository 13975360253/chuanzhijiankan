package com.itheima.health.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.health.constant.MessageConstant;
import com.itheima.health.constant.RedisMessageConstant;
import com.itheima.health.entity.Result;
import com.itheima.health.pojo.Member;
import com.itheima.health.service.MemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/login")
public class LoginController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private JedisPool jedisPool;

    @Reference
    private MemberService memberService;

    @PostMapping("/check")
    public Result check(@RequestBody Map<String,String> loginInfo, HttpServletResponse response) {
        // 校验验证码
        //- 拼接redis的key, 获取redis中的验证码
        Jedis jedis = jedisPool.getResource();
        String telephone = loginInfo.get("telephone");
        String key = RedisMessageConstant.SENDTYPE_LOGIN + "_" + telephone;
        String codeInRedis = jedis.get(key);

        log.debug("codeInRedis:{}",codeInRedis);
        if (StringUtils.isEmpty(codeInRedis)) {
            // - 没有值
            // - 提示重新获取验证码
            return new Result(false,"请重新获取验证码!");
        }

        log.debug("codeFromUI:{}",loginInfo.get("validateCode"));

        // 有值
        if (!codeInRedis.equals(loginInfo.get("validateCode"))) {
            // 不相同
            return new Result(false,"验证码不正确");
        }
        // 相同，删除key,防止重复提交
        jedis.del(key);
        jedis.close();
        // 通过手机号码判断是否为会员
        Member member = memberService.findByTelephone(telephone);
        if (member == null) {
            // 未注册
            member = new Member();
            member.setRegTime(new Date());
            member.setPhoneNumber(telephone);
            member.setRemark("快速登录");
            // 添加会员
            memberService.add(member);
        }

        // 添加cookie跟踪
        Cookie cookie = new Cookie("login_member_telephone",telephone);
        cookie.setMaxAge(30*24*60*60); // cookie存活时间
        cookie.setPath("/"); // 所有的访问url都带上这个cookie
        response.addCookie(cookie); // 输出给客户端
        return new Result(true, MessageConstant.LOGIN_SUCCESS);
    }
}
