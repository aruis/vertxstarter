package com.aruistar.vertxstarter;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.ext.web.Router;

public class MainVerticle extends AbstractVerticle {

  int i = 0;

  @Override
  public void start(Future<Void> startFuture) throws Exception {

    Router router = Router.router(vertx);

    router.get("/test")
      .handler(routingContext -> {
        i++;
        routingContext.response().end(i + "");
      });

    router.get("/show")
      .handler(routingContext -> {
        routingContext.response().end(i + "");
      });

    router.get("/clean")
      .handler(routingContext -> {
        i = 0;
        routingContext.response().end(i + "");
      });

    vertx.createHttpServer().requestHandler(router)
      .listen(7070, ar -> {
        if (ar.succeeded()) {
          System.out.println("listen on 7070");
        }
      });
  }

}
