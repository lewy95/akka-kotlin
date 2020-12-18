package cn.xzxy.lewy.classic.caseIlot
import akka.actor.ActorSystem
import akka.actor.Props

fun main(args: Array<String>) {
    val actorSystem = ActorSystem.create("IoT-System")

    val iotSupervisorActor = actorSystem.actorOf(Props.create(IotSupervisor::class.java), "iotSupervisor")
}