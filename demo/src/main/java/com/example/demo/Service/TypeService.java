package com.example.demo.Service;

import com.example.demo.Entity.Type;
import com.example.demo.Repositories.TypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TypeService {
    @Autowired
    private TypeRepository typeRepository;

    public List<Type> findAll() {
        return typeRepository.findAll();
    }

    public Type save(Type type) {
        return typeRepository.save(type);
    }

    public void delete(Long id) {
        typeRepository.deleteById(id);
    }

    public Type findById(Long id) {
        return typeRepository.findById(id).orElse(null);
    }
}