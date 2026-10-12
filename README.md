# Cook&Share

¡Bienvenido a Cook&Share! 

Un espacio donde podrás encontrar esa receta que siempre quisiste comer pero que nunca encontraste, y lo mejor es que.... ¡Puedes compartir las tuyas propias!

Puedes aprender a cocinar.. hay recetas de todos los niveles o tambien sorprender a tu pareja con algo especial (Menudo Romeo.. o Julietta 😎)

## ¿Que te vas a encontrar aquí?
Esta parte de la aplicación se compone de un servicio API REST dedicado a darle soporte al frontend utilizando una arquitectura por features y añadiendo capas de seguridad como CORS o OAuth2 además de otro tipo de dependencias que le dan seguridad y legibilidad al código.

## Pruebas

La suite está organizada en tres capas:

- **Unitarias con mocks**: prueban los servicios sin levantar Spring ni conectar a una base de datos.
- **Integración con Mockito y Testcontainers**: valida servicios y persistencia contra PostgreSQL real en un contenedor.
- **End-to-end**: prueba los endpoints HTTP, la seguridad, la validación y los flujos principales de autenticación, perfil, categorías, ingredientes y recetas.

Ejecuta todas las pruebas desde la raíz del backend con `.\mvnw.cmd test`. Las pruebas de integración y end-to-end requieren Docker disponible. El informe de cobertura JaCoCo se genera en `target/site/jacoco/index.html`.