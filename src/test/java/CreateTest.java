import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.Login;
import org.example.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
public class CreateTest {
    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.education-services.ru/api";
    }
    @Test
    @DisplayName("Успешное создание пользователя")
    void createUser() {
        Response response = sendPostUser();
        compareRequest(response);
    }

    @Step("Отправка POST-запроса на создание курьера")
    public Response sendPostUser() {
        User user = new User("create@qwe.ru", "12345678", "testik");
        return given()
                .header("Content-Type", "application/json")
                .body(user)
                .post("/auth/register");
    }

    @Step("Проверка: статус 200 и success: true")
    public void compareRequest(Response response) {
        response.then()
                .body("success", equalTo(true))
                .and()
                .statusCode(200);
    }
    @Test
    @DisplayName("Cоздание пользователя, который уже зарегистрирован")
    void createDuplicateUser() {
        Response response = sendDuplicatePostUser();
        compareDuplicateRequest(response);
    }

    @Step("Повторная отправка POST-запроса с тем же логином")
    public Response sendDuplicatePostUser() {
        User user = new User("qwee@qwe.ru", "12345", "testik");
        return given()
                .header("Content-Type", "application/json")
                .body(user)
                .post("/auth/register");
    }

    @Step("Проверка: статус 403")
    public void compareDuplicateRequest(Response response) {
        response.then()
                .body("message",equalTo("User already exists"))
                .statusCode(403);
    }
    @Test
    @DisplayName("Cоздание пользователя, без обязательного поля")
    void createUserWithoutField() {
        Response response = sendUserWithoutField();
        compareUserWithoutField(response);
    }

    @Step("Повторная отправка POST-запроса с тем же логином")
    public Response sendUserWithoutField() {
        User user = new User("qwee@qwe.ru", "12345");
        return given()
                .header("Content-Type", "application/json")
                .body(user)
                .post("/auth/register");
    }

    @Step("Проверка: статус 403")
    public void compareUserWithoutField(Response response) {
        response.then()
                .body("message",equalTo("Email, password and name are required fields"))
                .statusCode(403);
    }
    @AfterEach
    void deleteCreateUser() {
        Response response = sendPost();
        if (response.statusCode() == 200) {
            deleteCourier(response);
        }
    }
    public Response sendPost() {
        Login courier = new Login("create@qwe.ru", "12345678");
        return given()
                .header("Content-Type", "application/json")
                .body(courier)
                .post("/auth/login");
    }
    public void deleteCourier(Response response) {
        String bearer = response.jsonPath().getString("accessToken");
        given()
                .header("Authorization",bearer)
                .delete("/auth/user");
    }
}
