package com.itheima.stock.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Strings;
import com.itheima.stock.constant.StockConstant;
import com.itheima.stock.exception.BusinessException;
import com.itheima.stock.mapper.SysRoleMapper;
import com.itheima.stock.mapper.SysUserMapperExt;
import com.itheima.stock.mapper.SysUserRoleMapper;
import com.itheima.stock.pojo.domain.UserQueryDomain;
import com.itheima.stock.pojo.entity.SysPermission;
import com.itheima.stock.pojo.entity.SysRole;
import com.itheima.stock.pojo.entity.SysUser;
import com.itheima.stock.pojo.entity.SysUserRole;
import com.itheima.stock.service.PermissionService;
import com.itheima.stock.service.UserServiceExt;
import com.itheima.stock.log.utils.IdWorker;
import com.itheima.stock.vo.req.*;
import com.itheima.stock.vo.resp.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author by itheima
 * @Date 2021/12/21
 * @Description 定义用户信息操作服务实现
 */
@Service("userServiceExt")
public class UserServiceExtImpl implements UserServiceExt {

    @Autowired
    private SysUserMapperExt sysUserMapperExt;

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 用户登录功能
     * @param vo
     * @return
     */
    @Override
    public R<LoginRespVoExt> login(LoginReqVo vo) {
        //1.判断参数是否合法
        if (vo==null || Strings.isNullOrEmpty(vo.getUsername())
                || Strings.isNullOrEmpty(vo.getPassword())
                || Strings.isNullOrEmpty(vo.getSessionId()) || Strings.isNullOrEmpty(vo.getCode())) {
            return R.error(ResponseCode.USERNAME_OR_PASSWORD_ERROR.getMessage());
        }
        //从程序执行的效率看，先进行校验码的校验，成本较低
        //补充：根据传入的rkye从redis中获取校验码
        String rCheckCode =(String) redisTemplate.opsForValue().get(StockConstant.CHECK_PREFIX + vo.getSessionId());
        if (rCheckCode==null || ! rCheckCode.equalsIgnoreCase(vo.getCode())) {
            //响应验证码输入错误
            return R.error(ResponseCode.CHECK_CODE_ERROR.getMessage());
        }
        //是否需要添加手动淘汰redis缓存的数据,如果想快速淘汰，则可手动删除
        redisTemplate.delete("CK:" + vo.getSessionId());
        //2.根据用户名查询用户信息
        SysUser info= sysUserMapperExt.findUserByUserName(vo.getUsername());
        //3.判断查询的用户信息
        if (info==null) {
            return R.error(ResponseCode.ACCOUNT_NOT_EXISTS.getMessage());
        }
        //4.判断输入的明文密码与数据库的密文密码进行匹配
        boolean isSuccess = passwordEncoder.matches(vo.getPassword(), info.getPassword());
        //4.1 如果密码匹配不成功
        if (! isSuccess) {
            return R.error(ResponseCode.ACCOUNT_NOT_EXISTS.getMessage());
        }
        //4.2 成功则返回用户的正常信息
        LoginRespVoExt respVo = new LoginRespVoExt();
//        respVo.setId(info.getId());
//        respVo.setNickName(info.getNickName());
        //保证复制的双方具有共同的属性名称
        BeanUtils.copyProperties(info,respVo);
        //获取指定用户的权限集合 添加获取侧边栏数据和按钮权限的结合信息
        List<SysPermission> permissions = permissionService.getPermissionByUserId(info.getId());
        //获取树状权限菜单数据
        List<PermissionRespNodeVo> tree = permissionService.getTree(permissions, 0l, true);
        //获取菜单按钮集合
        List<String> authBtnPerms = permissions.stream()
                .filter(per -> !Strings.isNullOrEmpty(per.getCode()) && per.getType() == 3)
                .map(per -> per.getCode()).collect(Collectors.toList());
        respVo.setMenus(tree);
        respVo.setPermissions(authBtnPerms);
        //TODO 后期借助jwt生成token
        respVo.setAccessToken(info.getId()+":"+info.getUsername());
        return R.ok(respVo);
    }

    /**
     * 多条件综合查询用户分页信息，条件包含：分页信息 用户创建日期范围
     * @param vo
     * @return
     */
    @Override
    public R<PageResult> pageUsers(UserPageReqVo vo) {
        //组装分页数据
        PageHelper.startPage(vo.getPageNum(),vo.getPageSize());
        //设置查询条件
        List<UserQueryDomain> users= this.sysUserMapperExt.pageUsers(vo.getUsername(),vo.getNickName(),vo.getStartTime(),vo.getEndTime());
        if (CollectionUtils.isEmpty(users)) {
            return R.error("没有数据");
        }
        PageResult<UserQueryDomain> pageResult = new PageResult<>(new PageInfo<>(users));
        return R.ok(pageResult);
    }

