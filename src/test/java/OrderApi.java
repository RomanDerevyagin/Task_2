import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;


public class OrderApi {
    private final RequestSpecification spec;

    public OrderApi(RequestSpecification spec) {
        this.spec = spec;
    }

    public Response getOrders(String token) {
        return spec.header("Authorization", token).get("/orders");
    }

    public Response getOrdersWithoutAuth() {
        return spec.get("/orders");
    }
    public Response createOrderWithoutBody(String token) {
        return spec.header("Authorization", token).post("/orders");
    }
    public Response createOrder(String token, String ingredientsJson) {
        return spec.header("Authorization", token).body(ingredientsJson).post("/orders");
    }

    public Response createOrderWithoutAuth(String ingredientsJson) {
        return spec.body(ingredientsJson).post("/orders");
    }
}