package com.runcode.entities;

import lombok.Data;

/**
 * 前端发来的websocket请求
 * @author RhettPeng
 */
@Data
public class CodeWrapperDTO {
    /**
     * 编程语言类型
    */
    private String langType;
    /**
     * 代码内容
     */
    private String content;
}
