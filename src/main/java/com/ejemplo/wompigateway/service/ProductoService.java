package com.ejemplo.wompigateway.service;

import com.ejemplo.wompigateway.model.Producto;
import com.ejemplo.wompigateway.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {
    @Autowired
    private ProductoRepository productoRepository;

    //creamos una lista
    public List<Producto> findAll() {
        return productoRepository.findAll();
    }
    public Optional<Producto> findById(Long id) {
        return productoRepository.findById(id);
    }
}