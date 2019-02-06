package com.aruistar.vertxstarter;

import io.reactiverse.pgclient.PgClient;
import io.reactiverse.pgclient.PgPool;
import io.reactiverse.pgclient.PgPoolOptions;
import io.reactiverse.pgclient.Tuple;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
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

    JsonObject config = config();
    String host = config.getString("host", "127.0.0.1");
    int port = config.getInteger("port", 5432);
    String database = config.getString("database");
    String user = config.getString("user");
    String password = config.getString("password");

    PgPoolOptions options = new PgPoolOptions()
      .setPort(port)
      .setHost(host)
      .setDatabase(database)
      .setUser(user)
      .setPassword(password)
      .setMaxSize(5);

// Create the client pool
    PgPool client = PgClient.pool(vertx, options);

    Router router = Router.router(vertx);

    router.post("/score")
      .handler(BodyHandler.create())
      .handler(routingContext -> {
        HttpServerResponse response = routingContext.response();
        JsonObject json = routingContext.getBodyAsJson();

        client.preparedQuery("insert into edu_score (v_lesson, n_score, v_name) values ($1,$2,$3) returning id;",
          Tuple.of(json.getString("v_lesson"), json.getFloat("n_score"), json.getString("v_name")),
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
          startFuture.complete();
          System.out.println("HTTP server started on http://localhost:8080");
        } else {
          startFuture.fail(http.cause());
        }
      });
  }

}
