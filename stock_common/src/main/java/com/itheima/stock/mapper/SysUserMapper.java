package com.itheima.stock.mapper;

import com.itheima.stock.pojo.entity.SysUser;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
* @author 46035
* @description 针对表【sys_user(用户表)】的数据库操作Mapper
* @createDate 2023-01-08 15:14:39
* @Entity com.itheima.stock.pojo.entity.SysUser
*/
//@CacheNamespace(blocking = true)
public interface SysUserMapper {

    int deleteByPrimaryKey(Long id);

    int insert(SysUser record);

    int insertSelective(SysUser record);

    SysUser selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SysUser record);

    int updateByPrimaryKey(SysUser record);

    SysUser findUserInfoByUserName(@Param("userName") String userName);

    /**
     * 查询所有用户信息
     * @return
     */
    List<SysUser> findAll();

}
