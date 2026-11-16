import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.example.Login;
import org.example.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.equalTo;

public class CreateTest extends BaseTest {
    private AuthApi authApi;
    private String createdEmail;
    private String createdPassword;

    @BeforeEach
    public void init() {
        authApi = new AuthApi(requestSpec());
        createdEmail = null;
        createdPassword = null;
    }

    @Test
    @DisplayName("Успешное создание пользователя")
    void createUser() {
        User user = new User("create@qwe.ru", "12345678", "testik");
        Response response = authApi.register(user);
        compareRequest(response);

        createdEmail = user.getEmail();
        createdPassword = user.getPassword();
    }

    @Step("Проверка: статус 200 и success: true")
    public void compareRequest(Response response) {
        response.then()
                .body("success", equalTo(true))
                .and()
                .statusCode(200);
    }

    @Test
    @DisplayName("Создание пользователя, который уже зарегистрирован")
    void createDuplicateUser() {
        User user = new User("duplicate@qwe.ru", "12345", "testik");

        Response existingLogin = authApi.login(new Login(user.getEmail(), user.getPassword()));
        if (existingLogin.statusCode() == 200) {
            String token = existingLogin.jsonPath().getString("accessToken");
            authApi.deleteUser(token);
        }

        authApi.register(user);
        createdEmail = user.getEmail();
        createdPassword = user.getPassword();

        Response response = authApi.register(user);
        compareDuplicateRequest(response);
    }

    @Step("Проверка: статус 403 и сообщение User already exists")
    public void compareDuplicateRequest(Response response) {
        response.then()
                .body("message", equalTo("User already exists"))
                .statusCode(403);
    }

    @Test
    @DisplayName("Создание пользователя без обязательного поля")
    void createUserWithoutField() {
        Response response = authApi.register(new User("qwee@qwe.ru", "12345"));
        compareUserWithoutField(response);
    }

    @Step("Проверка: статус 403 и сообщение о полях")
    public void compareUserWithoutField(Response response) {
        response.then()
                .body("message", equalTo("Email, password and name are required fields"))
                .statusCode(403);
    }

    @AfterEach
    void deleteCreatedUser() {
        if (createdEmail != null) {
            Response login = authApi.login(new Login(createdEmail, createdPassword));
            if (login.statusCode() == 200) {
                String token = login.jsonPath().getString("accessToken");
                authApi.deleteUser(token);
            }
            createdEmail = null;
            createdPassword = null;
        }
    }
}