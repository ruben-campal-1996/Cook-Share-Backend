package dev.ruben.cookandshare.ingredient.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity 
@Table (name = "ingredients")
public class IngredientEntity {
    @Id 
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column (nullable = false, unique = true, length = 60)
    private String name;

    @Column (columnDefinition = "TEXT")
    private String description;

    @Column (name="image_url", nullable = false, columnDefinition = "TEXT")
    private String imageUrl;

    @Column (name="calories_per_100g", nullable = true)
    private Integer caloriesPer100g;

    
    
    public IngredientEntity() {
    }



    public IngredientEntity(Integer id, String name, String description, String imageUrl, Integer caloriesPer100g) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.caloriesPer100g = caloriesPer100g;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id=id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name=name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl){
        this.imageUrl=imageUrl;
    }

    public Integer getCaloriesPer100g(){
        return caloriesPer100g;
    }

    public void setCaloriesPer100g(Integer caloriesPer100g) {
        this.caloriesPer100g=caloriesPer100g;
    }
}
