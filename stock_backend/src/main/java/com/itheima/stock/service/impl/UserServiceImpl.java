package com.itheima.stock.service.impl;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import com.itheima.stock.constant.StockConstant;
import com.itheima.stock.mapper.SysUserMapper;
import com.itheima.stock.pojo.entity.SysUser;
import com.itheima.stock.service.UserService;
import com.itheima.stock.log.utils.IdWorker;
import com.itheima.stock.vo.req.LoginReqVo;
import com.itheima.stock.vo.resp.LoginRespVo;
import com.itheima.stock.vo.resp.R;
import com.itheima.stock.vo.resp.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author : itheima
 * @date : 2023/1/8 16:08
 * @description : 定义用户服务实现
 */
@Service("userService")
@Slf4j
public class UserServiceImpl implements UserService {


    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 根据用户名查询用户信息
     * @param userName
     * @return
     */
    @Override
    public SysUser findByUserName(String userName) {
        return sysUserMapper.findUserInfoByUserName(userName);
    }

    /**
     * 用户登录功能
     * @param vo
     * @return
     */
    @Override
    public R<LoginRespVo> login(LoginReqVo vo) {
        //1.判断参数是否合法
        if (vo==null || StringUtils.isBlank(vo.getUsername()) || StringUtils.isBlank(vo.getPassword())) {
            return R.error(ResponseCode.DATA_ERROR);
        }
        //判断输入的验证码是否存在
        if (StringUtils.isBlank(vo.getCode()) || StringUtils.isBlank(vo.getSessionId())){
            return R.error(ResponseCode.CHECK_CODE_ERROR);
        }
        //判断redis中保存的验证码与输入的验证码是否相同（比较时忽略大小写）
        String redisCode = (String) redisTemplate.opsForValue().get(StockConstant.CHECK_PREFIX + vo.getSessionId());
        if (StringUtils.isBlank(redisCode)) {
            //验证码过期
            return R.error(ResponseCode.CHECK_CODE_TIMEOUT);
        }
        if (! redisCode.equalsIgnoreCase(vo.getCode())) {
            //验证码错误
            return R.error(ResponseCode.CHECK_CODE_ERROR);
        }

        //2.根据用户名去数据库查询用户信息，获取密码的密文
        SysUser dbUser = sysUserMapper.findUserInfoByUserName(vo.getUsername());
        if (dbUser==null) {
            //用户不存在
            return R.error(ResponseCode.ACCOUNT_NOT_EXISTS);
        }
        //3.调用密码匹配器匹配输入的明文密码和数据库的密文密码
        if (!passwordEncoder.matches(vo.getPassword(),dbUser.getPassword())) {
            return R.error(ResponseCode.USERNAME_OR_PASSWORD_ERROR);
        }
        //4.响应
        LoginRespVo respVo = new LoginRespVo();
        respVo.setUsername(dbUser.getUsername());
        respVo.setNickName(dbUser.getNickName());
        respVo.setPhone(dbUser.getPhone());
        respVo.setId(dbUser.getId());
        //发现LoginRespVo与SysUser对象属性名称和类型一致
        //必须保证属性名称和类型一致
        BeanUtils.copyProperties(dbUser,respVo);
        return R.ok(respVo);
    }

    /**
     * 生成图片验证码功能
     * @return
     */
    @Override
    public R<Map> getCaptchaCode() {
        //1.生成图片验证码
        /*
          参数1：图片的宽度
          参数2：图片高度
          参数3：图片中包含验证码的长度
          参数4：干扰线数量
         */
        LineCaptcha captcha = CaptchaUtil.createLineCaptcha(250, 40, 4, 5);
        //设置背景颜色
        captcha.setBackground(Color.LIGHT_GRAY);
        //自定义生成校验码的规则
//        captcha.setGenerator(new CodeGenerator() {
//            @Override
//            public String generate() {
//                //自定义校验码生成的逻辑
//                return null;
//            }
//
//            @Override
//            public boolean verify(String code, String userInputCode) {
//                //匹配校验码逻辑
//                return false;
//            }
//        });

        //获取校验码
        String checkCode = captcha.getCode();
        //获取经过base64编码处理的图片数据
        String imageData = captcha.getImageBase64();
        //2.生成sessionId 转化成string，避免前端精度丢失
        String sessionId = String.valueOf(idWorker.nextId());
        log.info("当前生成的图片校验码：{}，会话id:{}",checkCode,sessionId);
        //3.将sessinId作为key，校验码作为value保存在redis中，（使用redis模拟sessioin的行为，通过过期时间设置）
        redisTemplate.opsForValue().set(StockConstant.CHECK_PREFIX +sessionId,checkCode,5, TimeUnit.MINUTES);
        //4.组装数据
        Map<String,String> data=new HashMap();
        data.put("imageData",imageData);
        data.put("sessionId",sessionId);
        //5.响应数据
        return R.ok(data);
    }
}
