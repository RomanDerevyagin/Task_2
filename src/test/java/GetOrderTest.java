import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.Login;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
public class GetOrderTest {
    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.education-services.ru/api";
    }
    @Test
    @DisplayName("Получение списка заказов авторизованным пользователем")
    void getOrderWithAuth() {
        Response response = sendPostAuth();
        Response response1 = getOrder(response);
        compareRequest(response);
        compareRequest1(response1);
    }
    @Step("Отправка POST-запроса на авторизацию пользователя")
    public Response sendPostAuth() {
        Login courier = new Login("change@test.ru", "12345678");
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
    @Step("Отправка Get-запроса на получение списка заказов")
    public Response getOrder(Response response){
        String bearer = response.jsonPath().getString("accessToken");
        return given()
                .header("Content-Type", "application/json")
                .header("Authorization", bearer)
                .get("/orders");
    }
    @Step("Проверка: статус 200 и success: true")
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
        Response response2 = getOrder1();
        compareRequest2(response2);
    }
    @Step("Отправка Get-запроса на получение списка заказов")
    public Response getOrder1(){
        return given()
                .header("Content-Type", "application/json")
                .get("/orders");
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
