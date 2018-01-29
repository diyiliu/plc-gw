package com.tiza.gw.server;

import com.tiza.gw.handler.DtuHandler;
import com.tiza.gw.handler.codec.DtuDecoder;
import com.tiza.gw.handler.codec.DtuEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description: DtuServer
 * Author: DIYILIU
 * Update: 2018-01-26 10:38
 */
public class DtuServer extends Thread {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private int port;

    public void init(){

        this.start();
    }

    @Override
    public void run() {


        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();

            b.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1000)
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {

                            ch.pipeline().addLast(new DtuEncoder())
                                    .addLast(new DtuDecoder())
                                    .addLast(new DtuHandler());
                        }
                    });

            ChannelFuture f = b.bind(port).sync();
            logger.info("DTU网关服务器启动...");
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

    public void setPort(int port) {
        this.port = port;
    }
}
