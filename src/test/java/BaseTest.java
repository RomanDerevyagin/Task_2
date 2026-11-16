import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;

import static io.restassured.RestAssured.given;

public class BaseTest {
    protected static final String BASE_URL = "https://stellarburgers.education-services.ru/api";

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = BASE_URL;
    }

    protected RequestSpecification requestSpec() {
        return given().header("Content-Type", "application/json");
    }
}