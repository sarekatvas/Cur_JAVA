package com.example.demo.Service;

import com.example.demo.Entity.Device;
import com.example.demo.Entity.Network;
import com.example.demo.Repositories.DeviceRepository;
import com.example.demo.Repositories.NetworkRepository;
import com.vaadin.flow.component.charts.model.Inactive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NetworkService {
    @Autowired
    private NetworkRepository networkRepository;
    @Autowired
    private DeviceService deviceService;

    public List<Network> findAll() {
        return networkRepository.findAll();
    }

    public Network save(Network network) {
        return networkRepository.save(network);
    }

    public void delete(Long id) {
        networkRepository.deleteById(id);
    }

    public Network findById(Long id) {
        return networkRepository.findById(id).orElse(null);
    }
    public void updateNetworkStatus() {
        List<Network> networks = networkRepository.findAll();
        for (Network network : networks) {
            List<Device> devices = deviceService.findByNetwork(network);
            boolean hasInactiveDevice = devices.stream().anyMatch(device -> "Inactive".equals(device.getStatus()));
            if (hasInactiveDevice) {
                network.setStatus("Inactive");
            } else {
                network.setStatus("Active");
            }
            save(network);
        }
    }

}