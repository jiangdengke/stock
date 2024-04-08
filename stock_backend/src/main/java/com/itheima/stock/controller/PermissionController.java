package com.itheima.stock.controller;

import com.itheima.stock.pojo.entity.SysPermission;
import com.itheima.stock.service.PermissionService;
import com.itheima.stock.vo.req.PermissionAddVo;
import com.itheima.stock.vo.req.PermissionUpdateVo;
import com.itheima.stock.vo.resp.PermissionRespNodeTreeVo;
import com.itheima.stock.vo.resp.PermissionRespNodeVo;
import com.itheima.stock.vo.resp.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author by itheima
 * @Date 2021/12/22
 * @Description
 */
@RestController
@RequestMapping("/api")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    /**
     * 权限数据显示（仅仅包含id title childen）
     * @return
     */
    @GetMapping("/permissions/tree/all")
    public R<List<PermissionRespNodeVo>> getAllPermissionTree(){
        return permissionService.selectAllTree();
    }

    /**
     * 查询所有权限集合
     * @return
     */
    @GetMapping("/permissions")
    public R<List<SysPermission>>  getAll(){
        return permissionService.getAll();
    }

    /**
     * 添加权限时回显权限树
     * @return
     */
    @GetMapping("/permissions/tree")
    public R<List<PermissionRespNodeTreeVo>>  getAllPermissionTreeExBt(){
        return permissionService.getAllPermissionTreeExBt();
    }

    /**
     * 权限添加按钮
     * @param vo
     * @return
     */
    @PostMapping("/permission")
    public R<String> addPermission(@RequestBody PermissionAddVo vo){
        return permissionService.addPermission(vo);
    }

    /**
     * 更新权限
     * @param vo
     * @return
     */
    @PutMapping("/permission")
    public R<String> updatePermission(@RequestBody PermissionUpdateVo vo){
        return permissionService.updatePermission(vo);
    }

    @DeleteMapping("/permission/{permissionId}")
    public R<String> removePermission(@PathVariable("permissionId") Long permissionId){
        return permissionService.removePermission(permissionId);
    }
}
