package com.runcode.entities;

import lombok.Data;

/**
 * 保存代码请求
 * @author RhettPeng
 */
@Data
public class SaveCodeRequest {

    /**
     * 编程语言类型
     */
    private String langType;
    /**
     * 代码内容
     */
    private String content;

    /**
     * 密码
     */
    private String password;
}
