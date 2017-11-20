package netty.server

import io.netty.bootstrap.ServerBootstrap
import io.netty.buffer.{ByteBuf, Unpooled}
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.channel._

class SimpleServer {

  val server = new ServerBootstrap()
  val (group, childGroup) = (new NioEventLoopGroup(), new NioEventLoopGroup())

  def start(host: String, port: Int) = {
    try {
      val b = server.group(group, childGroup).channel(classOf[NioServerSocketChannel])
        .option(ChannelOption.SO_BACKLOG, Int.box(1024))
        .childOption(ChannelOption.SO_KEEPALIVE, Boolean.box(true))
        .childHandler(new ChannelInitializer[SocketChannel] {
          override def initChannel(ch: SocketChannel) = {
            //数据包长度声明
            //            ch.pipeline().addLast("lengthField", new LengthFieldBasedFrameDecoder(1024, 0, 2, 0, 2))
            //            换行分割
            //            ch.pipeline().addLast(new LineBasedFrameDecoder(1024))
            //            ch.pipeline().addLast(new StringEncoder())

            //自定义分隔符
            //            ch.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, Unpooled.copiedBuffer("\t".getBytes())))
            //            ch.pipeline().addLast(new StringEncoder())
            //
            //            ch.pipeline().addLast(new ReadTimeoutHandler(300))
            ch.pipeline().addLast(new ChannelInboundHandlerAdapter {
              override def channelRead(ctx: ChannelHandlerContext, msg: scala.Any): Unit = {
                println(msg)
                msg match {
                  case _:ByteBuf => println("alive"); ctx.writeAndFlush(Unpooled.copyInt(1))
                  case _ => println("dead"); ctx.fireChannelRead(msg)
                }
              }

              override def channelActive(ctx: ChannelHandlerContext): Unit = {
                val time = ctx.alloc().buffer(4)
                time.writeInt((System.currentTimeMillis()/1000 + 220898800L).toInt)
                val f = ctx.writeAndFlush(time)
                f.addListener(new ChannelFutureListener {
                  override def operationComplete(future: ChannelFuture): Unit = {
                    println("complete")
                    assert(f==future)
                    ctx.close()
                  }
                })
              }

              override def channelReadComplete(ctx: ChannelHandlerContext): Unit = {
                println("read channel complete")
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
