import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class ChangeWithAuthTest {
    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.education-services.ru/api";
    }
    @Test
    @DisplayName("Изменение имени пользователя")
    void changeUserName() {
        Response response1 = changeNameUser();
        compareRequest(response1);
    }
    @Step("Сохранение токена и изменение email пользователя")
    public Response changeNameUser() {
        String json = "{\"email\": \"change@test.ru\", \"name\": \"qwerty\"}";
        return given()
                .header("Content-Type", "application/json")
                .body(json)
                .patch("/auth/user");
    }
    @Step("Проверка: статус 401 и success: false")
    public void compareRequest(Response response){
        response.then()
                .body("message", equalTo("You should be authorised"))
                .and()
                .statusCode(401);
    }
}

