package com.runcode.entities;

import lombok.Data;

import java.util.Date;

/**
 * 数据库存储的对象
 * @author RhettPeng
 */
@Data
public class CodeSegmentPO {
    public Long id;
    /**
     * 源代码
     */
    public String codeContent;
    /**
     * 编程语言类型
     */
    public String codeType;
    /**
     * 创建时间
     */
    public Date gmtCreate;
}
