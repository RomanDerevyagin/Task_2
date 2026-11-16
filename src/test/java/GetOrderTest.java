import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.example.Login;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.*;

public class GetOrderTest extends BaseTest {
    private AuthApi authApi;
    private OrderApi orderApi;

    @BeforeEach
    public void init() {
        authApi = new AuthApi(requestSpec());
        orderApi = new OrderApi(requestSpec());
    }

    @Test
    @DisplayName("Получение списка заказов авторизованным пользователем")
    void getOrderWithAuth() {
        Response login = authApi.login(new Login("change@test.ru", "12345678"));
        compareRequest(login);
        String token = login.jsonPath().getString("accessToken");

        Response orders = orderApi.getOrders(token);
        compareRequest1(orders);
    }

    @Step("Проверка: статус 200 и success: true")
    public void compareRequest(Response response) {
        response.then()
                .body("success", equalTo(true))
                .and()
                .statusCode(200);
    }

    @Step("Проверка: статус 200, success: true, orders не пустой")
    public void compareRequest1(Response response) {
        response.then()
                .body("success", equalTo(true))
                .and()
                .body("orders", notNullValue())
                .and()
                .statusCode(200);
    }

    @Test
    @DisplayName("Получение списка заказов не авторизованным пользователем")
    void getOrderWithoutAuth() {
        Response response = orderApi.getOrdersWithoutAuth();
        compareRequest2(response);
    }

    @Step("Проверка: статус 401 и success: false")
    public void compareRequest2(Response response) {
        response.then()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("You should be authorised"))
                .and()
                .statusCode(401);
    }
}