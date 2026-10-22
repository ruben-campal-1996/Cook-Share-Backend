package dev.ruben.cookandshare.recipe.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import dev.ruben.cookandshare.auth.entity.UserDetailsEntity;
import dev.ruben.cookandshare.categories.entity.CategoryEntity;
import dev.ruben.cookandshare.recipe.status.entity.RecipeStatusEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "recipe")
@Getter
@Setter
@NoArgsConstructor
public class RecipeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 155)
    private String name;

    @Column(nullable = true, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = true, columnDefinition = "TEXT")
    private String instructions;

    @Column(name = "image_url", nullable = false, columnDefinition = "TEXT")
    private String imageUrl;

    @Column(nullable = false)
    private Integer servings;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private UserDetailsEntity author;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "status_id", nullable = false)
    private RecipeStatusEntity recipeStatus;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "category_recipe",
            joinColumns = @JoinColumn(name = "id_recipe"),
            inverseJoinColumns = @JoinColumn(name = "id_category"))
    private Set<CategoryEntity> categories = new HashSet<>();

    @Column(name = "total_calories")
    private Integer totalCalories;

    @Column(name = "calories_per_serving")
    private Integer caloriesPerServing;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false, name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by")
    private UserDetailsEntity reviewer;

    @Column(nullable = false)
    private boolean status = true;

    @Builder
    public RecipeEntity(
            String name,
            String description,
            String instructions,
            String imageUrl,
            Integer servings,
            Integer totalCalories,
            Integer caloriesPerServing) {
        this.name = name;
        this.description = description;
        this.instructions = instructions;
        this.imageUrl = imageUrl;
        this.servings = servings;
        this.totalCalories = totalCalories;
        this.caloriesPerServing = caloriesPerServing;
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
