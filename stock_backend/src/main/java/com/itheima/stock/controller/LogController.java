package com.itheima.stock.controller;

import com.itheima.stock.service.LogService;
import com.itheima.stock.vo.req.LogPageReqVo;
import com.itheima.stock.vo.resp.PageResult;
import com.itheima.stock.vo.resp.R;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author by itheima
 * @Date 2021/12/23
 * @Description
 */
@RestController
@RequestMapping("/api")
public class LogController {

    @Autowired
    private LogService logService;


    /**
     * 日志信息综合查询
     * @param vo
     * @return
     */
    @PostMapping("/logs")
    public R<PageResult> logPageQuery(@RequestBody LogPageReqVo vo){

        return logService.logPageQuery(vo);
    }

    /**
     * 批量删除日志信息功能
     * @param logIds
     * @return
     */
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "body", dataType = "List<Long>", name = "logIds", value = "", required = true)
    })
    @ApiOperation(value = "批量删除日志信息功能", notes = "批量删除日志信息功能", httpMethod = "DELETE")
//    @StockLog("日志删除")
    @DeleteMapping("/log")
    @PreAuthorize("hasAuthority('sys:log:delete')")//权限表示与数据库定义的标识一致
    public R<String> deleteBatch(@RequestBody List<Long> logIds){
        return this.logService.deleteBatch(logIds);
    }

}
