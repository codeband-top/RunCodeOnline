package com.runcode.entities;

import cn.hutool.core.util.IdUtil;

/**
 * 各种编程语言的枚举
 * @author RhettPeng
 */

public enum CodeLang {
    /**
     * Python语言
     */
    PYTHON3{
        @Override
        public String getImageName() {
            return "python:3";
        }

        @Override
        public String getContainerNamePrefix() {
            return "python-running-script-";
        }

        @Override
        public String[][] getExecCommand(String fileName) {
            return new String[][]{{"python",fileName}};
        }

        @Override
        public String getFileName() {
            return "temp.py";
        }
    },
    /**
     * C++语言
     */
    CPP{
        @Override
        public String getImageName() {
            return "gcc:7.3";
        }

        @Override
        public String getContainerNamePrefix() {
            return "cpp-running-file-";
        }

        @Override
        public String[][] getExecCommand(String fileName) {
            return new String[][]{{"g++",fileName,"-o","temp"},{"./temp"}};
        }

        @Override
        public String getFileName() {
            return "temp.cpp";
        }
    },
    /**
     * JAVA语言
     */
    JAVA{
        @Override
        public String getImageName() {
            return "openjdk:11";
        }

        @Override
        public String getContainerNamePrefix() {
            return "java-running-file-";
        }
        
        @Override
        public String[][] getExecCommand(String fileName) {
            // jdk11可以不经过javac
            return new String[][]{{"java",fileName}};
        }

        @Override
        public String getFileName() {
            return "Untitled.java";
        }

    },
    /**
     * Go语言
     */
    GOLANG{
        @Override
        public String getImageName() {
            return "golang:1.14";
        }

        @Override
        public String getContainerNamePrefix() {
            return "golang-running-file-";
        }

        @Override
        public String[][] getExecCommand(String fileName) {
            // Go可不经过编译
            return new String[][]{{"go","run",fileName}};
        }

        @Override
        public String getFileName() {
            return "temp.go";
        }

    };

    /**
     * 镜像名称
     */
    public String getImageName(){
        return null;
    }

    /**
     * 容器名称前缀
     */
    public String getContainerNamePrefix(){
        return null;
    }

    /**
     * 执行该文件需要的指令，供Docker EXEC调用
     */
    public String[][] getExecCommand(String fileName){
        return null;
    }

    /**
     * 该编程语言的文件名
     */
    public String getFileName(){return null;}
}
