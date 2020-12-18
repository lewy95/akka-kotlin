package cn.xzxy.lewy.typed.caseGreet

import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.javadsl.AbstractBehavior
import akka.actor.typed.javadsl.ActorContext
import akka.actor.typed.javadsl.Behaviors
import akka.actor.typed.javadsl.Receive

/**
 * 作为引导的守护者actor
 * 其构造函数为私有
 */
class GreeterMain private constructor(
    context: ActorContext<SayHello>,
    private val greeter: ActorRef<Greeter.Greet> = context.spawn(Greeter.create(), "greeter")
) : AbstractBehavior<GreeterMain.SayHello>(context) {
  // 对应java下的static方法
  companion object {
    /**
     * 用静态方法创建一个GreeterMain对象，并setup()
     * 使用Behaviors.setup()方法实现
     *
     * @return Behavior<SayHello>
     */
    fun create(): Behavior<SayHello> {
      return Behaviors.setup { context: ActorContext<SayHello> ->
        GreeterMain(context)
      }
    }
  }

  /**
   * 构造打招呼的消息结构体
   * 嵌套类，在java中用static定义，在kotlin中不需要定义
   *
   * @property name String 打招呼的目标的名字
   * @constructor
   */
  data class SayHello(val name: String)

  /**
   * 实现createReceive()接口
   * 当消息为SayHello类型时，将执行onSayHello()方法
   *
   * @return Receive<SayHello>
   */
  override fun createReceive(): Receive<SayHello> {
    return newReceiveBuilder().onMessage(
        SayHello::class.java
    ) { command: SayHello -> onSayHello(command) }.build()
  }

  /**
   * 收到消息后的处理逻辑
   *
   * @param command SayHello 打招呼消息
   * @return Behavior<SayHello>
   */
  private fun onSayHello(command: SayHello): Behavior<SayHello> {
    val replyTo: ActorRef<Greeter.Greeted> = context.spawn(GreeterBot.create(3), command.name)
    println("starting test")
    println(context.children) // 获取所有子Actor的ActorRef
    println(context.self)     // 获取自身的ActorRef，最开始为 akka://xxx/user
    greeter.tell(Greeter.Greet(command.name, replyTo))
    return this
  }
}