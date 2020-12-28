package com.aruistar.vertxstarter;

import groovy.sql.Sql;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.Timeout;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(VertxExtension.class)
public class TestMainVerticle {

  @BeforeEach
  void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
    vertx.deployVerticle(new MainVerticle(), testContext.succeeding(id -> testContext.completeNow()));
  }

  @Test
  @DisplayName("Should start a Web Server on port 8080")
  @Timeout(value = 10, timeUnit = TimeUnit.SECONDS)
  void start_http_server(Vertx vertx, VertxTestContext testContext) throws Throwable {

    vertx.createHttpClient().request(HttpMethod.GET, 8080, "localhost", "/")
      .compose(req -> req.send().compose(HttpClientResponse::body))
      .onComplete(testContext.succeeding(buffer -> testContext.verify(() -> {
        assertTrue(buffer.toString().contains("Hello from Vert.x!"));
        testContext.completeNow();
      })));

  }

  @Test
  @DisplayName("post /score")
  @Timeout(value = 10, timeUnit = TimeUnit.SECONDS)
  void postScore(Vertx vertx, VertxTestContext testContext) throws Throwable {
    int score = new Random().nextInt(100);
    JsonObject body = new JsonObject()
      .put("v_name", "lilei")
      .put("n_score", score)
      .put("v_lesson", "语文");

    Sql db = Sql.newInstance("jdbc:postgresql://localhost:5432/studypg", "postgres", "muyuntage", "org.postgresql.Driver");

    WebClient client = WebClient.create(vertx);
    client.post(8080, "127.0.0.1", "/score")
      .sendJsonObject(body, ar -> {
        assertTrue(ar.succeeded());
        HttpResponse response = ar.result();
        assertEquals(response.statusCode(), 200);
        String id = response.bodyAsString();
        try {
          BigDecimal scoreInRow = (BigDecimal) db.firstRow("select n_score from edu_score where id = ?;", List.of(id)).get("n_score");
          assertEquals(scoreInRow.floatValue(), score);
        } catch (SQLException e) {
          e.printStackTrace();
        }
        testContext.completeNow();
      });
  }

}
