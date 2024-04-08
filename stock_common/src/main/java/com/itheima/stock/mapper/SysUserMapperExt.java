package com.itheima.stock.mapper;

import com.itheima.stock.pojo.domain.UserQueryDomain;
import com.itheima.stock.pojo.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Entity com.itheima.stock.pojo.entity.SysUser
 */
@Mapper
public interface SysUserMapperExt {

    int deleteByPrimaryKey(Long id);

    int insert(SysUser record);

    int insertSelective(SysUser record);

    SysUser selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SysUser record);

    int updateByPrimaryKey(SysUser record);

    /**
     * 根据用户名 昵称 以及时间范围综合查询1
     * @param userName 用户名
     * @param nickName 昵称
     * @param startTime 起始创建时间
     * @param endTime 最终创建日期
     * @return
     */
    List<UserQueryDomain> pageUsers(@Param("userName") String userName, @Param("nickName") String nickName,
                                    @Param("startTime") String startTime, @Param("endTime") String endTime);

    /**
     * 根据账户名称查询账户信息1
     * @param username 账户名称
     * @return
     */
    SysUser findUserByUserName(@Param("userName") String username);

    /**
     * 逻辑删除用户信息
     * @param userIds
     * @return
     */
    int updateUserStatus4Deleted(@Param("userIds") List<Long> userIds);
}




