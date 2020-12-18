package cn.xzxy.lewy.typed.caseGreet


import akka.actor.typed.Behavior
import akka.actor.typed.javadsl.AbstractBehavior
import akka.actor.typed.javadsl.ActorContext
import akka.actor.typed.javadsl.Behaviors
import akka.actor.typed.javadsl.Receive

/**
 * 迎宾机器人
 *
 * @property max Int 最大迎宾次数
 * @property greetingCounter Int 默认为0，不传即可
 * @constructor
 */
class GreeterBot private constructor(
    context: ActorContext<Greeter.Greeted>,
    private val max: Int,
    private var greetingCounter: Int = 0
) :
  AbstractBehavior<Greeter.Greeted>(context) {

  companion object {
    /**
     * 用静态方法去创建一个GreeterBot对象，然后经过setup()方法返回一个Behavior
     *
     * @param max Int 最大打招呼次数
     * @return Behavior<Greeter.Greeted>
     */
    fun create(max: Int): Behavior<Greeter.Greeted> {
      return Behaviors.setup { context: ActorContext<Greeter.Greeted> ->
        GreeterBot(context, max)
      }
    }
  }

  /**
   * 实现createReceive()接口
   * 当消息为Greeted时，执行onGreeted()方法
   *
   * @return Receive<Greeter.Greeted>
   */
  override fun createReceive(): Receive<Greeter.Greeted> {
    return newReceiveBuilder().onMessage(Greeter.Greeted::class.java) { message: Greeter.Greeted ->
      onGreeted(message)
    }.build()
  }

  /**
   * 收到消息后的处理逻辑
   *
   * @param message Greeted 已打过招呼
   * @return Behavior<Greeter.Greeted>
   */
  private fun onGreeted(message: Greeter.Greeted): Behavior<Greeter.Greeted> {
    // 计数器加1
    greetingCounter++
    context.log.info("Greeting $greetingCounter for ${message.whom}")
    // 如迎宾数已达到最大值，则停止
    if (greetingCounter == max) {
      return Behaviors.stopped()
    } else {
      // 否则，让greeter继续打招呼
      message.from.tell(Greeter.Greet(message.whom, context.self))
      return this
    }
  }
}