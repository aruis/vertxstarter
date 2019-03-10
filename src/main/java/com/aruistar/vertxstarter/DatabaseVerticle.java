package com.aruistar.vertxstarter;

import io.reactiverse.pgclient.PgClient;
import io.reactiverse.pgclient.PgPool;
import io.reactiverse.pgclient.PgPoolOptions;
import io.reactiverse.pgclient.Tuple;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseVerticle extends AbstractVerticle {

  private static Logger logger = LoggerFactory.getLogger(DatabaseVerticle.class);

  @Override
  public void start() throws Exception {
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

    EventBus eventBus = vertx.eventBus();

    eventBus.<JsonObject>consumer("add_score", msg -> {
      logger.info("i am here");
      JsonObject json = msg.body();
      client.preparedQuery("insert into edu_score (v_lesson, n_score, v_name) values ($1,$2,$3) returning id;",
        Tuple.of(json.getString("v_lesson"), json.getFloat("n_score"), json.getString("v_name")),
        ar -> {
          if (ar.succeeded()) {
            String id = ar.result().iterator().next().getString("id");
            msg.reply(id);
          } else {
            msg.fail(500, ar.cause().getMessage());
          }
        });
    });


  }
}
