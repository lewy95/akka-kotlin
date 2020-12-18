package cn.xzxy.lewy.typed.caseGreet

import akka.actor.typed.ActorSystem

fun main() {
  // 初始化守护actor
  val greeterMain: ActorSystem<GreeterMain.SayHello> = ActorSystem.create(GreeterMain.create(), "helloakka")
  // 启动打招呼
  greeterMain.tell(GreeterMain.SayHello("Charles"))

  // 停止actorSystem
  greeterMain.terminate()
}