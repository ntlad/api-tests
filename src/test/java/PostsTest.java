import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.not;

class PostsTest extends BaseApiTest {

    @Test
    @DisplayName("Создание поста, изменение заголовка и чтение сохранённого результата")
    void shouldCreateUpdateAndReadPost() {
        Number userId = given()
                .spec(rest)
                .queryParam("limit", 1)
                .when()
                .get("/users")
                .then()
                .statusCode(200)
                .contentType("application/json")
                .body("data.size()", greaterThan(0))
                .body("data[0].id", instanceOf(Number.class))
                .extract().path("data[0].id");

        String title = "Homework post " + UUID.randomUUID();
        String body = "This text must remain unchanged after PATCH";

        String postId = given()
                .spec(rest)
                .body(Map.of("title", title, "body", body, "user_id", userId))
                .when()
                .post("/posts")
                .then()
                .statusCode(201)
                .contentType("application/json")
                .body("id", not(emptyOrNullString()))
                .body("title", equalTo(title))
                .body("body", equalTo(body))
                .body("user_id", equalTo(userId))
                .extract().path("id");

        String updatedTitle = title + " (updated)";

        given()
                .spec(rest)
                .pathParam("id", postId)
                .body(Map.of("title", updatedTitle))
                .when()
                .patch("/posts/{id}")
                .then()
                .statusCode(200)
                .contentType("application/json")
                .body("id", equalTo(postId))
                .body("title", equalTo(updatedTitle))
                .body("body", equalTo(body))
                .body("user_id", equalTo(userId));

        given()
                .spec(rest)
                .pathParam("id", postId)
                .when()
                .get("/posts/{id}")
                .then()
                .statusCode(200)
                .contentType("application/json")
                .body("id", equalTo(postId))
                .body("title", equalTo(updatedTitle))
                .body("body", equalTo(body))
                .body("user_id", equalTo(userId));
    }
}
