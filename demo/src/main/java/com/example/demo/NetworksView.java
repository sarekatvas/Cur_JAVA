package com.example.demo;

import com.example.demo.Entity.Location;
import com.example.demo.Entity.Network;
import com.example.demo.Service.LocationService;
import com.example.demo.Service.NetworkService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

@Route("networks")
public class NetworksView extends VerticalLayout {
    private final NetworkService networkService;
    private final LocationService locationService;
    private Grid<Network> grid = new Grid<>(Network.class);
    private TextField name = new TextField("Name");
    private ComboBox<Location> locationComboBox = new ComboBox<>("Location");
    private Button addButton = new Button("Add");
    private Button deleteButton = new Button("Delete");
    HorizontalLayout form = new HorizontalLayout();

    @Autowired
    public NetworksView(NetworkService networkService, LocationService locationService) {
        this.networkService = networkService;
        this.locationService = locationService;
        configureGrid();
        configureForm();
        add(new Menu(), grid, form);
        updateGrid();
    }

    private void configureGrid() {
        grid.setColumns("id", "name");
        grid.addColumn(network -> Optional.ofNullable(network.getLocation()).map(Location::getName).orElse("")).setHeader("Location");
        grid.addItemClickListener(event -> {
            Network network = event.getItem();
            if (network != null) {
                name.setValue(network.getName());
                locationComboBox.setValue(network.getLocation());
            }
        });
    }

    private void configureForm() {
        form = new HorizontalLayout(name, locationComboBox, addButton, deleteButton);
        locationComboBox.setItemLabelGenerator(Location::getName);
        locationComboBox.setItems(locationService.findAll());
        addButton.addClickListener(e -> {
            Network network = new Network();
            network.setName(name.getValue());
            network.setLocation(locationComboBox.getValue());
            networkService.save(network);
            updateGrid();
            name.clear();
            locationComboBox.clear();
        });
        deleteButton.addClickListener(e -> {
            Network network = grid.asSingleSelect().getValue();
            if (network != null) {
                networkService.delete(network.getId());
                updateGrid();
            }
        });
    }

    private void updateGrid() {
        grid.setItems(networkService.findAll());
        locationComboBox.setItems(locationService.findAll());
    }
}