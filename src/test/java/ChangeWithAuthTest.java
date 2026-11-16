import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.example.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.equalTo;

public class ChangeWithAuthTest extends BaseTest {
    private AuthApi authApi;

    @BeforeEach
    public void init() {
        authApi = new AuthApi(requestSpec());
    }

    @Test
    @DisplayName("Изменение имени пользователя без авторизации")
    void changeUserName() {
        Response response = changeNameUser("change@test.ru", "qwerty");
        compareRequest(response);
    }

    @Step("Изменение email/имени без токена")
    public Response changeNameUser(String email, String name) {
        return authApi.updateUserWithoutAuth(new User(email, null, name));
    }

    @Step("Проверка: статус 401 и success: false")
    public void compareRequest(Response response) {
        response.then()
                .body("message", equalTo("You should be authorised"))
                .and()
                .statusCode(401);
    }
}