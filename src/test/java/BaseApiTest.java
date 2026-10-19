import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.LogConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.cookie.CookieFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;

abstract class BaseApiTest {

    protected RequestSpecification rest;
    protected RequestSpecification graphql;

    @BeforeEach
    void configureApiClients() {
        rest = createSpecification("https://playground.nileslabs.com", "/api/v1");
        // CookieFilter сохраняет сессию стенда между шагами одного теста.
        rest.filter(new CookieFilter());
        graphql = createSpecification("https://graphql.postman-echo.com", "/graphql");
    }

    private RequestSpecification createSpecification(String baseUri, String basePath) {
        return new RequestSpecBuilder()
                .setBaseUri(baseUri)
                .setBasePath(basePath)
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .setConfig(RestAssuredConfig.config()
                        .httpClient(HttpClientConfig.httpClientConfig()
                                .setParam("http.connection.timeout", 20000)
                                .setParam("http.socket.timeout", 20000))
                        .logConfig(LogConfig.logConfig()
                                .enableLoggingOfRequestAndResponseIfValidationFails()
                                .blacklistHeader("Authorization")))
                .build();
    }
}
