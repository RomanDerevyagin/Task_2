import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.example.Login;
import org.example.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.equalTo;

public class LoginTest extends BaseTest {
    private AuthApi authApi;
    private String token;

    @BeforeEach
    public void init() {
        authApi = new AuthApi(requestSpec());
        Response register = authApi.register(new User("qwee@qwe.ru", "12345", "testik"));
    }

    @Test
    @DisplayName("Авторизация пользователя")
    void loginUser() {
        Response response = sendPostAuth("qwee@qwe.ru", "12345");
        compareRequest(response);
        token = response.jsonPath().getString("accessToken");
    }

    @Step("Отправка POST-запроса на авторизацию пользователя")
    public Response sendPostAuth(String email, String password) {
        return authApi.login(new Login(email, password));
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
        Response response = sendPostAuth("qwee@qwe.ru", "54321");
        compareRequestWrongPass(response);
    }

    @Step("Проверка: статус 401 и success: false")
    public void compareRequestWrongPass(Response response) {
        response.then()
                .body("success", equalTo(false))
                .and()
                .statusCode(401);
    }

    @AfterEach
    void deleteUser() {
        Response login = authApi.login(new Login("qwee@qwe.ru", "12345"));
        if (login.statusCode() == 200) {
            String t = login.jsonPath().getString("accessToken");
            authApi.deleteUser(t);
        }
    }
}