INSERT INTO recipe (
    name,
    description,
    image_url,
    instructions,
    servings,
    author_id,
    status_id,
    total_calories,
    calories_per_serving,
    reviewed_by
) VALUES
(
    'Pasta con pollo y espinacas',
    'Una receta de pasta cremosa con pollo, espinacas y tomate. Rica en proteínas y perfecta para una comida equilibrada.',
    './images/Pasta_pollo_espinacas.jpg',
    '1. Cuece la pasta siguiendo las instrucciones del fabricante.
2. Corta el pollo en dados y la cebolla y el ajo en trozos pequeños.
3. Calienta el aceite de oliva en una sartén y cocina la cebolla y el ajo.
4. Añade el pollo y cocínalo completamente.
5. Incorpora el tomate y las espinacas y cocina hasta que las verduras estén tiernas.
6. Añade la pasta cocida y mezcla todos los ingredientes.
7. Sirve con queso parmesano rallado por encima.',
    2,
    1,
    1,
    835,
    418,
    2
),
(
    'Ensalada de garbanzos y aguacate',
    'Una ensalada vegana, fresca y nutritiva elaborada con garbanzos, aguacate, tomate y lechuga, aliñada con limón y aceite de oliva.',
    './images/Ensalada_garbanzos_aguacate.jpg',
    '1. Escurre y enjuaga los garbanzos cocidos.
2. Lava la lechuga y el tomate.
3. Corta el tomate, el aguacate y la cebolla en trozos pequeños.
4. Coloca los garbanzos, la lechuga, el tomate, el aguacate y la cebolla en un bol.
5. Mezcla el zumo de limón con el aceite de oliva.
6. Aliña la ensalada y remueve suavemente antes de servir.',
    2,
    1,
    1,
    933,
    467,
    2
);