import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.example.Login;
import org.example.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.equalTo;

public class ChangeEmailTest extends BaseTest {
    private AuthApi authApi;

    @BeforeEach
    @Step("Создание пользователя с исходным email")
    public void init() {
        authApi = new AuthApi(requestSpec());
        authApi.register(new User("changeemailold@test.ru", "12345678", "test"));
    }

    @Test
    @DisplayName("Изменение email пользователя")
    @Step("Изменение email пользователя")
    void changeUserEmail() {
        Response login = loginUser("changeemailold@test.ru", "12345678");
        String token = login.jsonPath().getString("accessToken");

        Response response = updateEmail(token, "changeemailnew@email.ru", "name");
        compareRequest(response);
    }

    @Step("Авторизация пользователя")
    public Response loginUser(String email, String password) {
        return authApi.login(new Login(email, password));
    }

    @Step("Изменение email пользователя")
    public Response updateEmail(String token, String newEmail, String name) {
        return authApi.updateUser(token, new User(newEmail, null, name));
    }

    @Step("Проверка: статус 200 и email = changeemailnew@email.ru")
    public void compareRequest(Response response) {
        response.then()
                .body("user.email", equalTo("changeemailnew@email.ru"))
                .and()
                .statusCode(200);
    }

    @AfterEach
    @Step("Удаление пользователя")
    void deleteUser() {
        deleteByEmail("changeemailnew@email.ru");
    }

    private void deleteByEmail(String email) {
        Response login = authApi.login(new Login(email, "12345678"));
        if (login.statusCode() == 200) {
            String token = login.jsonPath().getString("accessToken");
            authApi.deleteUser(token);
        }
    }
}