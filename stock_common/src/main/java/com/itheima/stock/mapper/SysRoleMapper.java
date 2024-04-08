package com.itheima.stock.mapper;

import com.itheima.stock.pojo.entity.SysRole;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Entity com.itheima.stock.pojo.entity.SysRole
 */
public interface SysRoleMapper {

    int deleteByPrimaryKey(Long id);

    int insert(SysRole record);

    int insertSelective(SysRole record);

    SysRole selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SysRole record);

    int updateByPrimaryKey(SysRole record);

    /**
     * 查询所有角色信息
     * @return
     */
    List<SysRole> findAll();

    /**
     * 根据用户id查询角色信息
     * @param userId
     * @return
     */
    List<SysRole> getRoleByUserId(@Param("userId") Long userId);
}




