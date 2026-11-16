import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.example.Login;
import org.example.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.equalTo;

public class ChangeNameTest extends BaseTest {
    private AuthApi authApi;
    private String token;

    @BeforeEach
    public void init() {
        authApi = new AuthApi(requestSpec());
    }

    @Test
    @DisplayName("Изменение имени пользователя")
    void changeUserName() {
        Response login = authApi.login(new Login("change@test.ru", "12345678"));
        token = login.jsonPath().getString("accessToken");

        Response response = changeNameUser(token, "change@test.ru", "qwerty");
        compareRequest(response);
    }

    @Step("Изменение имени пользователя с токеном")
    public Response changeNameUser(String token, String email, String name) {
        return authApi.updateUser(token, new User(email, null, name));
    }

    @Step("Проверка: статус 200 и user.name = qwerty")
    public void compareRequest(Response response) {
        response.then()
                .body("user.name", equalTo("qwerty"))
                .and()
                .statusCode(200);
    }

    @AfterEach
    void returnOriginalUser() {
        if (token != null) {
            authApi.updateUser(token, new User("change@test.ru", null, "test"));
        }
    }
}