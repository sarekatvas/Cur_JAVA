package com.example.demo.Repositories;

import com.example.demo.Entity.Device;
import com.example.demo.Entity.Network;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeviceRepository extends JpaRepository<Device, Long> {
    List<Device> findByNetwork(Network network);
}