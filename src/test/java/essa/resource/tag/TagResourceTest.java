package essa.resource.tag;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import essa.entity.User;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.quarkus.test.security.jwt.Claim;
import io.quarkus.test.security.jwt.JwtSecurity;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class TagResourceTest {

    private static final String SUBJECT = "Jakže-Strbec";

    @Inject
    EntityManager entityManager;

    @BeforeEach
    @Transactional
    void seed() {
        entityManager.createNativeQuery("delete from property_tag").executeUpdate();
        entityManager.createNativeQuery("delete from property").executeUpdate();
        entityManager.createNativeQuery("delete from property_group").executeUpdate();
        entityManager.createNativeQuery("delete from tag").executeUpdate();
        entityManager.createNativeQuery("delete from \"user\"").executeUpdate();

        User user = new User();
        user.setKeycloakId(SUBJECT);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        entityManager.persist(user);

        entityManager.flush();
    }

    @Test
    @TestSecurity(user = SUBJECT)
    @JwtSecurity(claims = { @Claim(key = "sub", value = SUBJECT) })
    void tagCrudAndList() {

        Long createdTagId =
                given()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("""
                                {
                                  "name": "Apartment",
                                  "color": "#FFAA00"
                                }
                                """)
                        .when()
                        .post("/tags")
                        .then()
                        .statusCode(200)
                        .body("id", notNullValue())
                        .body("name", equalTo("Apartment"))
                        .body("color", equalTo("#FFAA00"))
                        .extract()
                        .jsonPath().getLong("id");

        given()
                .when()
                .get("/tags")
                .then()
                .statusCode(200)
                .body("$", hasSize(1))
                .body("[0].id", equalTo(createdTagId.intValue()))
                .body("[0].name", equalTo("Apartment"))
                .body("[0].color", equalTo("#FFAA00"));

        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body("""
                        {
                          "id": %d,
                          "name": "Apartment (updated)",
                          "color": "#00AAFF"
                        }
                        """.formatted(createdTagId))
                .when()
                .put("/tags")
                .then()
                .statusCode(200)
                .body("id", equalTo(createdTagId.intValue()))
                .body("name", equalTo("Apartment (updated)"))
                .body("color", equalTo("#00AAFF"));

        given()
                .when()
                .delete("/tags/{id}", createdTagId)
                .then()
                .statusCode(anyOf(is(204), is(200)));

        given()
                .when()
                .get("/tags")
                .then()
                .statusCode(200)
                .body("$", hasSize(0));
    }
}