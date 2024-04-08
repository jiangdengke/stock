package com.itheima.stock.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itheima.stock.exception.BusinessException;
import com.itheima.stock.mapper.SysRoleMapper;
import com.itheima.stock.mapper.SysRolePermissionMapper;
import com.itheima.stock.pojo.entity.SysRole;
import com.itheima.stock.pojo.entity.SysRolePermission;
import com.itheima.stock.service.RoleService;
import com.itheima.stock.log.utils.IdWorker;
import com.itheima.stock.vo.req.RoleAddVo;
import com.itheima.stock.vo.req.RolePageReqVo;
import com.itheima.stock.vo.req.RoleUpdateVo;
import com.itheima.stock.vo.resp.PageResult;
import com.itheima.stock.vo.resp.R;
import com.itheima.stock.vo.resp.ResponseCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author by itheima
 * @Date 2021/12/22
 * @Description 角色服务接口实现
 */

@Service("roleService")
public class RoleServiceImpl implements RoleService {

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private SysRolePermissionMapper sysRolePermissionMapper;

    @Autowired
    private IdWorker idWorker;

    @Override
    public R<PageResult> quryPageRole(RolePageReqVo vo) {
        //1.组装分页参数
        PageHelper.startPage(vo.getPageNum(),vo.getPageSize());
        //2.调用mapper查询
        List<SysRole> roleList= this.sysRoleMapper.findAll();
        //3.封装PageResult
        PageResult<SysRole> pageResult = new PageResult<>(new PageInfo<>(roleList));
        return R.ok(pageResult);
    }

    /**
     * 添加角色和角色关联权限
     * @param vo
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<String> addRoleWithPermissions(RoleAddVo vo) {
        //1.组装角色
        //角色ID
        Long roleId=idWorker.nextId();
        SysRole role = SysRole.builder().id(roleId)
                .deleted(1).name(vo.getName()).description(vo.getDescription())
                .createTime(new Date()).updateTime(new Date()).status(1).build();
        //添加角色
        int addRoleCount = sysRoleMapper.insert(role);
        if (addRoleCount !=1) {
            throw new BusinessException(ResponseCode.ERROR.getMessage());
        }
        //2.批量添加角色关联权限集合
        List<Long> permissionsIds = vo.getPermissionsIds();
        if (!CollectionUtils.isEmpty(permissionsIds)) {
            List<SysRolePermission> rps = permissionsIds.stream().map(permissionId -> {
                SysRolePermission rp = SysRolePermission.builder().id(idWorker.nextId()).roleId(roleId)
                        .permissionId(permissionId).createTime(new Date()).build();
                return rp;
            }).collect(Collectors.toList());
            //批量插入角色权限集合
            int counts=this.sysRolePermissionMapper.addRolePermissionBatch(rps);
            if (counts==0) {
                throw new BusinessException(ResponseCode.ERROR.getMessage());
            }
        }
        return R.ok(ResponseCode.SUCCESS.getMessage());
    }

    /**
     * 根据角色id查找对应的权限id集合
     * @param roleId
     * @return
     */
    @Override
    public R<Set<String>> getPermissionIdsByRoleId(Long roleId) {
        //根据角色id查找权限id集合
        Set<String> permissionIds=this.sysRolePermissionMapper.getPermissionIdsByRoleId(roleId);
        permissionIds= permissionIds==null?new HashSet<>():permissionIds;
        return R.ok(permissionIds);
    }

    /**
     * 更新角色信息，包含角色关联的权限信息
     * @param vo
     * @return
     */
    @Override
    public R<String> updateRoleWithPermissions(RoleUpdateVo vo) {
        //1.更新角色信息
        SysRole role = SysRole.builder().id(vo.getId()).name(vo.getName()).updateTime(new Date())
                .description(vo.getDescription()).build();
        int updateCount = this.sysRoleMapper.updateByPrimaryKeySelective(role);
        if (updateCount!=1) {
            throw new BusinessException(ResponseCode.ERROR.getMessage());
        }
        //2.删除当前角色关联的权限信息
        sysRolePermissionMapper.deleteByRoleId(vo.getId());
        //3.插入当前角色权限信息
        List<Long> permissionsIds = vo.getPermissionsIds();
        if (!CollectionUtils.isEmpty(permissionsIds)) {
            List<SysRolePermission> rps = permissionsIds.stream().map(permissionId -> {
                SysRolePermission rp = SysRolePermission.builder().roleId(vo.getId()).permissionId(permissionId)
                        .createTime(new Date()).id(idWorker.nextId()).build();
                return rp;
            }).collect(Collectors.toList());
            this.sysRolePermissionMapper.addRolePermissionBatch(rps);
        }
        return R.ok(ResponseCode.SUCCESS.getMessage());
    }

    /**
     * 根据角色id删除角色信息
     * @param roleId
     * @return
     */
    @Override
    public R<String> deleteRoleById(Long roleId) {
        //逻辑删除，更新角色状态
        SysRole role = SysRole.builder().id(roleId).deleted(0).updateTime(new Date()).build();
        int count = this.sysRoleMapper.updateByPrimaryKeySelective(role);
        if (count!=1) {
            throw new BusinessException(ResponseCode.ERROR.getMessage());
        }
        return R.ok(ResponseCode.SUCCESS.getMessage());
    }

    /**
     * 更新用户的状态信息
     * @param roleId 角色id
     * @param status 状态 1.正常 0：启用
     * @return
     */
    @Override
    public R<String> updateRoleStatus(Long roleId, Integer status) {
        //组装数据
        SysRole role = SysRole.builder().id(roleId).status(status).updateTime(new Date()).build();
        //更新
        int count = this.sysRoleMapper.updateByPrimaryKeySelective(role);
        if (count!=1) {
            throw new BusinessException(ResponseCode.ERROR.getMessage());
        }
        return R.ok(ResponseCode.SUCCESS.getMessage());
    }
}
