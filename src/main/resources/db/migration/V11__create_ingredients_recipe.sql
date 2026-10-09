CREATE TABLE ingredient_recipe (
    id_recipe INTEGER,
    id_ingredient INTEGER,
    quantity INTEGER NOT NULL,
    unit_id INTEGER NOT NULL,

    CONSTRAINT pk_ingredients_recipe
        PRIMARY KEY (id_recipe, id_ingredient),

    CONSTRAINT ck_ingredient_recipe_quantity_positive
        CHECK (quantity > 0),
    
    CONSTRAINT fk_ingredient_recipe_recipe
        FOREIGN KEY (id_recipe)
        REFERENCES recipe(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_ingredient_recipe_ingredient
        FOREIGN KEY (id_ingredient)
        REFERENCES ingredients(id),

    CONSTRAINT fk_ingredients_recipe_unit
        FOREIGN KEY (unit_id)
        REFERENCES unit_type(id)
)