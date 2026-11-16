import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.example.Login;
import org.example.User;

import static io.restassured.RestAssured.given;

public class AuthApi {
    private final RequestSpecification spec;

    public AuthApi(RequestSpecification spec) {
        this.spec = spec;
    }

    public Response login(Login login) {
        return spec.body(login).post("/auth/login");
    }

    public Response register(User user) {
        return spec.body(user).post("/auth/register");
    }

    public Response updateUserWithoutAuth(User user) {
        return spec.body(user).patch("/auth/user");
    }

    public Response updateUser(String token, User user) {
        return spec.header("Authorization", token).body(user).patch("/auth/user");  // БЕЗ "Bearer "
    }

    public Response deleteUser(String token) {
        return given().header("Authorization", token).delete("/auth/user");  // БЕЗ "Bearer "
    }
}