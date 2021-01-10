package com.itheima.health.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.health.dao.OrderSettingDao;
import com.itheima.health.exception.MyException;
import com.itheima.health.pojo.OrderSetting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.List;
@Service(interfaceClass = OrderSettingService.class)
public class OrderSettingServiceImpl implements OrderSettingService {
    @Autowired
    private OrderSettingDao orderSettingDao;

    /**
     * 批量导入预约设置
     * @param orderSettingList
     */
    @Override
    public void addBatch(List<OrderSetting> orderSettingList) {
        //- 判断List<Ordersetting>不为空
        if (!CollectionUtils.isEmpty(orderSettingList)) {
            //- 遍历导入的预约设置信息List<Ordersetting>
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            for (OrderSetting orderSetting : orderSettingList) {
                //- 通过预约的日期来查询预约设置表，看这个日期的设置信息是否存在
                OrderSetting osInDB = orderSettingDao.findByOrderDate(orderSetting.getOrderDate());
                //- 没有预约设置(表中没有这个日期的记录)
                if (osInDB == null) {
                    //不存在则添加设置信息
                    orderSettingDao.add(orderSetting);
                } else {
                    //- 有预约设置(表中有这个日期的记录)
                    //- 判断已预约人数是否大于要更新的最大预约数
                    if (osInDB.getReservations() > orderSetting.getNumber()) {
                        throw new MyException(sdf.format(orderSetting.getOrderDate())+"最大预约数不能小于已预约人数");
                    } else {
                        //  - 小于，则可以更新最大预约数
                        orderSettingDao.updateNumber(orderSetting);
                    }
                }
            }
        }
    }
}
