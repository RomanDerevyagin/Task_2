import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.Login;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class ChangeEmailTest {
    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.education-services.ru/api";
    }
    @Test
    @DisplayName("Изменение email пользователя")
    void changeUserEmail() {
        Response response = sendPostAuth();
        Response response1 = changeEmailUser(response);
        compareRequest(response1);
    }
    @Step("Отправка POST-запроса на авторизацию пользователя")
    public Response sendPostAuth() {
        Login courier = new Login("change@email.ru", "12345678");
        return given()
                .header("Content-Type", "application/json")
                .body(courier)
                .post("/auth/login");
    }
    @Step("Сохранение токена и изменение email пользователя")
    public Response changeEmailUser(Response response) {
        String bearer = response.jsonPath().getString("accessToken");
        String json = "{\"email\": \"change22@email.ru\", \"name\": \"name\"}";
        return given()
                .header("Content-Type", "application/json")
                .header("Authorization", bearer)
                .body(json)
                .patch("/auth/user");
    }
    @Step("Проверка: статус 200 и success: true")
    public void compareRequest(Response response){
        response.then()
                .body("user.email", equalTo("change22@email.ru"))
                .and()
                .statusCode(200);
    }
    @AfterEach
    void returnOriginalUser() {
        Response response = sendPost();
        if (response.statusCode() == 200) {
            returnUser(response);
        }
    }
    public Response sendPost() {
        Login courier = new Login("change22@email.ru", "12345678");
        return given()
                .header("Content-Type", "application/json")
                .body(courier)
                .post("/auth/login");
    }
    public Response returnUser(Response response) {
        String bearer = response.jsonPath().getString("accessToken");
        String json = "{\"email\": \"change@email.ru\", \"name\": \"test\"}";
        return given()
                .header("Content-Type", "application/json")
                .header("Authorization", bearer)
                .body(json)
                .patch("/auth/user");
    }
}
