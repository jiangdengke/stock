package com.itheima.stock.vo.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermissionRespNodeVo {

    /**
     * 角色ID
     */
    private Long id;
    /**
     * 角色标题
     */
    private String title;
    /**
     * 角色图标
     */
    private String icon;
    /**
     * 路由地址URL
     */
    private String path;

    /**
     * 路由名称
     */
    private String name;

    /**
     * 菜单数结构
     */
    private List<PermissionRespNodeVo> children;

}
