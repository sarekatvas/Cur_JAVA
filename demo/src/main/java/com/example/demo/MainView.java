package com.example.demo;

import com.example.demo.Entity.*;
import com.example.demo.Service.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.stream.Collectors;

@Route("main")
public class MainView extends VerticalLayout {
    private final DeviceService deviceService;
    private final TypeService typeService;
    private final ManufacturerService manufacturerService;
    private final NetworkService networkService;
    private final LocationService locationService;
    private final H5 label = new H5("Заполните поля для добавления устройства");
    private final H2 mainLabel = new H2("Таблица 'Устройства'");
    private final Grid<Device> grid = new Grid<>(Device.class);
    private final TextField name = new TextField("Наименование");
    private final ComboBox<Type> typeComboBox = new ComboBox<>("Тип устройства");
    private final TextField manufacturerName = new TextField("Производитель");
    private final ComboBox<Location> locationComboBox = new ComboBox<>("Месторасположение");
    private final ComboBox<Network> networkComboBox = new ComboBox<>("Сеть");
    private final TextField statusField = new TextField("Статус");
    private final TextField priceField = new TextField("Цена");
    private final TextField searchField = new TextField();
    private final Button addButton = new Button("Добавить");
    private final Button deleteButton = new Button("Удалить");
    private final Button updateButton = new Button("Изменить");
    private final Button updateDialog = new Button("Изменить");
    private VerticalLayout mainLayout;
    private HorizontalLayout headerLayout;

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
        headerLayout = new HorizontalLayout(mainLabel);
        headerLayout.setWidth("100%");
        headerLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        Menu menu = new Menu();
        add(menu, headerLayout, mainLayout);
        updateGrid();
    }

    private void configureGrid() {
        grid.setColumns("id");
        grid.addColumn(device -> Optional.ofNullable(device.getName()).orElse("")).setHeader("Наименование");
        grid.addColumn(device -> Optional.ofNullable(device.getType()).map(Type::getName).orElse("")).setHeader("Тип устройства").setSortable(true);
        grid.addColumn(device -> Optional.ofNullable(device.getManufacturer()).map(Manufacturer::getName).orElse("")).setHeader("Производитель").setSortable(true);
        grid.addColumn(device -> Optional.ofNullable(device.getNetwork()).map(Network::getName).orElse("")).setHeader("Сеть").setSortable(true);
        grid.addColumn(device -> Optional.ofNullable(device.getNetwork()).map(Network::getLocation)).setHeader("Месторасположение").setSortable(true);
        grid.addColumn(device -> Optional.ofNullable(device.getStatus()).orElse("Inactive")).setHeader("Статус").setSortable(true);
        grid.addColumn(device -> Optional.ofNullable(device.getPrice()).orElse(BigDecimal.ZERO)).setHeader("Цена").setSortable(true);
        grid.addItemClickListener(event -> {
            Device device = event.getItem();
            if (device != null) {
                name.setValue(device.getName());
                typeComboBox.setValue(device.getType());
                manufacturerName.setValue(device.getManufacturer().getName());
                locationComboBox.setValue(device.getNetwork().getLocation());
                networkComboBox.setValue(device.getNetwork());
                statusField.setValue(device.getStatus());
                priceField.setValue(device.getPrice().toString());
                if (device.getStatus() != null) {
                    device.setStatus(device.getStatus());
                }
            }
        });

    }

    private void configureMainLayout() {
        mainLayout = new VerticalLayout();
        HorizontalLayout buttonLayout = new HorizontalLayout();
        Button newButton = new Button("Добавить", event -> createDeviceDialog());
        buttonLayout.add(newButton, deleteButton, searchField, updateDialog);
        mainLayout.add(grid, buttonLayout);
        mainLayout.setFlexGrow(1, grid);
    }

    private void createDeviceDialog() {
        Dialog dialog = new Dialog();
        VerticalLayout form = new VerticalLayout(label, name, typeComboBox, manufacturerName, locationComboBox,
                networkComboBox, statusField, priceField, addButton);
        typeComboBox.setItemLabelGenerator(Type::getName);
        typeComboBox.setItems(typeService.findAll());
        locationComboBox.setItemLabelGenerator(Location::getName);
        locationComboBox.setItems(locationService.findAll());
        networkComboBox.setItemLabelGenerator(Network::getName);
        networkComboBox.setItems(networkService.findAll());
        addButton.addClickListener(e -> {
            Device device = new Device();
            device.setName(name.getValue());
            device.setType(typeComboBox.getValue());
            device.setManufacturer(findOrCreateManufacturer(manufacturerName.getValue()));
            device.setNetwork(networkComboBox.getValue());
            device.setLocation(locationComboBox.getValue());
            device.setStatus(statusField.getValue());
            try {
                device.setPrice(new BigDecimal(priceField.getValue()));
            } catch (NumberFormatException ex) {
                priceField.setInvalid(true);
                priceField.setErrorMessage("Введите число");
                return;
            }
            deviceService.save(device);
            updateGrid();
            name.clear();
            typeComboBox.clear();
            manufacturerName.clear();
            networkComboBox.clear();
            locationComboBox.clear();
            statusField.clear();
            priceField.clear();
            dialog.close();
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

        updateDialog.addClickListener(e -> {
            Device device = grid.asSingleSelect().getValue();
            typeComboBox.setItemLabelGenerator(Type::getName);
            typeComboBox.setItems(typeService.findAll());
            locationComboBox.setItemLabelGenerator(Location::getName);
            locationComboBox.setItems(locationService.findAll());
            networkComboBox.setItemLabelGenerator(Network::getName);
            networkComboBox.setItems(networkService.findAll());
            if (device != null) {
                name.setValue(device.getName());
                typeComboBox.setValue(device.getType());
                manufacturerName.setValue(device.getManufacturer().getName());
                networkComboBox.setValue(device.getNetwork());
                locationComboBox.setValue(device.getLocation());
                statusField.setValue(device.getStatus());
                priceField.setValue(device.getPrice().toString());
                updateDeviceDialog(device);
            }
        });
    }

    private void updateDeviceDialog(Device device) {
        Dialog dialog = new Dialog();
        VerticalLayout form = new VerticalLayout(label, name, typeComboBox, manufacturerName, networkComboBox,
                locationComboBox, statusField, priceField, updateButton);
        updateButton.addClickListener(e -> {
            device.setName(name.getValue());
            device.setType(typeComboBox.getValue());
            device.setManufacturer(findOrCreateManufacturer(manufacturerName.getValue()));
            device.setNetwork(networkComboBox.getValue());
            device.setLocation(locationComboBox.getValue());
            device.setStatus(statusField.getValue());
            try {
                device.setPrice(new BigDecimal(priceField.getValue()));
            } catch (NumberFormatException ex) {
                priceField.setInvalid(true);
                priceField.setErrorMessage("Введите число");
                return;
            }
            deviceService.save(device);
            updateGrid();
            dialog.close();
        });

        dialog.add(form);
        dialog.open();
    }

    private void configureSearch() {
        //searchField.setPlaceholder("Search by name, type, manufacturer, location, network, status, or price");
        searchField.addValueChangeListener(event -> updateGrid());
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
    }

    private void updateGrid() {
        String searchString = searchField.getValue().trim().toLowerCase();
        grid.setItems(deviceService.findAll().stream()
                .filter(device -> device.getName().toLowerCase().contains(searchString) ||
                        (device.getType() != null && device.getType().getName().toLowerCase().contains(searchString)) ||
                        (device.getManufacturer() != null && device.getManufacturer().getName().toLowerCase().contains(searchString)) ||
                        (device.getLocation() != null && device.getLocation().getName().toLowerCase().contains(searchString)) ||
                        (device.getNetwork() != null && device.getNetwork().getName().toLowerCase().contains(searchString)) ||
                        (device.getStatus() != null && device.getStatus().toLowerCase().contains(searchString)) ||
                        (device.getPrice() != null && device.getPrice().toString().contains(searchString)))
                .collect(Collectors.toList()));
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
}