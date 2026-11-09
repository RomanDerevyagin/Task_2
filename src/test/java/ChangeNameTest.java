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

public class ChangeNameTest {
    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.education-services.ru/api";
    }
    @Test
    @DisplayName("Изменение имени пользователя")
    void changeUserName() {
        Response response = sendPostAuth();
        Response response1 = changeNameUser(response);
        compareRequest(response1);
    }
    @Step("Отправка POST-запроса на авторизацию пользователя")
    public Response sendPostAuth() {
        Login courier = new Login("change@test.ru", "12345678");
        return given()
                .header("Content-Type", "application/json")
                .body(courier)
                .post("/auth/login");
    }
    @Step("Сохранение токена и изменение email пользователя")
    public Response changeNameUser(Response response) {
        String bearer = response.jsonPath().getString("accessToken");
        String json = "{\"email\": \"change@test.ru\", \"name\": \"qwerty\"}";
        return given()
                .header("Content-Type", "application/json")
                .header("Authorization", bearer)
                .body(json)
                .patch("/auth/user");
    }
    @Step("Проверка: статус 200 и success: true")
    public void compareRequest(Response response){
        response.then()
                .body("user.name", equalTo("qwerty"))
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
        Login courier = new Login("change@test.ru", "12345678");
        return given()
                .header("Content-Type", "application/json")
                .body(courier)
                .post("/auth/login");
    }
    public Response returnUser(Response response) {
        String bearer = response.jsonPath().getString("accessToken");
        String json = "{\"email\": \"change@test.ru\", \"name\": \"test\"}";
        return given()
                .header("Content-Type", "application/json")
                .header("Authorization", bearer)
                .body(json)
                .patch("/auth/user");
    }
    }
