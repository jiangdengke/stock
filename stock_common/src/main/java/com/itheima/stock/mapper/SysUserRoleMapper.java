package com.itheima.stock.mapper;

import com.itheima.stock.pojo.entity.SysUserRole;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Entity com.itheima.stock.pojo.entity.SysUserRole
 */
public interface SysUserRoleMapper {

    int deleteByPrimaryKey(Long id);

    int insert(SysUserRole record);

    int insertSelective(SysUserRole record);

    SysUserRole selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SysUserRole record);

    int updateByPrimaryKey(SysUserRole record);

    /**
     * 根据用户id查询角色集合
     * @param userId
     * @return
     */
    List<Long> findRoleIdsByUserId(@Param("userId") Long userId);

    /**
     * 根据用户id删除关联的角色
     * @param userId
     * @return
     */
    int deleteByUserId(@Param("userId") Long userId);

    /**
     * 批量插入信息
     * @param list
     * @return
     */
    int insertBatch(@Param("urs") List<SysUserRole> list);
}




