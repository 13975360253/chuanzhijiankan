package com.itheima.health.dao;

import com.itheima.health.pojo.OrderSetting;

import java.util.Date;

public interface OrderSettingDao {
    /**
     * 通过预约的日期来查询预约设置表，看这个日期的设置信息是否存在
     * @param orderDate
     * @return
     */
    OrderSetting findByOrderDate(Date orderDate);

    /**
     * 添加预约设置
     * @param orderSetting
     */
    void add(OrderSetting orderSetting);

    /**
     * 更新最大预约数
     * @param orderSetting
     */
    void updateNumber(OrderSetting orderSetting);
}
