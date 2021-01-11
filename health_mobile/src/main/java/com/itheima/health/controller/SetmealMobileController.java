package com.itheima.health.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.health.constant.MessageConstant;
import com.itheima.health.entity.Result;
import com.itheima.health.pojo.Setmeal;
import com.itheima.health.service.SetmealSerivce;
import com.itheima.health.utils.QiNiuUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/setmeal")
public class SetmealMobileController {
    @Reference
    private SetmealSerivce setmealSerivce;

    /**
     * 套餐列表
     * @return
     */
    @GetMapping("/getSetmeal")
    public Result getSetmeal() {
        // 调用服务来查询所有的套餐
        List<Setmeal> setmealList = setmealSerivce.findAll();
        // 拼接图片的完整路径
        setmealList.forEach(s -> s.setImg(QiNiuUtils.DOMAIN + s.getImg()));
        // 返回给页面
        return new Result(true, MessageConstant.QUERY_SETMEAL_SUCCESS,setmealList);
    }


    /**
     * 通过id查询套餐详情方法1--性能最好
     * @param id
     * @return
     */
    @GetMapping("/findDetailById")
    public Result findDetailById(int id) {
        // 调用服务查询套餐详情
        Setmeal setmeal = setmealSerivce.findDetailById(id);
        // 拼接图片的完整路径
        setmeal.setImg(QiNiuUtils.DOMAIN + setmeal.getImg());
        // 返回结果给页面
        return new Result(true,MessageConstant.QUERY_SETMEAL_SUCCESS,setmeal);
    }

    /**
     * 通过id查询套餐详情方法3--性能最差
     * @param id
     * @return
     */
    @GetMapping("/findDetailById2")
    public Result findDetailById2(int id) {
        // 调用服务查询套餐详情
        Setmeal setmeal = setmealSerivce.findDetailById2(id);
        // 拼接图片的完整路径
        setmeal.setImg(QiNiuUtils.DOMAIN + setmeal.getImg());
        // 返回结果给页面
        return new Result(true,MessageConstant.QUERY_SETMEAL_SUCCESS,setmeal);
    }

}
