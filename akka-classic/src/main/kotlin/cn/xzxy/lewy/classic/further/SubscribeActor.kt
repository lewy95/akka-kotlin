package cn.xzxy.lewy.classic.further

import akka.actor.AbstractLoggingActor
import akka.actor.ActorSystem
import akka.actor.Props
import akka.japi.pf.ReceiveBuilder

/**
 * 发布/订阅
 */
fun main(args: Array<String>) {

    open class Event
    class Event1 : Event()
    class Event2 : Event()

    class LoggingActor : AbstractLoggingActor() {

        override fun createReceive() =
            ReceiveBuilder()
                .match(Event1::class.java) { log().info("Received Event 1") }
                .match(Event2::class.java) { log().info("Received Event 2") }
                .build()
    }

    val actorSystem = ActorSystem.create("subscribeActor")
    // 创建三个actor
    val loggerRef1 = actorSystem.actorOf(Props.create(LoggingActor::class.java), "Logger1")
    val loggerRef2 = actorSystem.actorOf(Props.create(LoggingActor::class.java), "Logger2")
    val loggerRef3 = actorSystem.actorOf(Props.create(LoggingActor::class.java), "Logger3")
    // 定义一个事件流
    val eventStream = actorSystem.eventStream()

    // 绑定事件和Actor
    eventStream.subscribe(loggerRef1, Event::class.java)
    eventStream.subscribe(loggerRef2, Event1::class.java)
    eventStream.subscribe(loggerRef3, Event2::class.java)

    // Event1，Event2 继承 Event
    // 绑定在父类事件上的actor都可以被子类事件获取到
    eventStream.publish(Event1()) // Logger2 / Logger1
    eventStream.publish(Event2()) // Logger3 / Logger1

    actorSystem.terminate()
}