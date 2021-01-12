package com.itheima.health.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.health.dao.OrderSettingDao;
import com.itheima.health.exception.MyException;
import com.itheima.health.pojo.OrderSetting;
import com.itheima.health.service.OrderSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

@Service(interfaceClass = OrderSettingService.class)
public class OrderSettingServiceImpl implements OrderSettingService {
    @Resource
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

    /**
     * 查询预约设置表当月的数据
     * @param month
     * @return
     */
    @Override
    public List<Map<String, Integer>> getOrderSettingByMonth(String month) {
        month+="%";
        return orderSettingDao.getOrderSettingByMonth(month);
    }

    /**
     * 通过日期设置可预约的最大数
     * @param orderSetting
     */
    @Override
    public void editNumberByDate(OrderSetting orderSetting) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
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
