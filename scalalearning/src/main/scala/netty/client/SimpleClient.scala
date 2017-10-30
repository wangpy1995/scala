package netty.client

import java.util.Date

import io.netty.bootstrap.Bootstrap
import io.netty.buffer.ByteBuf
import io.netty.channel._
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel

class SimpleClient {
  def connect(host: String, port: Int) = {
    assert(host != null && host.nonEmpty)
    assert(port > 0)
    val group = new NioEventLoopGroup()
    try {
      val bootStrap = new Bootstrap()

      bootStrap.group(group).channel(classOf[NioSocketChannel])
        .option(ChannelOption.SO_KEEPALIVE, Boolean.box(true))
        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, Int.box(6000))
        .handler(new ChannelInitializer[SocketChannel] {
          override def initChannel(ch: SocketChannel) = {
            //数据包长度声明
            //            ch.pipeline().addLast("lengthField", new LengthFieldBasedFrameDecoder(1024, 0, 2, 0, 2))
            //换行分割
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
                  case message: ByteBuf =>
                    val current = (message.readInt() - 220898800) * 1000L
                    println(s"alive at time:[${new Date(current)}]")
                    ctx.close()
                  case _ => println("dead"); ctx.fireChannelRead(msg)
                }
              }

              override def channelActive(ctx: ChannelHandlerContext): Unit = {
                println("channel active")
                //                ctx.writeAndFlush(Unpooled.copyInt(1))
                super.channelActive(ctx)
                /*ctx.executor().scheduleAtFixedRate(new Runnable {
                  override def run(): Unit = ctx.writeAndFlush(1)
                }, 0, 9000, TimeUnit.MILLISECONDS)*/
              }
            })
          }
        })

      val future = bootStrap.connect(host, port).sync()
      println(s"连接: $host:$port")
      future.channel().closeFuture().sync()
      println(s"断开连接:[$host:$port]")
    }
    finally
      group.shutdownGracefully()
  }
}