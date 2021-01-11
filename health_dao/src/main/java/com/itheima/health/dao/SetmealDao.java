package com.itheima.health.dao;

import com.github.pagehelper.Page;
import com.itheima.health.pojo.CheckGroup;
import com.itheima.health.pojo.CheckItem;
import com.itheima.health.pojo.Setmeal;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SetmealDao {
    /**
     * 添加套餐
     * @param setmeal
     */
    void add(Setmeal setmeal);

    /**
     * 添加套餐余检查组关系
     * @param setmealId
     * @param checkgroupId
     */
    void addSetmealCheckGroup(@Param("setmealId") Integer setmealId, @Param("checkgroupId") Integer checkgroupId);

    /**
     * 条件查询
     * @param queryString
     * @return
     */
    Page<Setmeal> findByCondition(@Param("queryString") String queryString);

    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    Setmeal findById(int id);

    /**
     * 根据套餐id查询检查组id
     * @param id
     * @return
     */
    List<Integer> findCheckGroupIdsBySetmealId(int id);

    /**
     * 更新套餐
     * @param setmeal
     */
    void update(Setmeal setmeal);

    /**
     * 删除旧关系
     * @param id
     */
    void deledeSetmealCheckgroup(Integer id);

    /**
     * 先判断 是否被订单使用了
     * @param id
     * @return
     */
    int findCountBySetmealId(int id);

    /**
     * 删除套餐
     * @param id
     */
    void deleteById(int id);

    /**
     * 查询数据库所有的图片
     * @return
     */
    List<String> findImgs();

    /**
     * 查询所有的套餐
     * @return
     */
    List<Setmeal> findAll();

    /**
     * 查询套餐下的检查组信息
     * @param id
     * @return
     */
    List<CheckGroup> findCheckGroupListBySetmealId(int id);

    /**
     * 查询检查组下的检查项信息
     * @param id
     * @return
     */
    List<CheckItem> findCheckItemByCheckGroupId(Integer id);

    /**
     * 调用服务查询套餐详情1
     * @param id
     * @return
     */
    Setmeal findDetailById(int id);
}
