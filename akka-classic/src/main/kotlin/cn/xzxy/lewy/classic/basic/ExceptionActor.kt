package cn.xzxy.lewy.classic.basic


import akka.actor.*
import akka.actor.SupervisorStrategy.*
import akka.japi.pf.ReceiveBuilder
import scala.concurrent.duration.Duration
import java.util.*

/**
 * 多 actor 案例 （异常处理策略）
 */
fun main(args: Array<String>) {

    class ChildActor : AbstractLoggingActor() {

        // 生命周期：
        // actorOf -> preStart -> start -> receive -> stop -> postStop

        // preStart() 在 actor 启动后，处理第一条消息之前被调用
        override fun preStart() {
            super.preStart()
            log().info("Starting Child Actor")
        }

        override fun createReceive() =
            ReceiveBuilder().match(String::class.java, this::onMessage).build()

        private fun onMessage(message: String) {
            when (message) {
                "DIE" -> throw Exception("DEAD")
                else -> log().info(message)
            }
        }

        override fun preRestart(reason: Throwable?, message: Optional<Any>?) {
            super.preRestart(reason, message)
            log().info("child actor prepare to restart ...")
        }
    }

    class ParentActor : AbstractLoggingActor() {

        // 异常处理策略
        override fun supervisorStrategy() = AllForOneStrategy(-1, Duration.Inf()) {
            when (it) {
                is ActorInitializationException -> stop() as Directive?
                is ActorKilledException -> stop() as Directive?
                is DeathPactException -> stop() as Directive?
                else -> restart() as Directive?
            }
        }

        override fun preStart() {
            super.preStart()
            context.actorOf(Props.create(ChildActor::class.java), "child1")
            context.actorOf(Props.create(ChildActor::class.java), "child2")
            context.actorOf(Props.create(ChildActor::class.java), "child3")
        }

        override fun createReceive() =
            ReceiveBuilder()
                .match(String::class.java) {
                    context.children.forEach { child -> child.tell(it, self()) }
                }
                .build()
    }

    val actorSystem = ActorSystem.create("exceptionActor")
    val actorRef = actorSystem.actorOf(Props.create(ParentActor::class.java), "parent")
    actorSystem.log().info("### Sending Hello Kotlin...")
    // 父 actorRef tell 后，createReceive 触发，遍历调用 childActor，每个子 Actor又会触发 createReceive >> onMessage 方法
    actorRef.tell("Hello Kotlin", ActorRef.noSender())
    actorSystem.log().info("### Sending DIE message to child1. We expect all child actors to restart:")
    Thread.sleep(1000)
    // 只给 child1 Actor 发送异常
    actorSystem.actorSelection("akka://exceptionActor/user/parent/child1").tell("DIE", ActorRef.noSender())

    actorSystem.terminate()
}