package cn.xzxy.lewy.typed.caseGreet

import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.javadsl.AbstractBehavior
import akka.actor.typed.javadsl.ActorContext
import akka.actor.typed.javadsl.Behaviors
import akka.actor.typed.javadsl.Receive
import java.util.*

/**
 * Greeter 迎宾员actor接收向某人Greet的命令
 */
class Greeter private constructor(context: ActorContext<Greet>) : AbstractBehavior<Greeter.Greet>(context) {
  // 在java版本中，他使用了嵌套类，在java需要在类定义前加上static，但在kotlin不用加static，因为默认在类中定义类就是嵌套类
  // 在kotlin中要定义内部类，需要在前面加上inner
  /**
   * 迎宾消息
   *
   * @property whom String 迎宾的目标的名字
   * @property replyTo ActorRef<Greeted> 将消息返回
   * @constructor
   */
  data class Greet(val whom: String, val replyTo: ActorRef<Greeted>)

  /**
   * 迎宾已打招呼消息
   *
   * @property whom String 迎宾的目标的名字
   * @property from ActorRef<Greet> 将消息返回
   * @constructor
   */
  data class Greeted(val whom: String, val from: ActorRef<Greet>) {
    // 重写Greeted的equals方法
    override fun equals(o: Any?): Boolean {
      if (this === o) {
        return true
      }
      if (o === null || o !is Greeted) {
        return false
      }
      return whom == o.whom &&
          from == o.from
    }

    // 重写了equals也要重写hashCode
    override fun hashCode(): Int {
      return Objects.hash(whom, from)
    }

    override fun toString(): String {
      return "Greeted{whom='$whom', from='$from'}"
    }
  }

  // 伴生对象，用于装java里的static属性和方法
  companion object {
    /**
     * 用静态方法去创建一个Greeter对象，然后经过setup()方法返回一个Behavior
     *
     * @return Behavior<Greet>
     */
    fun create(): Behavior<Greet> {
      return Behaviors.setup { context: ActorContext<Greet> -> Greeter(context) }
    }
  }

  /**
   * 实现createReceive()接口
   * 当消息为Greet时，执行onGreet()方法
   *
   * @return Receive<Greet>
   */
  override fun createReceive(): Receive<Greet> {
    return newReceiveBuilder()
      .onMessage(Greet::class.java) { command: Greet -> this.onGreet(command) }
      .build()
  }

  /**
   * 收到消息后的处理逻辑
   *
   * @param command Greet
   * @return Behavior<Greet>
   */
  private fun onGreet(command: Greet): Behavior<Greet> {
    context.log.info("hello ${command.whom}!")
    command.replyTo.tell(Greeted(command.whom, context.self))
    // 返回this表示不更新状态state
    return this
  }
}