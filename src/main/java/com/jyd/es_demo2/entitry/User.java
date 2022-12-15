package com.jyd.es_demo2.entitry;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: jyd
 * @Date: 2022/12/15 15:23
 * @Company:
 * @Version: 1.0
 * @Desc:
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private String name;
    private String sex;
    private Integer age;
    private String address;
}
