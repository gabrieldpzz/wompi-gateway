package com.ejemplo.wompigateway.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import java.util.UUID;
import java.util.HashMap;
import java.util.Map;

@Controller
public class CompraController {

    private final String TOKEN_URL = "https://id.wompi.sv/connect/token";
    private final String TRANSACCION_URL = "https://api.wompi.sv/TransaccionCompra/3Ds";
    private final String ENLACE_PAGO_URL = "https://api.wompi.sv/EnlacePago";

    @RequestMapping("/")
    public String form() {
        return "form"; // nombre del archivo HTML sin extensión
    }

    @PostMapping("/transaccion")
    public String transaccion(
            @RequestParam String numero_tarjeta,
            @RequestParam String cvv,
            @RequestParam int mes_vencimiento,
            @RequestParam int anio_vencimiento,
            @RequestParam double monto,
            @RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam String email,
            @RequestParam String ciudad,
            @RequestParam String direccion,
            @RequestParam String codigo_postal,
            @RequestParam String telefono,
            Model model
    ) {
        String token = obtenerToken();

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("tarjetaCreditoDebido", Map.of(
                "numeroTarjeta", numero_tarjeta,
                "cvv", cvv,
                "mesVencimiento", mes_vencimiento,
                "anioVencimiento", anio_vencimiento
        ));
        requestBody.put("monto", monto);
        requestBody.put("urlRedirect", "https://ficticio.com");
        requestBody.put("nombre", nombre);
        requestBody.put("apellido", apellido);
        requestBody.put("email", email);
        requestBody.put("ciudad", ciudad);
        requestBody.put("direccion", direccion);
        requestBody.put("idPais", "SV");
        requestBody.put("idRegion", "SV-CF");
        requestBody.put("codigoPostal", codigo_postal);
        requestBody.put("telefono", telefono);

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.set("Content-Type", "application/json-patch+json");

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<Map> response = restTemplate.exchange(TRANSACCION_URL, HttpMethod.POST, entity, Map.class);

        model.addAttribute("response", response.getBody());
        return "response"; // nombre del archivo HTML de respuesta
    }

    @PostMapping("/transaccion/enlacePago")
    public String enlacePago(
            @RequestParam double monto,
            Model model
    ) {
        String token = obtenerToken();

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("idAplicativo", "");
        String identificadorEnlaceComercio = UUID.randomUUID().toString();
        requestBody.put("identificadorEnlaceComercio", identificadorEnlaceComercio);
        requestBody.put("monto", monto);
        requestBody.put("nombreProducto", "Perolito Shop"); // Valor fijo
        requestBody.put("formaPago", Map.of(
                "permitirTarjetaCreditoDebido", true,
                "permitirPagoConPuntoAgricola", false,
                "permitirPagoEnCuotasAgricola", true,
                "permitirPagoEnBitcoin", false
        ));
        requestBody.put("cantidadMaximaCuotas", "12");
        requestBody.put("infoProducto", Map.of(
                "descripcionProducto", "Estas  a punto de obtener tus productos de Perolito Shop",
                "urlImagenProducto", "https://i.imgur.com/m60HKKt.png"
        ));
        requestBody.put("configuracion", Map.of(
                "urlRedirect", "http://localhost:8080",
                "esMontoEditable", false,
                "esCantidadEditable", false,
                "cantidadPorDefecto", 1,
                "duracionInterfazIntentoMinutos", 15,
                "urlRetorno", "http://localhost:8080",
                "emailsNotificacion", "g.alexis7112@gmail.com",
                "urlWebhook", "https://example.com/webhook",
                "telefonosNotificacion", "60696984",
                "notificarTransaccionCliente", true
        ));
        requestBody.put("vigencia", Map.of(
                "fechaInicio", "2024-09-22T00:00:00.000Z",
                "fechaFin", "2024-12-22T23:59:59.999Z"
        ));
        requestBody.put("limitesDeUso", Map.of(
                "cantidadMaximaPagosExitosos", 1000,
                "cantidadMaximaPagosFallidos", 5
        ));

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.set("Content-Type", "application/json-patch+json");

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<Map> response = restTemplate.exchange(ENLACE_PAGO_URL, HttpMethod.POST, entity, Map.class);

        model.addAttribute("response", response.getBody());

        // Redirigir a la URL del enlace
        String urlEnlace = (String) response.getBody().get("urlEnlace");
        return "redirect:" + urlEnlace; // Redirige a la URL recibida
    }

    private String obtenerToken() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/x-www-form-urlencoded");

        String body = "grant_type=client_credentials&client_id=2c27b2f9-845a-48b3-8a12-0ae2aa3761c7&client_secret=cde4dceb-39fd-4996-bc84-e040201870e7&audience=wompi_api";

        HttpEntity<String> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.exchange(TOKEN_URL, HttpMethod.POST, request, Map.class);

        return (String) response.getBody().get("access_token");
    }
}
