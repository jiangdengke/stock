package com.itheima.stock.vo.resp;

import com.itheima.stock.pojo.entity.SysRole;
import lombok.Data;

import java.util.List;

/**
 * @author by itheima
 * @Date 2021/12/21
 * @Description 响应用户角色信息
 */
@Data
public class UserOwnRoleRespVo {
    /**
     * 用户用户的角色id集合
     */
    private List<Long> ownRoleIds;
    /**
     * 所有角色集合
     */
    private List<SysRole> allRole;
}
