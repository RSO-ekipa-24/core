package essa.entity;

import io.smallrye.common.constraint.NotNull;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tag")
public class Tag extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 7) // Assuming hex color format like #RRGGBB
    private String color = "#000000"; // Default color

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id_fk", updatable = false)
    private User user;

    @ManyToMany(mappedBy = "tags")
    private Set<Property> properties = new HashSet<>();

    @NotNull
    public Long getId() {
        return id;
    }

    @NotNull
    public String getName() {
        return name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    @NotNull
    public String getColor() {
        return color;
    }

    public void setColor(@NotNull String color) {
        this.color = color;
    }

    @NotNull
    public User getUser() {
        return user;
    }

    public void setUser(@NotNull User user) {
        this.user = user;
    }

    @NotNull
    public Set<Property> getProperties() {
        return properties;
    }

    public void setProperties(@NotNull Set<Property> properties) {
        this.properties = properties;
    }

    public void addProperty(Property property) {
        if (properties.add(property)) {
            property.getTags().add(this);
        }
    }

    public void removeProperty(Property property) {
        if (properties.remove(property)) {
            property.getTags().remove(this);
        }
    }
}