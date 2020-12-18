package cn.xzxy.lewy.typed.caseGreet.practise

import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.javadsl.AbstractBehavior
import akka.actor.typed.javadsl.ActorContext
import akka.actor.typed.javadsl.Behaviors
import akka.actor.typed.javadsl.Receive

class DispatcherTest private constructor(
    context: ActorContext<Greet>
) : AbstractBehavior<DispatcherTest.Greet>(context) {

  data class Greet(val whom: String, val replyTo: ActorRef<Greeted>)
  data class Greeted(val whom: String, val replyTo: ActorRef<Greet>)

  override fun createReceive(): Receive<Greet> {
    return newReceiveBuilder().onMessage(Greet::class.java) { command: Greet ->
      this.onGreet(command)
    }.build()
  }

  private fun onGreet(command: Greet): Behavior<Greet>? {
    context.log.info("hello {}!", command.whom)
    command.replyTo.tell(Greeted(command.whom, context.self))
    return this
  }

  companion object {
    fun create(): Behavior<Greet> {
      return Behaviors.setup { context: ActorContext<Greet> -> DispatcherTest(context) }
    }
  }
}