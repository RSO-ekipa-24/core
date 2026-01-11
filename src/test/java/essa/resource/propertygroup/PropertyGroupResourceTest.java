package essa.resource.propertygroup;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import essa.entity.Property;
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
class PropertyGroupResourceTest {

    private static final String SUBJECT = "Jakže-Strbec";

    @Inject
    EntityManager entityManager;

    private Long userId;
    private Long existingGroupId;
    private Long existingGroupPropertyId;

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

        Tag tag = new Tag();
        tag.setName("SeedTag");
        tag.setColor("#112233");
        tag.setUser(user);
        entityManager.persist(tag);

        PropertyGroup propertyGroup = new PropertyGroup();
        propertyGroup.setName("SeedGroup");
        propertyGroup.setUser(user);
        entityManager.persist(propertyGroup);

        Property property = new Property();
        property.setName("SeedProperty");
        property.setDescription("SeedDescription");
        property.setUser(user);
        property.setPropertyGroup(propertyGroup);
        property.getTags().add(tag);
        entityManager.persist(property);

        entityManager.flush();

        userId = user.getId();
        existingGroupId = propertyGroup.getId();
        existingGroupPropertyId = property.getId();
    }

    @Test
    @TestSecurity(user = SUBJECT)
    @JwtSecurity(claims = { @Claim(key = "sub", value = SUBJECT) })
    void propertyGroupCrudAndQueries() {

        Long createdGroupId =
                given()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("""
                                {
                                  "name": "Favorites"
                                }
                                """)
                        .when()
                        .post("/property-groups")
                        .then()
                        .statusCode(200)
                        .body("id", notNullValue())
                        .body("name", equalTo("Favorites"))
                        .extract()
                        .jsonPath().getLong("id");

        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body("""
                        {
                          "id": %d,
                          "name": "Favorites (updated)"
                        }
                        """.formatted(createdGroupId))
                .when()
                .put("/property-groups")
                .then()
                .statusCode(200)
                .body("id", equalTo(createdGroupId.intValue()))
                .body("name", equalTo("Favorites (updated)"));

        given()
                .when()
                .get("/property-groups/{id}", createdGroupId)
                .then()
                .statusCode(200)
                .body("id", equalTo(createdGroupId.intValue()))
                .body("name", equalTo("Favorites (updated)"));

        given()
                .when()
                .get("/property-groups/for-user/{userId}", userId)
                .then()
                .statusCode(200)
                .body("$", not(empty()))
                .body("id", hasItems(existingGroupId.intValue(), createdGroupId.intValue()));

        given()
                .when()
                .get("/property-groups/{id}/properties", existingGroupId)
                .then()
                .statusCode(200)
                .body("$", hasSize(1))
                .body("[0].id", equalTo(existingGroupPropertyId.intValue()))
                .body("[0].name", equalTo("SeedProperty"))
                .body("[0].description", equalTo("SeedDescription"))
                .body("[0].propertyGroupId", equalTo(existingGroupId.intValue()))
                .body("[0].tags", hasSize(1))
                .body("[0].tags[0].name", equalTo("SeedTag"))
                .body("[0].tags[0].color", equalTo("#112233"));

        given()
                .when()
                .delete("/property-groups/{id}", createdGroupId)
                .then()
                .statusCode(anyOf(is(204), is(200)));
    }
}