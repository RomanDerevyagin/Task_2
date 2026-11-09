import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.Login;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
public class LoginTest {
    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.education-services.ru/api";
    }
    @Test
    @DisplayName("Авторизация пользователя")
    void loginUser() {
        Response response = sendPostAuth();
        compareRequest(response);
    }
    @Step("Отправка POST-запроса на авторизацию пользователя")
    public Response sendPostAuth() {
        Login courier = new Login("qwee@qwe.ru", "12345");
        return given()
                .header("Content-Type", "application/json")
                .body(courier)
                .post("/auth/login");
    }
    @Step("Проверка: статус 200 и success: true")
    public void compareRequest(Response response) {
        response.then()
                .body("success", equalTo(true))
                .and()
                .statusCode(200);
    }
    @Test
    @DisplayName("Авторизация пользователя c неправильным логином или паролем")
    void loginUserWrongPass() {
        Response response = sendPostAuthWrongPass();
        compareRequestWrongPass(response);
    }
    @Step("Отправка POST-запроса на авторизацию пользователя")
    public Response sendPostAuthWrongPass() {
        Login courier = new Login("qwee@qwe.ru", "54321");
        return given()
                .header("Content-Type", "application/json")
                .body(courier)
                .post("/auth/login");
    }
    @Step("Проверка: статус 401 и success: false")
    public void compareRequestWrongPass(Response response) {
        response.then()
                .body("success", equalTo(false))
                .and()
                .statusCode(401);
    }
}
