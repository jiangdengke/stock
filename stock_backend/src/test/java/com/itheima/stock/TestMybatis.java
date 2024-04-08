package com.itheima.stock;

import com.itheima.stock.mapper.SysUserMapper;
import com.itheima.stock.pojo.entity.SysUser;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author : itheima
 * @date : 2023/1/29 10:51
 * @description :
 */
@SpringBootTest
public class TestMybatis {

//    @Autowired
//    private SysUserMapper sysUserMapper;

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    /**
     * @desc 测试一级级缓存
     */
    @Test
    public void test1() {
        SqlSession session = sqlSessionFactory.openSession(true);
//        SqlSession session2 = sqlSessionFactory.openSession(true);
        SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
//        SysUserMapper sysUserMapper2 = session2.getMapper(SysUserMapper.class);
        //存入一级缓存
        SysUser admin = sysUserMapper.findUserInfoByUserName("admin");
        //直接从一级缓存拿数据
        SysUser admin2 = sysUserMapper.findUserInfoByUserName("admin");
//        SysUser admin2 = sysUserMapper2.findUserInfoByUserName("admin");
    }
    /**
     * @desc 测试二级缓存 xml中添加<cache/>
     */
    @Test
    public void test() {
        SqlSession session = sqlSessionFactory.openSession(true);
        SysUserMapper sysUserMapper = session.getMapper(SysUserMapper.class);
        //数据存入session一级缓存
        SysUser admin = sysUserMapper.findUserInfoByUserName("admin");
        //关闭session，强迫一级缓存数据同步到二级缓存中
        session.close();
        SqlSession session2 = sqlSessionFactory.openSession(true);
        SysUserMapper sysUserMapper2 = session2.getMapper(SysUserMapper.class);
        //从二级缓存获取数据，不会执行sql语句
        SysUser admin2 = sysUserMapper2.findUserInfoByUserName("admin");
        //执行删除操作，会导致无论是一级缓存还是二级缓存都清除
        sysUserMapper2.deleteByPrimaryKey(123124124l);
        SysUser admin3 = sysUserMapper2.findUserInfoByUserName("admin");
    }



}
