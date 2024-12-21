package com.example.demo.Service;

import com.example.demo.Entity.Network;
import com.example.demo.Repositories.NetworkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NetworkService {
    @Autowired
    private NetworkRepository networkRepository;

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
}