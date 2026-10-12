package dev.ruben.cookandshare.recipe.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

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

    public RecipeEntity() {
    }

    public RecipeEntity(
            Integer id,
            String name,
            String description,
            String instructions,
            String imageUrl,
            Integer servings,
            UserDetailsEntity author,
            RecipeStatusEntity recipeStatus,
            Integer totalCalories,
            Integer caloriesPerServing,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            LocalDateTime publishedAt,
            LocalDateTime reviewedAt,
            UserDetailsEntity reviewer,
            boolean status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.instructions = instructions;
        this.imageUrl = imageUrl;
        this.servings = servings;
        this.author = author;
        this.recipeStatus = recipeStatus;
        this.totalCalories = totalCalories;
        this.caloriesPerServing = caloriesPerServing;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.publishedAt = publishedAt;
        this.reviewedAt = reviewedAt;
        this.reviewer = reviewer;
        this.status = status;
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getServings() {
        return servings;
    }

    public void setServings(Integer servings) {
        this.servings = servings;
    }

    public UserDetailsEntity getAuthor() {
        return author;
    }

    public void setAuthor(UserDetailsEntity author) {
        this.author = author;
    }

    public RecipeStatusEntity getRecipeStatus() {
        return recipeStatus;
    }

    public void setRecipeStatus(RecipeStatusEntity recipeStatus) {
        this.recipeStatus = recipeStatus;
    }

    public Set<CategoryEntity> getCategories() {
        return categories;
    }

    public void setCategories(Set<CategoryEntity> categories) {
        this.categories = categories;
    }

    public Integer getTotalCalories() {
        return totalCalories;
    }

    public void setTotalCalories(Integer totalCalories) {
        this.totalCalories = totalCalories;
    }

    public Integer getCaloriesPerServing() {
        return caloriesPerServing;
    }

    public void setCaloriesPerServing(Integer caloriesPerServing) {
        this.caloriesPerServing = caloriesPerServing;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }

    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(LocalDateTime reviewedAt) {
        this.reviewedAt = reviewedAt;
    }

    public UserDetailsEntity getReviewer() {
        return reviewer;
    }

    public void setReviewer(UserDetailsEntity reviewer) {
        this.reviewer = reviewer;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
