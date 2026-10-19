import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

class GraphQlTest extends BaseApiTest {

    @Test
    @DisplayName("GraphQL query возвращает приветствие для указанного человека без errors")
    void shouldReturnGreetingFromQuery() {
        String query = """
                query Hello {
                  hello(person: { name: "Ivan", age: 35 })
                }
                """;

        given()
                .spec(graphql)
                .body(Map.of("query", query))
                .when()
                .post("")
                .then()
                .statusCode(200)
                .contentType("application/json")
                .body("$", not(hasKey("errors")))
                .body("data", notNullValue())
                .body("data.hello", equalTo("Hello Ivan"));
    }

    @Test
    @DisplayName("GraphQL mutation возвращает ID, имя и возраст созданного человека без errors")
    void shouldCreatePersonWithMutation() {
        String name = "Student " + UUID.randomUUID();
        int age = 20;
        String mutation = """
                mutation CreatePerson {
                  createPerson(person: { name: "%s", age: %d }) {
                    id
                    name
                    age
                  }
                }
                """.formatted(name, age);

        given()
                .spec(graphql)
                .body(Map.of("query", mutation))
                .when()
                .post("")
                .then()
                .statusCode(200)
                .contentType("application/json")
                .body("$", not(hasKey("errors")))
                .body("data", notNullValue())
                .body("data.createPerson.id", instanceOf(String.class))
                .body("data.createPerson.id", not(emptyOrNullString()))
                .body("data.createPerson.name", equalTo(name))
                .body("data.createPerson.age", equalTo(age));
    }
}
