package com.itheima.health.service;

import com.itheima.health.pojo.OrderSetting;

import java.util.List;

public interface OrderSettingService {
    /**
     * 调用服务导入
     * @param orderSettingList
     */
    void addBatch(List<OrderSetting> orderSettingList);
}
