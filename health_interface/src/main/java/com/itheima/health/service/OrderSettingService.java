package com.itheima.health.service;

import com.itheima.health.exception.MyException;
import com.itheima.health.pojo.OrderSetting;

import java.util.List;
import java.util.Map;

public interface OrderSettingService {
    /**
     * 调用服务导入
     * @param orderSettingList
     */
    void addBatch(List<OrderSetting> orderSettingList) throws MyException;

    /**
     * 查询预约设置表当月的数据
     * @param month
     * @return
     */
    List<Map<String, Integer>> getOrderSettingByMonth(String month);

    /**
     * 通过日期设置可预约的最大数
     * @param orderSetting
     */
    void editNumberByDate(OrderSetting orderSetting) throws MyException;
}
