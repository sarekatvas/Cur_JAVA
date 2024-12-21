package com.example.demo;

import com.example.demo.Entity.*;
import com.example.demo.Service.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.*;
import java.util.Optional;
import java.util.stream.Collectors;

@Route("main")
public class MainView extends VerticalLayout {
    private final DeviceService deviceService;
    private final TypeService typeService;
    private final ManufacturerService manufacturerService;
    private final NetworkService networkService;
    private final LocationService locationService;
    private final Grid<Device> grid = new Grid<>(Device.class);
    private final TextField name = new TextField("Name");
    private final ComboBox<Type> typeComboBox = new ComboBox<>("Type");
    private final TextField manufacturerName = new TextField("Manufacturer");
    private final TextField locationName = new TextField("Location");
    private final TextField networkName = new TextField("Network");
    private final TextField searchField = new TextField();
    private final Button addButton = new Button("Add");


    private final Button deleteButton = new Button("Delete");
    private  VerticalLayout mainLayout;
    private Menu menu = new Menu();

    @Autowired
    public MainView(DeviceService deviceService, TypeService typeService, ManufacturerService manufacturerService,
                       NetworkService networkService, LocationService locationService) {
        this.deviceService = deviceService;
        this.typeService = typeService;
        this.manufacturerService = manufacturerService;
        this.networkService = networkService;
        this.locationService = locationService;
        configureGrid();
        configureForm();
        configureMainLayout();
        configureSearch();
        add (menu, mainLayout);
        updateGrid();
    }
    private HorizontalLayout createMenu() {
        HorizontalLayout menu = new HorizontalLayout();
        Button dashboardButton = new Button("Dashboard", event -> getUI().ifPresent(ui -> ui.navigate("dashboard")));
        //menu.add(dashboardButton);
        return menu;
    }
    private void configureGrid() {
        grid.setColumns("id", "name");
        grid.addColumn(device -> Optional.ofNullable(device.getType()).map(Type::getName).orElse("")).setHeader("Type");
        grid.addColumn(device -> Optional.ofNullable(device.getManufacturer()).map(Manufacturer::getName).orElse("")).setHeader("Manufacturer");
        grid.addColumn(device -> Optional.ofNullable(device.getNetwork()).map(Network::getName).orElse("")).setHeader("Network");
        grid.addColumn(device -> Optional.ofNullable(device.getLocation()).map(Location::getName).orElse("")).setHeader("Location");
        grid.addItemClickListener(event -> {
            Device device = event.getItem();
            if (device != null) {
                name.setValue(device.getName());
                typeComboBox.setValue(device.getType());
                manufacturerName.setValue(device.getManufacturer().getName());
                networkName.setValue(device.getNetwork().getName());
                locationName.setValue(device.getLocation().getName());
            }
        });
    }

    private void configureMainLayout (){
        mainLayout = new VerticalLayout();
        HorizontalLayout buttonLayout = new HorizontalLayout();
        Button newButton = new Button("New", event -> createDeviceDialog());
        buttonLayout.add(newButton,deleteButton, searchField);
        mainLayout.add(grid, buttonLayout);
        mainLayout.setFlexGrow(1, grid);
    }

    private void createDeviceDialog(){
        Dialog dialog = new Dialog();
        VerticalLayout form = new VerticalLayout(name, typeComboBox, manufacturerName, locationName,
                networkName, addButton);
        typeComboBox.setItemLabelGenerator(Type::getName);
        typeComboBox.setItems(typeService.findAll());
        addButton.addClickListener(e -> {
            Device device = new Device();
            device.setName(name.getValue());
            device.setType(typeComboBox.getValue());
            device.setManufacturer(findOrCreateManufacturer(manufacturerName.getValue()));
            device.setNetwork(findOrCreateNetwork(networkName.getValue()));
            device.setLocation(findOrCreateLocation(locationName.getValue()));
            deviceService.save(device);
            updateGrid();
            name.clear();
            typeComboBox.clear();
            manufacturerName.clear();
            networkName.clear();
            locationName.clear();
        });


        dialog.add(form);
        dialog.open();
    }

    private void configureForm() {
        deleteButton.addClickListener(e -> {
            Device device = grid.asSingleSelect().getValue();
            if (device != null) {
                deviceService.delete(device.getId());
                updateGrid();
            }
        });

    }

    private void configureSearch(){
        searchField.setPlaceholder("Search by name");
        searchField.addValueChangeListener(event->updateGrid());
    }

    private void updateGrid() {
        String searchString = searchField.getValue().trim().toLowerCase();
        grid.setItems(deviceService.findAll().stream()
                .filter(device -> device.getName().toLowerCase().contains(searchString))
                .collect(Collectors.toList()));
        typeComboBox.setItems(typeService.findAll());
    }

    private Manufacturer findOrCreateManufacturer(String name) {
        return manufacturerService.findAll().stream()
                .filter(manufacturer -> manufacturer.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseGet(() -> {
                    Manufacturer manufacturer = new Manufacturer();
                    manufacturer.setName(name);
                    return manufacturerService.save(manufacturer);
                });
    }

    private Network findOrCreateNetwork(String name) {
        return networkService.findAll().stream()
                .filter(network -> network.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseGet(() -> {
                    Network network = new Network();
                    network.setName(name);
                    return networkService.save(network);
                });
    }

    private Location findOrCreateLocation(String name) {
        return locationService.findAll().stream()
                .filter(location -> location.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseGet(() -> {
                    Location location = new Location();
                    location.setName(name);
                    return locationService.save(location);
                });
    }
}