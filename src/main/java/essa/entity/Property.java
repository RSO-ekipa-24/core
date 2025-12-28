package essa.entity;

import io.smallrye.common.constraint.NotNull;
import io.smallrye.common.constraint.Nullable;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "property")
public class Property extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column
    private String description;

    @ManyToOne
    @JoinColumn(name = "property_group_id_fk")
    private PropertyGroup propertyGroup;

    @ManyToOne
    @JoinColumn(name = "user_id_fk")
    private User user;

    @ManyToMany
    @JoinTable(
            name = "property_tag",
            joinColumns = @JoinColumn(name = "property_id_fk"),
            inverseJoinColumns = @JoinColumn(name = "tag_id_fk")
    )
    private Set<Tag> tags = new HashSet<>();

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

    @Nullable
    public String getDescription() {
        return description;
    }

    public void setDescription(@Nullable String description) {
        this.description = description;
    }

    @Nullable
    public PropertyGroup getPropertyGroup() {
        return propertyGroup;
    }

    public void setPropertyGroup(@Nullable PropertyGroup propertyGroup) {
        if (Objects.equals(this.propertyGroup, propertyGroup)) {
            return;
        }

        if (this.propertyGroup != null) {
            this.propertyGroup.getProperties().remove(this);
        }

        this.propertyGroup = propertyGroup;

        if (propertyGroup != null && !propertyGroup.getProperties().contains(this)) {
            propertyGroup.getProperties().add(this);
        }
    }

    @Nullable
    public User getUser() {
        return user;
    }

    public void setUser(@Nullable User user) {
        this.user = user;
    }

    @NotNull
    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(@NotNull Set<Tag> tags) {
        this.tags = tags;
    }

    public void addTag(@NotNull Tag tag) {
        if (tags.add(tag)) {
            tag.getProperties().add(this);
        }
    }

    public void removeTag(@NotNull Tag tag) {
        if (tags.remove(tag)) {
            tag.getProperties().remove(this);
        }
    }
}
