package netty.server

import io.netty.bootstrap.ServerBootstrap
import io.netty.buffer.Unpooled
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.channel.{ChannelHandlerContext, ChannelInboundHandlerAdapter, ChannelInitializer, ChannelOption}
import io.netty.handler.codec.string.StringEncoder
import io.netty.handler.codec.{DelimiterBasedFrameDecoder, LengthFieldBasedFrameDecoder, LineBasedFrameDecoder}
import io.netty.handler.timeout.ReadTimeoutHandler

class SimpleServer {

  val server = new ServerBootstrap()
  val (group, childGroup) = (new NioEventLoopGroup(), new NioEventLoopGroup())

  def start(host: String, port: Int) = {
    try {
      val b = server.group(group, childGroup).channel(classOf[NioServerSocketChannel])
        .option(ChannelOption.SO_BACKLOG, Int.box(1024))
        .childHandler(new ChannelInitializer[SocketChannel] {
          override def initChannel(ch: SocketChannel) = {
            //数据包长度声明
            ch.pipeline().addLast("lengthField", new LengthFieldBasedFrameDecoder(1024, 0, 2, 0, 2))
            //换行分割
            ch.pipeline().addLast(new LineBasedFrameDecoder(1024))
            ch.pipeline().addLast(new StringEncoder())

            //自定义分隔符
            ch.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, Unpooled.copiedBuffer("\t".getBytes())))
            ch.pipeline().addLast(new StringEncoder())

            ch.pipeline().addLast(new ReadTimeoutHandler(300))
            ch.pipeline().addLast(new ChannelInboundHandlerAdapter {
              override def channelRead(ctx: ChannelHandlerContext, msg: scala.Any): Unit = msg match {
                case message: Int if message == 1 => println("alive"); ctx.writeAndFlush(1)
                case _ => println("dead"); ctx.fireChannelRead(msg)
              }

              override def channelReadComplete(ctx: ChannelHandlerContext): Unit = {
                ctx.flush()
              }

              override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable): Unit = {
                cause.printStackTrace()
                ctx.close()
              }
            })
          }
        })

      val future = b.bind(host, port).sync()
      println(s"NettyServer: $port")
      future.channel().closeFuture().sync()
    } finally {
      group.shutdownGracefully()
      childGroup.shutdownGracefully()
    }
  }
}
