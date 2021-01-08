package com.itheima.health.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.StringUtil;
import com.itheima.health.dao.CheckGroupDao;
import com.itheima.health.entity.PageResult;
import com.itheima.health.entity.QueryPageBean;
import com.itheima.health.exception.MyException;
import com.itheima.health.pojo.CheckGroup;
import com.itheima.health.service.CheckGroupService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service(interfaceClass = CheckGroupService.class)
public class CheckGroupServiceImpl implements CheckGroupService {
    @Resource
    private CheckGroupDao checkGroupDao;

    /**
     * 添加检查组
     *
     * @param checkGroup  检查组信息
     * @param checkitemIds 选中的检查项id数组
     */
    @Override
    @Transactional
    public void add(CheckGroup checkGroup, Integer[] checkitemIds) {
        //- 先添加检查组
        checkGroupDao.add(checkGroup);
        //- 获取检查组的id
        Integer checkGroupId = checkGroup.getId();
        //- 遍历选中的检查项id的数组
        if (null != checkitemIds) {
            for (Integer checkitemId : checkitemIds) {
                //- 添加检查组与检查项的关系
                checkGroupDao.addCheckGroupCheckItem(checkGroupId,checkitemId);
            }
        }
    }


    /**
     * 检查组的分页查询
     * @param queryPageBean
     * @return
     */
    @Override
    public PageResult<CheckGroup> findPage(QueryPageBean queryPageBean) {
        PageHelper.startPage(queryPageBean.getCurrentPage(),queryPageBean.getPageSize());
        // 条件查询
        if (StringUtil.isNotEmpty(queryPageBean.getQueryString())) {
            // 有查询条件， 模糊查询
            queryPageBean.setQueryString("%"+queryPageBean.getQueryString()+"%");
        }
        // page extends arrayList
        Page<CheckGroup> page = checkGroupDao.findByCondition(queryPageBean.getQueryString());
        PageResult<CheckGroup> pageResult = new PageResult<>(page.getTotal(), page.getResult());
        return pageResult;

    }


    /**
     * 通过id查询检查组
     * @param id
     * @return
     */
    @Override
    public CheckGroup findById(int id) {
        return checkGroupDao.findById(id);
    }

    /**
     * 通过检查组id查询选中的检查项id
     * @param id
     * @return
     */
    @Override
    public List<Integer> findCheckItemIdsByCheckGroupId(int id) {
        return checkGroupDao.findCheckItemIdsByCheckGroupId(id);
    }

    /**
     * 修改检查组
     * @param checkGroup
     * @param checkitemIds
     */
    @Override
    @Transactional
    public void update(CheckGroup checkGroup, Integer[] checkitemIds) {
        //- 先更新检查组
        checkGroupDao.update(checkGroup);
        //- 先删除旧关系
        checkGroupDao.deleteCheckGroupCheckItem(checkGroup.getId());

        for (Integer checkitemId : checkitemIds) {
            //- 添加检查组与检查项的关系
            checkGroupDao.addCheckGroupCheckItem(checkGroup.getId(),checkitemId);
        }
    }


    /**
     * 通过id删除检查组
     * @param id
     * @throws MyException
     */
    @Override
    @Transactional
    public void deleteById(int id) throws MyException {
        // 通过检查组id查询是否被套餐使用了
        int count = checkGroupDao.findCountByCheckGroupId(id);
        if (count > 0) {
            throw new MyException("该检查组被套餐使用了，不能删除");
        }
        // 没使用，删除检查组与检查项的关系
        checkGroupDao.deleteCheckGroupCheckItem(id);
        // 删除检测组
        checkGroupDao.deleteById(id);

    }


}
