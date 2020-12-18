package cn.xzxy.lewy.classic.caseIlot

import akka.actor.AbstractLoggingActor
import akka.japi.pf.ReceiveBuilder

class IotSupervisor : AbstractLoggingActor() {

  override fun preStart() {
    super.preStart()
    log().info("IoT Application started");
  }

  override fun createReceive(): Receive =
    ReceiveBuilder().build()

  private fun onPostStop(message: String) {
    log().info("IoT Application stopped");
  }
}