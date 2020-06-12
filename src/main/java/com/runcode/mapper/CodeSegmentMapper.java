package com.runcode.mapper;

import com.runcode.entities.CodeSegmentPO;

/**
 * @author RhettPeng
 */
public interface CodeSegmentMapper {
    /**
     * 通过id查找一段代码
     * @param id 数据库中的id
     * @return CodeSegmentPO对象
     */
    CodeSegmentPO getById(Long id);

    /**
     * 保存到数据库
     * @param codeSegment 代码段对象
     * @return 插入数据的主键值
     */
    Long save(CodeSegmentPO codeSegment);
}
