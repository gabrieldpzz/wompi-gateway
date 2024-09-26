package com.ejemplo.wompigateway.controller;


import com.ejemplo.wompigateway.model.Producto;
import com.ejemplo.wompigateway.repository.ProductoRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ProductoController {

    @Autowired
    private ProductoRepository productoRepository;

    // Método para verificar si el usuario está autenticado
    private boolean isAuthenticated(HttpSession session) {
        return session.getAttribute("user") != null;
    }

    @GetMapping("/check-session")
    public ResponseEntity<Map<String, String>> checkSession(HttpSession session) {
        Map<String, String> response = new HashMap<>();
        String user = (String) session.getAttribute("user");

        if (user != null) {
            response.put("user", user);
            return ResponseEntity.ok(response); // Retorna el usuario si está logeado
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // Retorna 401 si no hay sesión
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> cerrarSesion(HttpSession session) {
        session.invalidate(); // Invalida la sesión actual
        return ResponseEntity.ok().build(); // Retorna un estado OK
    }



    @GetMapping("/")
    public String mostrarProductos(HttpSession session, Model model) {
        if (!isAuthenticated(session)) {
            return "redirect:/login"; // Redirige al login si no está autenticado
        }

        List<Producto> productos = productoRepository.findAll();
        model.addAttribute("productos", productos);
        return "productos"; // Devuelve la vista de productos directamente
    }

    @GetMapping("/login")
    public String mostrarLogin() {
        return "index"; // Devuelve la vista de inicio de sesión
    }

    @PostMapping("/login")
    public ResponseEntity<Object> iniciarSesion(@RequestBody Map<String, String> payload, HttpSession session) {
        String email = payload.get("email");
        session.setAttribute("user", email); // Guarda el email o un objeto de usuario
        return ResponseEntity.ok().build(); // Retorna un estado OK
    }

    @GetMapping("/productos")
    public String listarProductos(HttpSession session, Model model) {
        if (!isAuthenticated(session)) {
            return "redirect:/login";
        }

        List<Producto> productos = productoRepository.findAll();
        model.addAttribute("productos", productos);
        return "productos";
    }

    @GetMapping("/form")
    public String mostrarFormularioCompra(@RequestParam double monto, HttpSession session, Model model) {
        if (!isAuthenticated(session)) {
            return "redirect:/login";
        }

        model.addAttribute("monto", monto);
        return "form"; // Vista del formulario de compra
    }

    @PostMapping("/carrito")
    public String agregarAlCarrito(
            @RequestParam Long idProducto,
            HttpSession session,
            Model model
    ) {
        if (!isAuthenticated(session)) {
            return "redirect:/login"; // Redirige al login si no está autenticado
        }

        List<Producto> carrito = (List<Producto>) session.getAttribute("carrito");
        if (carrito == null) {
            carrito = new ArrayList<>();
        }

        Producto producto = productoRepository.findById(idProducto).orElse(null);
        if (producto != null) {
            // Verificar si el producto ya está en el carrito
            boolean productoExiste = false;
            for (Producto prod : carrito) {
                if (prod.getId().equals(producto.getId())) {
                    prod.setCantidad(prod.getCantidad() + 1); // Incrementar la cantidad si ya existe
                    productoExiste = true;
                    break;
                }
            }
            if (!productoExiste) {
                producto.setCantidad(1); // Si no existe, agregarlo con cantidad 1
                carrito.add(producto);
            }
        }

        session.setAttribute("carrito", carrito);
        model.addAttribute("carrito", carrito);
        model.addAttribute("total", calcularTotal(carrito));

        return "carrito";
    }

    @PostMapping("/eliminarUnidad")
    public String eliminarUnaUnidad(
            @RequestParam Long idProducto,
            HttpSession session,
            Model model
    ) {
        if (!isAuthenticated(session)) {
            return "redirect:/login";
        }

        List<Producto> carrito = (List<Producto>) session.getAttribute("carrito");
        if (carrito != null) {
            for (Producto producto : carrito) {
                if (producto.getId().equals(idProducto)) {
                    producto.setCantidad(producto.getCantidad() - 1);
                    if (producto.getCantidad() <= 0) {
                        carrito.remove(producto); // Si la cantidad es 0 o menos, eliminar el producto
                    }
                    break;
                }
            }
            session.setAttribute("carrito", carrito);
        }

        model.addAttribute("carrito", carrito);
        model.addAttribute("total", calcularTotal(carrito));

        return "carrito";
    }

    @PostMapping("/eliminarTodo")
    public String eliminarTodoElProducto(
            @RequestParam Long idProducto,
            HttpSession session,
            Model model
    ) {
        if (!isAuthenticated(session)) {
            return "redirect:/login";
        }

        List<Producto> carrito = (List<Producto>) session.getAttribute("carrito");
        if (carrito != null) {
            carrito.removeIf(producto -> producto.getId().equals(idProducto)); // Eliminar todas las unidades del producto
            session.setAttribute("carrito", carrito);
        }

        model.addAttribute("carrito", carrito);
        model.addAttribute("total", calcularTotal(carrito));

        return "carrito";
    }

    private double calcularTotal(List<Producto> carrito) {
        return carrito.stream()
                .mapToDouble(producto -> producto.getPrecio() * producto.getCantidad()) // Calcular el subtotal para cada producto
                .sum();
    }
}
