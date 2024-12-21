package com.example.demo.Service;

import com.example.demo.Entity.Manufacturer;
import com.example.demo.Repositories.ManufacturerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ManufacturerService {
    @Autowired
    private ManufacturerRepository manufacturerRepository;

    public List<Manufacturer> findAll() {
        return manufacturerRepository.findAll();
    }

    public Manufacturer save(Manufacturer manufacturer) {
        return manufacturerRepository.save(manufacturer);
    }

    public void delete(Long id) {
        manufacturerRepository.deleteById(id);
    }

    public Manufacturer findById(Long id) {
        return manufacturerRepository.findById(id).orElse(null);
    }
}