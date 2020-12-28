package com.aruistar.vertxstarter;


import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

public class MainVerticle extends AbstractVerticle {

  static {
    SLF4JBridgeHandler.removeHandlersForRootLogger();
    SLF4JBridgeHandler.install();
  }

  private static final Logger logger = LoggerFactory.getLogger(MainVerticle.class);


  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    logger.info("verticle start");

    PgConnectOptions connectOptions = new PgConnectOptions()
      .setPort(5432)
      .setHost("127.0.0.1")
      .setDatabase("studypg")
      .setUser("postgres")
      .setPassword("muyuntage");

    PoolOptions poolOptions = new PoolOptions()
      .setMaxSize(5);

    PgPool client = PgPool.pool(vertx, connectOptions, poolOptions);

    Router router = Router.router(vertx);

    router.get("/")
      .handler(routingContext -> {
        routingContext.response().putHeader("Context-Type", "text/plain")
          .end("Hello from Vert.x!");
      });

    router.post("/score")
      .handler(BodyHandler.create())
      .handler(routingContext -> {
        HttpServerResponse response = routingContext.response();
        JsonObject json = routingContext.getBodyAsJson();

        client.preparedQuery("insert into edu_score (v_lesson, n_score, v_name) values ($1,$2,$3) returning id;")
          .execute(Tuple.of(json.getString("v_lesson"), json.getFloat("n_score"), json.getString("v_name")),
            ar -> {
              if (ar.succeeded()) {
                String id = ar.result().iterator().next().getString("id");
                response.end(id);
              } else {
                routingContext.fail(500, ar.cause());
              }
            });
      });

    router.errorHandler(500, routingContext -> {
      logger.error("😭", routingContext.failure());
    });

    vertx.createHttpServer().requestHandler(router)
      .listen(8080, http -> {
        if (http.succeeded()) {
          startPromise.complete();
          System.out.println("HTTP server started on http://localhost:8080");
        } else {
          startPromise.fail(http.cause());
        }
      });
  }

}
