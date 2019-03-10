import groovy.util.logging.Slf4j
import io.vertx.core.AbstractVerticle
import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx

@Slf4j
class SomeVerticle extends AbstractVerticle {
  def i = 0

  @Override
  void start() throws Exception {

    vertx.setPeriodic(1000, {
      i++
      log.info(i + "")
    })

    new Thread({
      i++
    }).start()

    vertx.executeBlocking({

    },false,{

    })

  }
}

def vertx = Vertx.vertx()
vertx.deployVerticle("SomeVerticle",new DeploymentOptions().setWorker(true).setMultiThreaded(true))



