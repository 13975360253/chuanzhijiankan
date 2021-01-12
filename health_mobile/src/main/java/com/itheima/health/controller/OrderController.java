package com.itheima.health.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.health.constant.MessageConstant;
import com.itheima.health.constant.RedisMessageConstant;
import com.itheima.health.entity.Result;
import com.itheima.health.pojo.Order;
import com.itheima.health.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Map;

@RestController
@RequestMapping("/order")
public class OrderController {
    private static final Logger log = LoggerFactory.getLogger(OrderController.class);
    @Autowired
    private JedisPool jedisPool;

    @Reference
    private OrderService orderService;

    /**
     * 提交订单
     * @param orderInfo
     * @return
     */
    @PostMapping("/submit")
    public Result submit(@RequestBody Map<String,String> orderInfo) {
        // 校验验证码
        //- 拼接redis的key, 获取redis中的验证码
        String telephone = orderInfo.get("telephone");
        String key = RedisMessageConstant.SENDTYPE_ORDER + "_" + telephone;
        Jedis jedis = jedisPool.getResource();
        String codeInRedis = jedis.get(key);
        log.debug("codeInRedis:{}",codeInRedis);
        //- 判断有没有值
        if (StringUtils.isEmpty(codeInRedis)) {
            //没值
            //  - 提示重新获取验证码
            return new Result(false,"请重新获取验证码");
        }
        //有值
        log.debug("codeFromUI:{}",orderInfo.get("validateCode"));
        // 判断验证码是否一致
        if (!codeInRedis.equals(orderInfo.get("validateCode"))) {
            // 不相同，提示验证码错误
            return new Result(false,"验证码错误");
        }
        // 验证码正确
        // 删除redis中的key,防止重复提交
        jedis.del(key);
        jedis.close();
        // 设置预约的类型，health_mobile, 微信预约
        orderInfo.put("orderType", Order.ORDERTYPE_WEIXIN);
        // 调用预约服务
        Order order = orderService.submitOrder(orderInfo);
        return new Result(true, MessageConstant.ORDER_SUCCESS,order);
    }


    /**
     * 查询预约成功订单信息
     * @param id
     * @return
     */
    @GetMapping("findById")
    public Result findById(int id) {
        Map<String,String> orderInfo = orderService.findDetailById(id);
        return new Result(true,MessageConstant.QUERY_ORDER_SUCCESS,orderInfo);
    }
}
