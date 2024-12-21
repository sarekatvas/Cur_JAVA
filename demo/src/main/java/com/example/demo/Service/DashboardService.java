package com.example.demo.Service;

import com.example.demo.Entity.Device;
import com.example.demo.Entity.Manufacturer;
import com.example.demo.Repositories.DeviceRepository;
import com.example.demo.Repositories.ManufacturerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardService {
    @Autowired
    private DeviceRepository deviceRepository;
    @Autowired
    private ManufacturerRepository manufacturerRepository;

    public Map<Manufacturer, Long> getDeviceCountByManufacturer() {
        List<Device> devices = deviceRepository.findAll();
        return devices.stream()
                .collect(Collectors.groupingBy(Device::getManufacturer, Collectors.counting()));
    }
}