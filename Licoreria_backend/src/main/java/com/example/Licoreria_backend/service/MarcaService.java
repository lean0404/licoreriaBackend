package com.example.Licoreria_backend.service;

import com.example.Licoreria_backend.model.Marca;
import com.example.Licoreria_backend.repository.MarcaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MarcaService {

    private final MarcaRepository marcaRepository;

    public MarcaService(MarcaRepository marcaRepository) {
        this.marcaRepository = marcaRepository;
    }

    public List<Marca> listarTodos() {
        return marcaRepository.findAll();
    }

    public Optional<Marca> obtenerPorId(Long id) {
        return marcaRepository.findById(id);
    }

    public Marca guardar(Marca marca) {
        return marcaRepository.save(marca);
    }

    public void eliminar(Long id) {
        marcaRepository.deleteById(id);
    }
}
