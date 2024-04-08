package com.itheima.stock.mapper;

import com.itheima.stock.pojo.entity.SysLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Entity com.itheima.stock.pojo.entity.SysLog
 */
public interface SysLogMapper {

    int deleteByPrimaryKey(Long id);

    int insert(SysLog record);

    int insertSelective(SysLog record);

    SysLog selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SysLog record);

    int updateByPrimaryKey(SysLog record);

    /**
     * 多添加查询
     * @param username 操作者账户名称
     * @param operation 操作类型
     * @param startTime 起始时间
     * @param endTime 截止时间
     * @return
     */
    List<SysLog> findByCondition(@Param("username") String username, @Param("operation") String operation,
                                 @Param("startTime") String startTime, @Param("endTime") String endTime);

    /**
     * 根据角色id批量删除用户信息
     * @param logIds
     */
    int deleteBatchByLogIds(@Param("logIds") List<Long> logIds);
}




