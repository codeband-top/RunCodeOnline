package com.runcode.controller;

import cn.hutool.json.JSONUtil;
import com.runcode.entities.CodeSegmentPO;
import com.runcode.entities.Result;
import com.runcode.entities.SaveCodeRequest;
import com.runcode.mapper.CodeSegmentMapper;
import com.runcode.utils.UserConfig;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.util.CharsetUtil;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Ignore;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 处理前端Rest请求
 * @author RhettPeng
 */
public class CodeSegmentController {
    private CodeSegmentMapper codeSegmentMapper;
    private final Pattern pattern = Pattern.compile("/codeSegment/(\\d+)");
    private static final String API_PATH = "/codeSegment";

    /**
     * 初始化Mapper
     */
    public CodeSegmentController(){
        // Mybatis配置
        InputStream mybatisConfig = null;
        try {
            // 初始化mapper
            mybatisConfig = new FileInputStream("config/mybatis-config.xml");
            SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
            SqlSessionFactory factory = builder.build(mybatisConfig);
            SqlSession session = factory.openSession(true);
            codeSegmentMapper = session.getMapper(CodeSegmentMapper.class);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * 处理前端请求
     * @param request
     * @return
     */
    @Ignore
    public Result handle(FullHttpRequest request){
        if(request.method() == HttpMethod.GET){
            // 使用正则表达式匹配请求
            Matcher matcher = pattern.matcher(request.uri());
            if(matcher.matches()){
                // 获取路径中的id
                String id = matcher.group(1);
                return getById(Long.valueOf(id));
            }
        }else if(request.method() == HttpMethod.POST){
            if(API_PATH.equals(request.uri())){
                SaveCodeRequest saveCodeRequest = JSONUtil.toBean(request.content().toString(CharsetUtil.UTF_8), SaveCodeRequest.class);
                if(!UserConfig.PASSWORD.equals(saveCodeRequest.getPassword())){
                    return Result.ERROR().msg("密码错误！");
                }
                return save(saveCodeRequest);
            }
        }
        return Result.ERROR();
    }

    /**
     * 根据id查询代码
     * @param id
     * @return
     */
    public Result getById(Long id){
        CodeSegmentPO codeSegment = codeSegmentMapper.getById(id);
        if(codeSegment == null) {
            return Result.ERROR();
        }
        return Result.OK()
                .data("lang", codeSegment.getCodeType())
                .data("content",codeSegment.getCodeContent());
    }

    /**
     * 保存一段代码
     * @param saveCodeRequest
     * @return
     */
    public Result save(SaveCodeRequest saveCodeRequest){
        CodeSegmentPO codeSegment = new CodeSegmentPO();
        codeSegment.setCodeContent(saveCodeRequest.getContent());
        codeSegment.setCodeType(saveCodeRequest.getLangType());
        codeSegmentMapper.save(codeSegment);
        long id = codeSegment.getId();
        return Result.OK().data("id", id);
    }
}
