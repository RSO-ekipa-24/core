package essa.entity;

import io.smallrye.common.constraint.NotNull;
import io.smallrye.common.constraint.Nullable;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "property_group")
public class PropertyGroup extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "propertyGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Property> properties = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "user_id_fk")
    private User user;

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

    public void addProperty(@NotNull Property p) {
        p.setPropertyGroup(this);
    }

    public void removeProperty(@NotNull Property p) {
        if (properties.remove(p)) {
            p.setPropertyGroup(null);
        }
    }

    @NotNull
    public List<Property> getProperties() {
        return properties;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
