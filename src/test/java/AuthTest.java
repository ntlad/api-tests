import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

class AuthTest extends BaseApiTest {

    @Test
    @DisplayName("Полученный при входе токен открывает профиль пользователя")
    void shouldAccessProfileWithReceivedToken() {
        String password = System.getProperty("api.password", "Password@123");

        String token = given()
                .spec(rest)
                .body(Map.of("username", "Bret", "password", password))
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .contentType("application/json")
                .body("access_token", instanceOf(String.class))
                .body("access_token", not(emptyOrNullString()))
                .extract().path("access_token");

        given()
                .spec(rest)
                .auth().oauth2(token)
                .when()
                .get("/auth/me")
                .then()
                .statusCode(200)
                .contentType("application/json")
                .body("username", equalToIgnoringCase("Bret"))
                .body("id", notNullValue())
                .body("name", not(emptyOrNullString()))
                .body("email", not(emptyOrNullString()));
    }

    @Test
    @DisplayName("Без токена доступ к профилю запрещён")
    void shouldRejectRequestWithoutToken() {
        given()
                .spec(rest)
                .when()
                .get("/auth/me")
                .then()
                .statusCode(401)
                .contentType("application/json")
                .body("$", not(hasKey("username")));
    }

    @Test
    @DisplayName("Заведомо неверный токен не открывает профиль")
    void shouldRejectInvalidToken() {
        given()
                .spec(rest)
                .auth().oauth2("definitely-invalid-token")
                .when()
                .get("/auth/me")
                .then()
                .statusCode(401)
                .contentType("application/json")
                .body("$", not(hasKey("username")));
    }

    @Test
    @DisplayName("Дополнительный сценарий: неверный пароль не позволяет получить токен")
    void shouldRejectWrongPassword() {
        given()
                .spec(rest)
                .body(Map.of("username", "Bret", "password", "definitely-wrong-password"))
                .when()
                .post("/auth/login")
                .then()
                .statusCode(401)
                .contentType("application/json")
                .body("error", equalTo("Invalid username or password."))
                .body("status", equalTo(401))
                .body("$", not(hasKey("access_token")));
    }
}
