import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.Login;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class CreateOrderTest {
    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.education-services.ru/api";
    }
    @Test
    @DisplayName("Создание заказа авторизованным пользователем и с ингридиентами")
    void createOrderWithAuth() {
        Response response = sendPostAuth();
        Response response1 = createOrder(response);
        compareRequest(response);
        compareRequest1(response1);
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
    @Step("Отправка POST-запроса на создание заказа")
    public Response createOrder(Response response){
        String bearer = response.jsonPath().getString("accessToken");
        String json = "{\"ingredients\": [\"6ae327a87e8698001b542b3f\", \"6ae327a87e8698001b542b38\"]}";
        return given()
                .header("Content-Type", "application/json")
                .header("Authorization", bearer)
                .body(json)
                .post("/orders");
    }
    @Step("Проверка: статус 200 и success: true")
    public void compareRequest1(Response response) {
        response.then()
                .body("success", equalTo(true))
                .and().body("order.status", equalTo("created"))
                .and()
                .statusCode(200);
    }
    @Test
    @DisplayName("Создание заказа не авторизованным пользователем")
    void createOrderWithoutAuth() {
        Response response2 = createOrder1();
        compareRequest2(response2);
    }
    @Step("Отправка POST-запроса на создание заказа")
    public Response createOrder1(){
        String json = "{\"ingredients\": [\"6ae327a87e8698001b542b3f\", \"6ae327a87e8698001b542b38\"]}";
        return given()
                .header("Content-Type", "application/json")
                .body(json)
                .post("/orders");
    }
    @Step("Проверка: статус 200 , success: true и поля статус отсутствует")
    public void compareRequest2(Response response){
        response.then()
                .body("success", equalTo(true))
                .and()
                .body("order.status", nullValue())
                .and()
                .statusCode(200);
    }
    @Test
    @DisplayName("Создание заказа авторизованным пользователем и без ингридиентов")
    void createOrderWithWrongIngridients() {
        Response response3 = sendPostAuth1();
        Response response4 = createOrder2(response3);
        compareRequest3(response3);
        compareRequest4(response4);
    }
    @Step("Отправка POST-запроса на авторизацию пользователя")
    public Response sendPostAuth1() {
        Login courier = new Login("qwee@qwe.ru", "12345");
        return given()
                .header("Content-Type", "application/json")
                .body(courier)
                .post("/auth/login");
    }
    @Step("Проверка: статус 200 и success: true")
    public void compareRequest3(Response response) {
        response.then()
                .body("success", equalTo(true))
                .and()
                .statusCode(200);
    }
    @Step("Отправка POST-запроса на создание заказа")
    public Response createOrder2(Response response){
        String bearer = response.jsonPath().getString("accessToken");
        return given()
                .header("Content-Type", "application/json")
                .header("Authorization", bearer)
                .post("/orders");
    }
    @Step("Проверка: статус 400 и success: false")
    public void compareRequest4(Response response) {
        response.then()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("Ingredient ids must be provided"))
                .and()
                .statusCode(400);
    }
    @Test
    @DisplayName("Создание заказа авторизованным пользователем и c неправильным хэшем ингридиентов")
    void createOrderWithoutIngridients() {
        Response response5 = sendPostAuth2();
        Response response6 = createOrder3(response5);
        compareRequest5(response5);
        compareRequest6(response6);
    }
    @Step("Отправка POST-запроса на авторизацию пользователя")
    public Response sendPostAuth2() {
        Login courier = new Login("qwee@qwe.ru", "12345");
        return given()
                .header("Content-Type", "application/json")
                .body(courier)
                .post("/auth/login");
    }
    @Step("Проверка: статус 200 и success: true")
    public void compareRequest5(Response response) {
        response.then()
                .body("success", equalTo(true))
                .and()
                .statusCode(200);
    }
    @Step("Отправка POST-запроса на создание заказа")
    public Response createOrder3(Response response){
        String bearer = response.jsonPath().getString("accessToken");
        String json = "{\"ingredients\": [\"1ae327a87e8698001b542b3f\", \"2ae327a87e8698001b542b38\"]}";
        return given()
                .header("Content-Type", "application/json")
                .header("Authorization", bearer)
                .body(json)
                .post("/orders");
    }
    @Step("Проверка: статус 400 и success: false")
    public void compareRequest6(Response response) {
        response.then()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("One or more ids provided are incorrect"))
                .and()
                .statusCode(400);
    }
}
