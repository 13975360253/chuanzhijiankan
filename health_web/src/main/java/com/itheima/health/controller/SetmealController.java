package com.itheima.health.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.health.constant.MessageConstant;
import com.itheima.health.entity.PageResult;
import com.itheima.health.entity.QueryPageBean;
import com.itheima.health.entity.Result;
import com.itheima.health.pojo.Setmeal;
import com.itheima.health.service.SetmealSerivce;
import com.itheima.health.utils.QiNiuUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    //日志
    private static Logger log = LoggerFactory.getLogger(SetmealController.class);

    @Reference
    private SetmealSerivce setmealSerivce;

    /**
     * 上传图片
     * @param imgFile
     * @return
     */
    @RequestMapping("/upload")
    public Result upload(MultipartFile imgFile) {
        //1. 获取源文件名
        String originalFilename = imgFile.getOriginalFilename();
        //2. 截取后缀名
        String substring = originalFilename.substring(originalFilename.lastIndexOf("."));
        //3. 生成唯一id
        String uniqueId = UUID.randomUUID().toString();
        //4. 拼接唯一文件名
        String fileName = uniqueId + substring;
        //5. 调用7牛工具上传图片
        try {
            QiNiuUtils.uploadViaByte(imgFile.getBytes(),fileName);
            //6. 构建返回的数据
            /*
             {
                imgName: 图片名 , 补全formData.img
                domain: 七牛的域名 图片回显imageUrl = domain+图片名
            }
             */
            Map<String,String> map = new HashMap<>();
            map.put("imgName",fileName);
            map.put("domain",QiNiuUtils.DOMAIN);
            //7. 放到result里，再返回给页面
            return new Result(true, MessageConstant.PIC_UPLOAD_SUCCESS,map);
        } catch (IOException e) {
            log.error("上传文件失败",e);
            return new Result(false,MessageConstant.PIC_UPLOAD_FAIL);
        }

    }

    /**
     * 添加套餐
     * @return
     */
    @PostMapping("/add")
    public Result add(@RequestBody Setmeal setmeal,Integer[] checkgroupIds) {
        // 调用服务添加套餐
        setmealSerivce.add(setmeal,checkgroupIds);
        return new Result(true,MessageConstant.ADD_SETMEAL_SUCCESS);
    }

    /**
     * 套餐分页查询
     * @param queryPageBean
     * @return
     */
    @PostMapping("/findPage")
    public Result findPage(@RequestBody QueryPageBean queryPageBean) {
        //套餐分页查询
        PageResult<Setmeal> pageResult = setmealSerivce.findPage(queryPageBean);
        return new Result(true,MessageConstant.QUERY_SETMEALLIST_SUCCESS,pageResult);
    }

    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    @GetMapping("/findById")
    public Result findById(int id) {
        Setmeal setmeal = setmealSerivce.findById(id);
        // 构建前端需要的数据, 还要有域名
        Map<String,Object> map = new HashMap<>(2);
        map.put("setmeal",setmeal);
        map.put("domain",QiNiuUtils.DOMAIN);
        return new Result(true, MessageConstant.QUERY_SETMEAL_SUCCESS, map);
    }

    /**
     * 根据套餐id查询检查组id
     * @param id
     * @return
     */
    @GetMapping("/findCheckGroupIdsBySetmealId")
    public Result findCheckGroupIdsBySetmealId(int id) {
        List<Integer> checkGroupIds = setmealSerivce.findCheckGroupIdsBySetmealId(id);
        return new Result(true,MessageConstant.QUERY_CHECKGROUP_SUCCESS,checkGroupIds);
    }

    /**
     * 更新套餐
     * @param setmeal
     * @param checkgroupIds
     * @return
     */
    @PostMapping("/update")
    public Result update(@RequestBody Setmeal setmeal, Integer[] checkgroupIds) {
        setmealSerivce.update(setmeal,checkgroupIds);
        return new Result(true,MessageConstant.EDIT_SETMEAL_SUCCESS);
    }

    /**
     * 删除套餐
     * @param id
     * @return
     */
    @PostMapping("/deleteById")
    public Result deleteById(int id) {
        setmealSerivce.deleteById(id);
        return new Result(true,MessageConstant.DELETE_SETMEAL_SUCCESS);
    }




}
