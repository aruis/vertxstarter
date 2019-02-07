import groovy.util.logging.Slf4j
import io.vertx.core.AbstractVerticle
import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx


@Slf4j
class SomeWorker extends AbstractVerticle {
  def i = 0

  @Override
  void start() throws Exception {
    vertx.setPeriodic(1000, {
      i++
      log.info(i + "")
    })
  }
}

def vertx = Vertx.vertx()
vertx.deployVerticle("SomeWorker", new DeploymentOptions().setWorker(true).setWorkerPoolSize(10))


