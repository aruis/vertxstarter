import groovy.util.logging.Slf4j
import io.vertx.core.AbstractVerticle
import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx


@Slf4j
class SomeWorker extends AbstractVerticle {
  def i = 0

  @Override
  void start() throws Exception {

    println Vertx.currentContext().hashCode()

    vertx.runOnContext({
      log.info("runOnContext")
    })

    vertx.eventBus().consumer("test", {
      i++
      log.info(i + "")
      println Vertx.currentContext().hashCode()
    })


  }
}

def vertx = Vertx.vertx()
vertx.deployVerticle("SomeWorker", new DeploymentOptions().setWorker(true)
  .setInstances(1)
  .setWorkerPoolSize(5))


vertx.setPeriodic(5000, {
//  println Vertx.currentContext().hashCode()
  vertx.eventBus().publish("test", "test")
})


