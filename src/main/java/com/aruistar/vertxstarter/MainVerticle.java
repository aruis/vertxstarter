package com.aruistar.vertxstarter;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainVerticle extends AbstractVerticle {


  private static Logger logger = LoggerFactory.getLogger(MainVerticle.class);

  @Override
  public void start(Future<Void> startFuture) throws Exception {
    logger.info("verticle start");

    vertx.deployVerticle("com.aruistar.vertxstarter.DatabaseVerticle",
      new DeploymentOptions().setConfig(config()).setInstances(4));

    Router router = Router.router(vertx);

    EventBus eventBus = vertx.eventBus();
    router.post("/score")
      .handler(BodyHandler.create())
      .handler(routingContext -> {
        HttpServerResponse response = routingContext.response();
        JsonObject json = routingContext.getBodyAsJson();

        eventBus.<String>send("add_score", json, msg -> {
          if (msg.succeeded()) {
            response.end(msg.result().body());
          } else {
            routingContext.fail(500, msg.cause());
          }
        });

      });

    router.errorHandler(500, routingContext -> {
      logger.error("😭", routingContext.failure());
    });

    vertx.createHttpServer().requestHandler(router)
      .listen(8080, http -> {
        if (http.succeeded()) {
          startFuture.complete();
          System.out.println("HTTP server started on http://localhost:8080");
        } else {
          startFuture.fail(http.cause());
        }
      });
  }

}
