# wompi-gateway

## 📦 Descripción del Proyecto

**wompi-gateway** es un sistema de gestión de productos que permite a los usuarios navegar por un catálogo de productos, agregar artículos a un carrito de compras y realizar compras en línea. La aplicación cuenta con autenticación de usuarios y una interfaz amigable para la gestión de productos.

## 🚀 Tecnologías Utilizadas

- **Backend**:
    - Java
    - Spring Boot
    - Jakarta EE (servlets, JSP)

- **Frontend**:
    - HTML
    - CSS
    - JavaScript

- **Base de Datos**:
    - MySQL (base de datos relacional)

- **Herramientas**:
    - Maven (gestión de dependencias)
    - Git (control de versiones)


## 📥 Instalación y Configuración

1. **Clonar el Repositorio**:
   ```bash
   git clone https://github.com/gabrieldpzz/wompi-gateway.git
   cd wompi-gateway

2. **Configuración de dependencias**:   
Asegúrese de tener instalado Maven en su sistema. Para instalar Maven, siga las instrucciones en la [documentación oficial](https://maven.apache.org/install.html).
Ejecutar el siguiente comando para instalar las dependencias del proyecto:
   ```bash
   mvn install
   
3. **Configuración de la Base de Datos**:
Configure las credenciales de la base de datos en el archivo `application.properties` ubicado en `src/main/resources`. Asegúrese de tener una base de datos MySQL en su sistema.
Crear la tabla productos en la base de datos:
   ```sql
   CREATE TABLE productos (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    precio DECIMAL(10, 2) NOT NULL,
    descripcion VARCHAR(100)
    image VARCHAR(100)
   );
   
4. **Ejecutar la Aplicación**:
   ```bash
   mvn spring-boot:run
   
5. **📖 Uso del Proyecto**:
   1. Abra su navegador y vaya a `http://localhost:8080/` para acceder a la aplicación.
   2. Regístrese como usuario o inicie sesión si ya tiene una cuenta.
   3. Navegue por el catálogo de productos y agregue artículos al carrito de compras.
   4. Realice el pago de los productos en el carrito de compras.
   5. Ha completado su compra con éxito.
   6. Para cerrar sesión, haga clic en el botón "Cerrar Sesión" en la barra de navegación.

6. **🌐 Rutas de la API**
    - GET /: Muestra la lista de productos (requiere autenticación).
    - GET /login: Muestra la página de inicio de sesión.
    - POST /login: Inicia sesión y establece la sesión del usuario.
    - POST /carrito: Agrega un producto al carrito.
    - POST /eliminarUnidad: Elimina una unidad de un producto del carrito.
    - POST /eliminarTodo: Elimina todas las unidades de un producto del carrito.

6. **🌐 Rutas de la API Wompi**
   - GET /: Muestra el formulario para realizar una transacción de compra.
   - POST /transaccion: Realiza una transacción de compra utilizando los datos de la tarjeta de crédito y la información del comprador.
   - POST /transaccion/enlacePago: Genera un enlace de pago para el monto especificado y redirige al usuario a esa URL.

