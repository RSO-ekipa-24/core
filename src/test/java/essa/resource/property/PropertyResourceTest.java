package essa.resource.property;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import essa.entity.PropertyGroup;
import essa.entity.Tag;
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
class PropertyResourceTest {

    private static final String SUBJECT = "Jakže-Strbec";

    @Inject
    EntityManager entityManager;

    private Long tag1Id;
    private Long tag2Id;
    private Long groupId;

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

        Tag tag1 = new Tag();
        tag1.setName("Apartment");
        tag1.setColor("#FFAA00");
        tag1.setUser(user);
        entityManager.persist(tag1);

        Tag tag2 = new Tag();
        tag2.setName("House");
        tag2.setColor("#00AAFF");
        tag2.setUser(user);
        entityManager.persist(tag2);

        PropertyGroup propertyGroup = new PropertyGroup();
        propertyGroup.setName("IDontKnow");
        propertyGroup.setUser(user);
        entityManager.persist(propertyGroup);

        entityManager.flush();

        tag1Id = tag1.getId();
        tag2Id = tag2.getId();
        groupId = propertyGroup.getId();
    }

    @Test
    @TestSecurity(user = SUBJECT)
    @JwtSecurity(claims = { @Claim(key = "sub", value = SUBJECT) })
    void crudProperty() {

        Long propertyId =
                given()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("""
                                {
                                  "name": "Moj apartman",
                                  "description": "Hopsasa tralala",
                                  "tags": [%d, %d],
                                  "propertyGroupId": %d
                                }
                                """.formatted(tag1Id, tag2Id, groupId))
                        .when()
                        .post("/properties")
                        .then()
                        .statusCode(200)
                        .body("id", notNullValue())
                        .body("name", equalTo("Moj apartman"))
                        .body("description", equalTo("Hopsasa tralala"))
                        .body("propertyGroupId", equalTo(groupId.intValue()))
                        .body("tags", hasSize(2))
                        .body("tags.id", containsInAnyOrder(tag1Id.intValue(), tag2Id.intValue()))
                        .body("tags.name", containsInAnyOrder("Apartment", "House"))
                        .extract()
                        .jsonPath().getLong("id");

        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body("""
                        {
                          "id": %d,
                          "name": "Moj apartman (updated)",
                          "description": "Updated description",
                          "tags": [%d, %d],
                          "propertyGroupId": %d
                        }
                        """.formatted(propertyId, tag1Id, tag2Id, groupId))
                .when()
                .put("/properties")
                .then()
                .statusCode(200)
                .body("id", equalTo(propertyId.intValue()))
                .body("name", equalTo("Moj apartman (updated)"))
                .body("description", equalTo("Updated description"))
                .body("propertyGroupId", equalTo(groupId.intValue()))
                .body("tags", hasSize(2))
                .body("tags.id", containsInAnyOrder(tag1Id.intValue(), tag2Id.intValue()));

        given()
                .when()
                .delete("/properties/{id}", propertyId)
                .then()
                .statusCode(anyOf(is(204), is(200)));
    }
}