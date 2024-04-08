package com.itheima.stock.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author by itheima
 * @Date 2021/12/27
 * @Description
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {

    private Integer id;

    private String userName;

    private String address;

}
