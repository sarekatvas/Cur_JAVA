package com.example.demo.Service;

import com.example.demo.Entity.Device;
import com.example.demo.Entity.Network;
import com.example.demo.Repositories.DeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeviceService {
    @Autowired
    private DeviceRepository deviceRepository;

    public List<Device> findAll() {
        return deviceRepository.findAll();
    }

    public Device save(Device device) {
        return deviceRepository.save(device);
    }

    public void delete(Long id) {
        deviceRepository.deleteById(id);
    }

    public Device findById(Long id) {
        return deviceRepository.findById(id).orElse(null);
    }

    public List<Device> findByNetwork(Network network){
        return deviceRepository.findByNetwork(network);
    }
}