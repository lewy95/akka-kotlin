package cn.xzxy.lewy.classic.basic

import akka.actor.AbstractLoggingActor
import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Props
import akka.japi.pf.ReceiveBuilder

/**
 * 单 actor 案例
 */
fun main(args: Array<String>) {

  // 消息处理类
  // 类似 java akka 中的 AbstractBehavior.createReceive
  class HelloKotlinActor : AbstractLoggingActor() {
    // 只匹配 string 类型
    override fun createReceive() = ReceiveBuilder().match(String::class.java) { log().info(it) }.build()
  }

  // actorSystem，akka的入口，通常一个jvm中有且只有一个
  val actorSystem = ActorSystem.create("SingleActor")
  // ActorRef 为 Actor 实例引用
  // 由于actor是通过消息驱动的，无法直接获取actor的实例，需要通过ActorRef来向actor投递消息
  val actorRef = actorSystem.actorOf(Props.create(HelloKotlinActor::class.java))
  // 打印日志信息
  actorSystem.log().info("Sending Hello Kotlin")
  // actorRef将消息扔到对应actor中，ActorRef.noSender()表示该消息没有下一个接收者
  actorRef.tell("Hello Kotlin", ActorRef.noSender())
  // actorRef.tell(915, ActorRef.noSender()) // 消息内容为int类型，不匹配String，actor收不到

  actorSystem.terminate()

  // FQA：
  // 1. 不匹配的消息去哪里？
}