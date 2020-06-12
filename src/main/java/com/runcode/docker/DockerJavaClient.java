package com.runcode.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.DockerCmdExecFactory;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import com.github.dockerjava.jaxrs.JerseyDockerCmdExecFactory;
import com.runcode.entities.CodeLang;
import com.runcode.utils.DockerConfig;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

/**
 * 控制Docker的Java客户端
 * @author RhettPeng
 */
@Slf4j
public class DockerJavaClient {
    /**
     * 计数器，用于给容器名取后缀
     */
    private static int counter = 0;


    /**
     * 获取一个docker连接
     * @return
     */
    public DockerClient getDockerClient(){
        DockerCmdExecFactory dockerCmdExecFactory = new JerseyDockerCmdExecFactory().withReadTimeout(20000)
                .withConnectTimeout(2000);
        DockerClientConfig config = null;

        config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost(DockerConfig.DOCKER_HOST)
                .withDockerTlsVerify(true)
                .withDockerCertPath(DockerConfig.DOCKER_CERT_PATH)
                .withDockerConfig(DockerConfig.DOCKER_CERT_PATH)
                .withRegistryUrl("https://index.docker.io/v1/")
                .withRegistryUsername(DockerConfig.REGISTRY_USER_NAME)
                .withRegistryPassword(DockerConfig.REGISTRY_PASSWORD)
                .withRegistryEmail(DockerConfig.REGISTRY_EMAIL)
                .build();

        return DockerClientBuilder.getInstance(config)
                .withDockerCmdExecFactory(dockerCmdExecFactory).build();
    }

    /**
     * 创建运行代码的容器
     * @param dockerClient
     * @param langType
     * @return
     */
    private String createContainer(DockerClient dockerClient,CodeLang langType){
        // 创建容器请求
        CreateContainerResponse containerResponse = dockerClient.createContainerCmd(langType.getImageName())
                .withName(langType.getContainerNamePrefix()+counter)
                .withWorkingDir(DockerConfig.DOCKER_CONTAINER_WORK_DIR)
                .withStdinOpen(true)
                .exec();

        return containerResponse.getId();
    }

    /**
     * 将程序代码写入容器中的一个文件
     * @param dockerClient
     * @param containerId
     * @param langType
     * @param sourcecode
     * @return
     * @throws InterruptedException
     */
    private String writeFileToContainer(DockerClient dockerClient,String containerId,CodeLang langType,String sourcecode) throws InterruptedException {
        String workDir = DockerConfig.DOCKER_CONTAINER_WORK_DIR;
        String fileName = langType.getFileName();
        String path = workDir + "/" + fileName;

        // 将\替换为\\\\，转义反斜杠
        sourcecode = sourcecode.replaceAll("\\\\", "\\\\\\\\\\\\\\\\");

        // 将"替换为\"，转义引号
        sourcecode = sourcecode.replaceAll("\\\"", "\\\\\"");

        // 通过重定向符写入文件，注意必须要带前面两个参数，否则重定向符会失效，和Docker CMD的机制有关
        ExecCreateCmdResponse createCmdResponse = dockerClient.execCreateCmd(containerId)
                .withCmd("/bin/sh","-c", "echo \""+sourcecode+"\" > "+path)
                .exec();
        dockerClient.execStartCmd(createCmdResponse.getId())
                .exec(new ExecStartResultCallback(System.out,System.err))
                .awaitCompletion();
        return fileName;
    }

    /**
     * 在容器上EXEC一条CMD命令
     * @param dockerClient docker客户端
     * @param command 命令，EXEC数组
     * @param containerId 容器ID
     * @param timeout 超时时间（单位为秒）
     * @param ctx
     * @param isFinal 是否是最后一条指令
     * @throws InterruptedException
     */
    private void runCommandOnContainer(DockerClient dockerClient, String[] command, String containerId,
                                       int timeout,ChannelHandlerContext ctx,boolean isFinal) throws InterruptedException {
        ExecCreateCmdResponse createCmdResponse = dockerClient.execCreateCmd(containerId)
                .withAttachStdout(true)
                .withAttachStderr(true)
                .withCmd(command)
                .exec();
        dockerClient.execStartCmd(createCmdResponse.getId())
                .exec(new RunCodeResultCallback(ctx,isFinal))
                //.awaitCompletion();
                .awaitCompletion(timeout,TimeUnit.SECONDS);
    }

    /**
     * 执行一个程序
     * @param langType 编程语言类型
     * @param sourcecode 源代码
     * @throws InterruptedException
     * @throws IOException
     */
    public void exec(CodeLang langType, String sourcecode, ChannelHandlerContext ctx){
        DockerClient dockerClient = getDockerClient();
        // 计数器加一
        counter++;
        log.info("开始创建容器");
        // 创建容器
        String containerId = createContainer(dockerClient, langType);
        log.info("创建容器结束");
        log.info("开始运行容器");
        // 运行容器
        dockerClient.startContainerCmd(containerId).exec();

        try {
            log.info("开始写文件");
            writeFileToContainer(dockerClient, containerId, langType, sourcecode);
            log.info("写文件结束");
            String[][] commands = langType.getExecCommand(langType.getFileName());
            for(int i = 0;i<commands.length;i++){
                log.info("开始执行第"+(i+1)+"条指令");
                runCommandOnContainer(dockerClient, commands[i], containerId, 10, ctx,i==commands.length-1);
                log.info("执行第"+(i+1)+"条指令结束");
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            log.info("开始移除容器");
            // 移除容器
            dockerClient.killContainerCmd(containerId).exec();
            dockerClient.removeContainerCmd(containerId).exec();
            counter--;
            try {
                dockerClient.close();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
            log.info("移除容器结束");
        }
    }

}
