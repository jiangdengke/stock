package com.itheima.stock.mapper;

import com.itheima.stock.pojo.entity.SysPermission;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Entity com.itheima.stock.pojo.entity.SysPermission
 */
public interface SysPermissionMapper {

    int deleteByPrimaryKey(Long id);

    int insert(SysPermission record);

    int insertSelective(SysPermission record);

    SysPermission selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SysPermission record);

    int updateByPrimaryKey(SysPermission record);

    /**
     * 获取所有权限集合
     * @return
     */
    List<SysPermission> findAll();

    /**
     * 根据权限父类id查询对应子集权限
     * @param permissionId
     * @return
     */
    int findChildrenCountByParentId(@Param("permissionId") Long permissionId);

    /**
     * 根据用户id查询用户信息
     * @param userId
     * @return
     */
    List<SysPermission> getPermissionByUserId(@Param("userId") Long userId);
}