    /**
     * 添加用户信息1
     * @param vo
     * @return
     */
    @Override
    public R<String> addUser(UserAddReqVo vo) {
        //1.判断当前账户username是否已被使用
        SysUser dbUser= this.sysUserMapperExt.findUserByUserName(vo.getUsername());
        if (dbUser!=null) {
            //抛出业务异常 等待全局异常处理器统一处理
            throw new BusinessException(ResponseCode.ACCOUNT_EXISTS_ERROR.getMessage());
        }
        //2.否则添加
        //封装用户信息
        SysUser user = new SysUser();
        BeanUtils.copyProperties(vo,user);
        //设置用户id
        user.setId(idWorker.nextId());
        //密码加密
//        user.setPassword(passwordEncoder.encode(user.getPassword()));
        //设置添加时间和更新时间
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        //是否删除
        user.setDeleted(1);
        //TODO 获取当前操作用户的id
        int count = this.sysUserMapperExt.insert(user);
        if (count!=1) {
            throw new BusinessException(ResponseCode.ERROR.getMessage());
        }
        return R.ok(ResponseCode.SUCCESS.getMessage());
    }

    /**
     * 更新用户信息
     * @param vo
     * @return
     */
    @Override
    public R<String> updateUser(UserEditReqVO vo) {
        SysUser user = new SysUser();
        BeanUtils.copyProperties(vo,user);
        //TODO 设置更新者ID
        //设置更新时间
        user.setUpdateTime(new Date());
        //更新用户信息
        int count = this.sysUserMapperExt.updateByPrimaryKeySelective(user);
        if (count!=1) {
            throw new BusinessException(ResponseCode.ERROR.getMessage());
        }
        return R.ok(ResponseCode.SUCCESS.getMessage());
    }

    /**
     * 获取用户具有的角色信息，以及所有角色信息
     * @param userId
     * @return
     */
    @Override
    public R<UserOwnRoleRespVo> getUserOwnRole(Long userId)  {
        //1.获取当前用户所拥有的角色id集合
        List<Long> roleIds= this.sysUserRoleMapper.findRoleIdsByUserId(userId);
        //2.获取所有角色信息
        List<SysRole> roles= this.sysRoleMapper.findAll();
        //3.封装数据
        UserOwnRoleRespVo vo = new UserOwnRoleRespVo();
        vo.setOwnRoleIds(roleIds);
        vo.setAllRole(roles);
        return R.ok(vo);
    }

    /**
     * 更新用户角色信息
     * @param vo
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<String> updateUserOwnRoles(UserOwnRoleReqVo vo) {
        //1.判断用户id是否存在
        if (vo.getUserId()==null) {
            throw new BusinessException(ResponseCode.DATA_ERROR.getMessage());
        }
        //2.删除用户原来所拥有的角色id
        this.sysUserRoleMapper.deleteByUserId(vo.getUserId());
        //如果对应集合为空，则说明用户将所有角色都清除了
        if (CollectionUtils.isEmpty(vo.getRoleIds())) {
            return R.ok(ResponseCode.SUCCESS.getMessage());
        }
        //封装用户角色对象集合

        List<SysUserRole> list = vo.getRoleIds().stream().map(roleId -> {
            SysUserRole userRole = SysUserRole.builder().
                    userId(vo.getUserId()).roleId(roleId).
                    createTime(new Date()).id(idWorker.nextId()).build();
            return userRole;
        }).collect(Collectors.toList());
        //批量插入
        int count= this.sysUserRoleMapper.insertBatch(list);
        if (count==0) {
            throw new BusinessException(ResponseCode.ERROR.getMessage());
        }
        return R.ok(ResponseCode.SUCCESS.getMessage());
    }

    /**
     * 批量删除用户信息
     * @param userIds
     * @return
     */
    @Override
    public R<String> deleteUsers(List<Long> userIds) {
        //判断集合是否为空
        if (CollectionUtils.isEmpty(userIds)) {
            throw new BusinessException(ResponseCode.DATA_ERROR.getMessage());
        }
        //删除用户未逻辑删除
        int result=this.sysUserMapperExt.updateUserStatus4Deleted(userIds);
        if (result==0) {
            throw new BusinessException(ResponseCode.ERROR.getMessage());
        }
        return R.ok(ResponseCode.SUCCESS.getMessage());
    }

    /**
     * 根据用户ID查询用户细腻
     * @param id 用户id
     * @return
     */
    @Override
    public R<UserInfoRespVo> getUserInfo(Long id) {
        if (id==null) {
            return R.error(ResponseCode.DATA_ERROR.getMessage());
        }
        SysUser user = this.sysUserMapperExt.selectByPrimaryKey(id);
        UserInfoRespVo userInfo = new UserInfoRespVo();
        BeanUtils.copyProperties(user,userInfo);
        return R.ok(userInfo);
    }
}
