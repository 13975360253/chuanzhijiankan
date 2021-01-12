package com.itheima.health.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.health.dao.MemberDao;
import com.itheima.health.dao.OrderDao;
import com.itheima.health.dao.OrderSettingDao;
import com.itheima.health.exception.MyException;
import com.itheima.health.pojo.Member;
import com.itheima.health.pojo.Order;
import com.itheima.health.pojo.OrderSetting;
import com.itheima.health.service.OrderService;
import com.itheima.health.service.OrderSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service(interfaceClass = OrderService.class)
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private OrderSettingDao orderSettingDao;

    @Autowired
    private MemberDao memberDao;

    /**
     * 提交预约订单
     * @param orderInfo
     * @return
     */
    @Override
    @Transactional
    public Order submitOrder(Map<String, String> orderInfo) {
        //1.根据体检日期查询预约设置
        String orderDateStr = orderInfo.get("orderDate");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date orderDate = null;
        try {
            orderDate = sdf.parse(orderDateStr);
        } catch (ParseException e) {
            throw new MyException("日期格式不正确");
        }
        // 通过预约日期查询预约设置
        OrderSetting osInDB = orderSettingDao.findByOrderDate(orderDate);
        if (null != osInDB) {
            // 有设置，可以预约
            // 判断是否约满
            if (osInDB.getReservations() >= osInDB.getNumber()) {
                //预约已满
                throw new MyException("所选日期预约已满，请选其它日期");
            }
        } else {
            // 没设置，不能预约
            throw new MyException("所选日期不能预约，请选其它日期");
        }
        //2. 会员操作
        String telephone = orderInfo.get("telephone");
        // 手机号码查询是否存在
        Member member = memberDao.findByTelephone(telephone);

        // 构建订单信息
        Order order = new Order();
        // 订单的套餐id
        String setmealId = orderInfo.get("setmealId");
        order.setSetmealId(Integer.valueOf(setmealId));
        // 订单预约日期 //orderDate: 前端
        order.setOrderDate(orderDate);


        if (member == null) {
            //不存在
            member = new Member();
            //    添加会员 返回主键
            //    会员信息由前端传过来
            member.setPhoneNumber(telephone);
            member.setRegTime(new Date());
            member.setRemark("微信预约自动注册");
            member.setName(orderInfo.get("name"));
            member.setSex(orderInfo.get("sex"));
            member.setIdCard(orderInfo.get("idCard"));
            member.setPassword(telephone.substring(5));
            memberDao.add(member);
            order.setMemberId(member.getId());
        } else {
            // 存在
            //    判断是否重复预约
            //    通过套餐id, 会员id, 预约日期
            Integer memberId = member.getId();
            order.setMemberId(memberId);
            List<Order> orderList = orderDao.findByCondition(order);
            if (!CollectionUtils.isEmpty(orderList)) {
                throw new MyException("不能重复预约");
            }
        }
        //3. 更新已预约人数, 防止超卖，行锁, 更新成功返回1，失败返回0
        int count = orderSettingDao.editReservationsByOrderDate(osInDB);
        if (count == 0) {
            throw new MyException("所选日期预约已满，请选其它日期");
        }

        //4. 订单表操作 添加预约
        //orderType: 微信预约   /电话 预约,
        order.setOrderType(orderInfo.get("orderType"));
        // 设置就诊状态
        order.setOrderStatus(Order.ORDERSTATUS_NO);
        orderDao.add(order);
        return order;

    }

    /**
     * 查询预约成功订单信息
     * @param id
     * @return
     */
    @Override
    public Map<String, String> findDetailById(int id) {
        return orderDao.findById4Detail(id);
    }
}
