package cn.xzxy.lewy.typed.caseIot

import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.PostStop
import akka.actor.typed.Signal
import akka.actor.typed.javadsl.AbstractBehavior
import akka.actor.typed.javadsl.ActorContext
import akka.actor.typed.javadsl.Receive
import java.util.*

/**
 * 设备参与者的任务：
 * 1. 收集温度测量值；
 * 2. 询问时，报告上一次测量的温度；
 */
class Device2 private constructor(
    context: ActorContext<Device.Command>,
    private val groupId: String,
    private val deviceId: String
) : AbstractBehavior<Device.Command>(context) {

  /**
   * 1. 最多一次，即没有保证的交付；
   * 2. 发送者/接收者都需要对消息顺序进行维护；
   */

  /**
   * 消息的优化：
   * 1. 消息中添加一个requestId字段，
   */
  // 密封类，类似一个接口，下面的方法可以认为是接口方法
  sealed class Command {
    // 读取当前温度
    class ReadTemperature(val requestId: Long, val replyTo: ActorRef<RespondTemperature>) : Command()

    // 回复当前温度
    class RespondTemperature(val requestId: Long, val temperatureValue: Optional<Double>)
  }

  override fun createReceive(): Receive<Device.Command> {
    return newReceiveBuilder()
      .onMessage(Device.Command.ReadTemperature::class.java, this::readTemperature)
      .onMessage(Device.Command.RecordTemperature::class.java, this::recordTemperature)
      .onSignal(PostStop::class.java, this::onStop)
      .build()
  }

  private fun readTemperature(command: Device.Command.ReadTemperature): Behavior<Device.Command> {
    return this
  }

  private fun recordTemperature(command: Device.Command.RecordTemperature): Behavior<Device.Command> {
    return this
  }

  private fun onStop(signal: Signal): Behavior<Device.Command> {
    context.log.info("Device[ $deviceId, $groupId ] stopped!")
    return this
  }

}