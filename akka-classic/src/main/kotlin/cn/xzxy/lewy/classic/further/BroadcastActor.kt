package cn.xzxy.lewy.classic.further

import akka.actor.AbstractLoggingActor
import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Props
import akka.japi.pf.ReceiveBuilder
import akka.routing.BroadcastGroup

/**
 * 广播分发
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

    val actorSystem = ActorSystem.create("broadcastActor")
    val loggerRef1 = actorSystem.actorOf(Props.create(LoggingActor::class.java), "Logger1")
    val loggerRef2 = actorSystem.actorOf(Props.create(LoggingActor::class.java), "Logger2")
    val loggerRef3 = actorSystem.actorOf(Props.create(LoggingActor::class.java), "Logger3")

    val routers = listOf(loggerRef1, loggerRef2, loggerRef3).map { it.path().toString() }
    // println(routers)
    val broadcastRef = actorSystem.actorOf(BroadcastGroup(routers).props(), "broadcaster")

    broadcastRef.tell(Event1(), ActorRef.noSender())
    broadcastRef.tell(Event2(), ActorRef.noSender())

    actorSystem.terminate()

    // FQA:
    // 1. 广播分组的依据是什么
}