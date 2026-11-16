import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.example.Login;
import org.example.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.*;

public class CreateOrderTest extends BaseTest {
    private AuthApi authApi;
    private OrderApi orderApi;

    private static final String VALID_INGREDIENTS =
            "{\"ingredients\": [\"6ae327a87e8698001b542b3f\", \"6ae327a87e8698001b542b38\"]}";
    private static final String INVALID_INGREDIENTS =
            "{\"ingredients\": [\"1ae327a87e8698001b542b3f\", \"2ae327a87e8698001b542b38\"]}";

    @BeforeEach
    public void init() {
        authApi = new AuthApi(requestSpec());
        orderApi = new OrderApi(requestSpec());
        authApi.register(new User("qwee@qwe.ru", "12345", "testik"));
    }

    @Test
    @DisplayName("Создание заказа авторизованным пользователем с ингредиентами")
    void createOrderWithAuth() {
        String token = loginAndGetToken("qwee@qwe.ru", "12345");
        Response response = orderApi.createOrder(token, VALID_INGREDIENTS);
        compareCreatedOrder(response);
    }

    @Test
    @DisplayName("Создание заказа неавторизованным пользователем")
    void createOrderWithoutAuth() {
        Response response = orderApi.createOrderWithoutAuth(VALID_INGREDIENTS);
        compareUnauthorizedOrder(response);
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов")
    void createOrderWithoutIngredients() {
        String token = loginAndGetToken("qwee@qwe.ru", "12345");
        Response response = orderApi.createOrderWithoutBody(token);
        compareMissingIngredients(response);
    }

    @Test
    @DisplayName("Создание заказа с неверным хэшем ингредиентов")
    void createOrderWithInvalidIngredients() {
        String token = loginAndGetToken("qwee@qwe.ru", "12345");
        Response response = orderApi.createOrder(token, INVALID_INGREDIENTS);
        compareInvalidIngredients(response);
    }

    @Step("Авторизация и получение токена")
    public String loginAndGetToken(String email, String password) {
        Response response = authApi.login(new Login(email, password));
        response.then().body("success", equalTo(true)).statusCode(200);
        return response.jsonPath().getString("accessToken");
    }

    @Step("Проверка: статус 200, success: true, order.status = created")
    public void compareCreatedOrder(Response response) {
        response.then()
                .body("success", equalTo(true))
                .and().body("order.status", equalTo("created"))
                .and().statusCode(200);
    }

    @Step("Проверка: статус 200, success: true, order.status отсутствует")
    public void compareUnauthorizedOrder(Response response) {
        response.then()
                .body("success", equalTo(true))
                .and().body("order.status", nullValue())
                .and().statusCode(200);
    }

    @Step("Проверка: статус 400 и сообщение Ingredient ids must be provided")
    public void compareMissingIngredients(Response response) {
        response.then()
                .body("success", equalTo(false))
                .and().body("message", equalTo("Ingredient ids must be provided"))
                .and().statusCode(400);
    }

    @Step("Проверка: статус 400 и сообщение One or more ids provided are incorrect")
    public void compareInvalidIngredients(Response response) {
        response.then()
                .body("success", equalTo(false))
                .and().body("message", equalTo("One or more ids provided are incorrect"))
                .and().statusCode(400);
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