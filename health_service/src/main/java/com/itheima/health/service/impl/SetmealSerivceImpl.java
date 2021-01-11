package com.itheima.health.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.StringUtil;
import com.itheima.health.dao.SetmealDao;
import com.itheima.health.entity.PageResult;
import com.itheima.health.entity.QueryPageBean;
import com.itheima.health.exception.MyException;
import com.itheima.health.pojo.CheckGroup;
import com.itheima.health.pojo.CheckItem;
import com.itheima.health.pojo.Setmeal;
import com.itheima.health.service.SetmealSerivce;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service(interfaceClass = SetmealSerivce.class)
public class SetmealSerivceImpl implements SetmealSerivce {

    @Autowired
    private SetmealDao setmealDao;
    /**
     * 添加套餐
     * @param setmeal
     * @param checkgroupIds
     */
    @Override
    @Transactional
    public void add(Setmeal setmeal, Integer[] checkgroupIds) {
        //添加套餐
        setmealDao.add(setmeal);
        //获取套餐的id
        Integer setmealId = setmeal.getId();
        //- 遍历checkgroupIds数组
        if (null != checkgroupIds) {
            //添加套餐余检查组关系
            for (Integer checkgroupId : checkgroupIds) {
                setmealDao.addSetmealCheckGroup(setmealId,checkgroupId);
            }
        }
    }

    /**
     * 套餐分页查询
     * @param queryPageBean
     * @return
     */
    @Override
    public PageResult<Setmeal> findPage(QueryPageBean queryPageBean) {
        PageHelper.startPage(queryPageBean.getCurrentPage(),queryPageBean.getPageSize());
        // 条件查询
        if (StringUtil.isNotEmpty(queryPageBean.getQueryString())) {
            // 模糊查询
            queryPageBean.setQueryString("%"+queryPageBean.getQueryString()+"%");
        }
        Page<Setmeal> setmealPage = setmealDao.findByCondition(queryPageBean.getQueryString());
        return new PageResult<Setmeal>(setmealPage.getTotal(),setmealPage.getResult());
    }

    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    @Override
    public Setmeal findById(int id) {
        return setmealDao.findById(id);
    }

    /**
     * 根据套餐id查询检查组id
     * @param id
     * @return
     */
    @Override
    public List<Integer> findCheckGroupIdsBySetmealId(int id) {
        return setmealDao.findCheckGroupIdsBySetmealId(id);
    }

    /**
     * 更新套餐
     * @param setmeal
     * @param checkgroupIds
     */
    @Override
    @Transactional
    public void update(Setmeal setmeal, Integer[] checkgroupIds) {
        //更新套餐
        setmealDao.update(setmeal);
        //删除旧关系
        setmealDao.deledeSetmealCheckgroup(setmeal.getId());
        //遍历选中的检查组
        if (null != checkgroupIds) {
            for (Integer checkgroupId : checkgroupIds) {
                //添加新关系
                setmealDao.addSetmealCheckGroup(setmeal.getId(),checkgroupId);
            }
        }
    }

    /**
     * 删除套餐
     * @param id
     */
    @Override
    @Transactional
    public void deleteById(int id) {
        // 先判断 是否被订单使用了
        int count = setmealDao.findCountBySetmealId(id);
        if (count > 0) {
            throw new MyException("套餐已被使用，不能删除");
        }
        // 没使用，则要先删除套餐与检查组的关系
        setmealDao.deledeSetmealCheckgroup(id);
        //删除套餐
        setmealDao.deleteById(id);
    }

    /**
     * 查询数据库所有的图片
     * @return
     */
    @Override
    public List<String> findImgs() {
        return setmealDao.findImgs();
    }

    /**
     * 查询所有的套餐
     * @return
     */
    @Override
    public List<Setmeal> findAll() {
        return setmealDao.findAll();
    }

    /**
     * 调用服务查询套餐详情1
     * @param id
     * @return
     */
    @Override
    public Setmeal findDetailById(int id) {
        return setmealDao.findDetailById(id);
    }

    /**
     * 调用服务查询套餐详情3
     * @param id
     * @return
     */
    @Override
    public Setmeal findDetailById2(int id) {
        //查询套餐信息
        Setmeal setmeal = setmealDao.findById(id);
        //查询套餐下的检查组信息
        List<CheckGroup> checkGroupList = setmealDao.findCheckGroupListBySetmealId(id);
        //遍历检查组
        for (CheckGroup checkGroup : checkGroupList) {
            //查询检查组下的检查项信息
            List<CheckItem> checkItemList = setmealDao.findCheckItemByCheckGroupId(checkGroup.getId());
            //设置这个检查组下所拥有的检查项
            checkGroup.setCheckItems(checkItemList);
        }
        //设置套餐下的所拥有的检查组
        setmeal.setCheckGroups(checkGroupList);
        return setmeal;
    }
}
