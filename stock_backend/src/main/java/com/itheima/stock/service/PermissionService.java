package com.itheima.stock.service;

import com.itheima.stock.pojo.entity.SysPermission;
import com.itheima.stock.vo.req.PermissionAddVo;
import com.itheima.stock.vo.req.PermissionUpdateVo;
import com.itheima.stock.vo.resp.PermissionRespNodeTreeVo;
import com.itheima.stock.vo.resp.PermissionRespNodeVo;
import com.itheima.stock.vo.resp.R;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author by itheima
 * @Date 2021/12/22
 * @Description
 */
public interface PermissionService {
    R<List<PermissionRespNodeVo>> selectAllTree();

    /**
     * 查询所有权限集合
     * @return
     */
    R<List<SysPermission>> getAll();

    /**
     * 添加权限时，回显权限树
     * @return
     */
    R<List<PermissionRespNodeTreeVo>> getAllPermissionTreeExBt();

    /**
     * 权限添加按钮
     * @param vo
     * @return
     */
    R<String> addPermission(PermissionAddVo vo);

    /**
     * 更新权限
     * @param vo
     * @return
     */
    R<String> updatePermission(PermissionUpdateVo vo);

    /**
     * 根据权限id删除权限操作（逻辑删除）
     * @param permissionId
     * @return
     */
    R<String> removePermission(Long permissionId);

    /**
     * 根据用户id查询用户信息
     * @param userId
     * @return
     */
    List<SysPermission> getPermissionByUserId(@Param("userId") Long userId);

    /**
     * @param permissions 权限树状集合
     * @param pid 权限父id，顶级权限的pid默认为0
     * @param isOnlyMenuType true:遍历到菜单，  false:遍历到按钮
     * type: 目录1 菜单2 按钮3
     * @return
     */
    List<PermissionRespNodeVo> getTree(List<SysPermission> permissions,Long pid,boolean isOnlyMenuType);

}
