package com.itheima.health.dao;

import com.itheima.health.pojo.Order;

import java.util.List;
import java.util.Map;

public interface OrderDao {

    /**
     * 判断是否重复预约
     * @param order
     * @return
     */
    List<Order> findByCondition(Order order);

    /**
     * 添加订单
     * @param order
     */
    void add(Order order);

    /**
     * 查询预约成功订单信息
     * @param id
     * @return
     */
    Map<String, String> findById4Detail(int id);
}
