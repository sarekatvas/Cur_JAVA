package com.example.demo.Service;

import com.example.demo.Entity.Location;
import com.example.demo.Entity.Manufacturer;
import com.example.demo.Repositories.LocationRepository;
import com.example.demo.Repositories.ManufacturerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationService {
    @Autowired
    private LocationRepository locationRepository;

    public List<Location> findAll() {
        return locationRepository.findAll();
    }

    public Location save(Location location) {
        return locationRepository.save(location);
    }

    public void delete(Long id) {
        locationRepository.deleteById(id);
    }

    public Location findById(Long id) {
        return locationRepository.findById(id).orElse(null);
    }

    public Location findByName(String name) {
        return locationRepository.findByName(name).orElse(null);
    }
}