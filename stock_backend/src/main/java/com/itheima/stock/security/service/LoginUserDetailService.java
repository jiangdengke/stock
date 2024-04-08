package com.itheima.stock.security.service;

import com.google.common.base.Strings;
import com.itheima.stock.mapper.SysRoleMapper;
import com.itheima.stock.mapper.SysUserMapperExt;
import com.itheima.stock.pojo.entity.SysPermission;
import com.itheima.stock.pojo.entity.SysRole;
import com.itheima.stock.pojo.entity.SysUser;
import com.itheima.stock.security.user.LoginUserDetail;
import com.itheima.stock.service.PermissionService;
import com.itheima.stock.service.RoleService;
import com.itheima.stock.vo.resp.PermissionRespNodeVo;
import com.itheima.stock.vo.resp.R;
import com.itheima.stock.vo.resp.ResponseCode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author jiangDk
 * @date 2024/4/6 19:48
 * @description 定义获取用户详情服务bean
 */
@Service
public class LoginUserDetailService implements UserDetailsService {
    @Autowired
    private SysUserMapperExt sysUserMapperExt;
    @Autowired
    private PermissionService permissionService;

    @Autowired
    private SysRoleMapper sysRoleMapper;
    /**
     * 根据传入的用户的名称获取用户相关详情信息，密文密码，权限集合等。
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser dbUser= sysUserMapperExt.findUserByUserName(username);
        if (dbUser==null) {
            throw  new UsernameNotFoundException("用户不存在");
        }
        //获取指定用户的权限集合 添加获取侧边栏数据和按钮权限的结合信息
        List<SysPermission> permissions = permissionService.getPermissionByUserId(dbUser.getId());
        List<SysRole> roles = sysRoleMapper.getRoleByUserId(dbUser.getId());
        //获取树状权限菜单数据
        List<PermissionRespNodeVo> menus = permissionService.getTree(permissions, 0l, true);
        //获取菜单按钮集合
        List<String> authBtnPerms = permissions.stream()
                .filter(per -> !Strings.isNullOrEmpty(per.getCode()) && per.getType() == 3)
                .map(per -> per.getCode()).collect(Collectors.toList());
        //获取springSecurity的权限标识
        ArrayList<String> ps = new ArrayList<>();
        List<String> pers = permissions.stream().filter(per -> StringUtils.isNotBlank(per.getPerms()))
                .map(per -> per.getPerms())
                .collect(Collectors.toList());
        ps.addAll(pers);
        List<String> rs = roles.stream().map(r->"ROLE_"+r.getName()).collect(Collectors.toList());
        ps.addAll(rs);
        //将用户拥有的权限表示转权限对象
        String[] psArray = ps.toArray(new String[pers.size()]);
        List<GrantedAuthority> authorityList = AuthorityUtils.createAuthorityList(psArray);
        //构建用户详情服务对象
        LoginUserDetail userDetail = new LoginUserDetail();
        BeanUtils.copyProperties(dbUser,userDetail);
        userDetail.setMenus(menus);
        userDetail.setPermissions(authBtnPerms);
        userDetail.setAuthorities(authorityList);
        return userDetail;
    }
}
