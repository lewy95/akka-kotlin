package cn.xzxy.lewy.classic.further

import akka.actor.*
import akka.japi.pf.ReceiveBuilder

fun main(args: Array<String>) {

    class HelloKotlinActor : AbstractLoggingActor() {
        override fun createReceive() = ReceiveBuilder()
            .match(String::class.java) {
                log().info(it)
            }.build()
    }

    val actorSystem = ActorSystem.create("dispatcherActor")
    val actorRef1 = actorSystem.actorOf(Props.create(HelloKotlinActor::class.java), "actor1")
    val actorRef2 = actorSystem.actorOf(Props.create(HelloKotlinActor::class.java)
        .withDispatcher("actor2-dispatcher"), "actor2")
    actorSystem.log().info("### Sending Hello Kotlin ...")
    // actor之间没有影响，并发执行
    actorRef1.tell("Hello Actor 1", ActorRef.noSender())
    actorRef2.tell("Hello Actor 2", ActorRef.noSender())

    actorSystem.terminate()

    // FQA ?
    // 1. 为什么是 application.conf, 其他名称不可以？
    // 2. dispatcher 有什么作用？
    // 3. Dispatcher 和 PinnedDispatcher 区别？
    //
}