package com.example.demo;

import com.example.demo.Entity.Device;
import com.example.demo.Entity.Location;
import com.example.demo.Entity.Network;
import com.example.demo.Entity.Type;
import com.example.demo.Service.LocationService;
import com.example.demo.Service.NetworkService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Route("networks")
public class NetworksView extends VerticalLayout {
    private final NetworkService networkService;
    private final LocationService locationService;
    private final H5 label= new H5("Заполните поля для добавления сети");
    private final H2 mainLabel = new H2("Таблица 'Сети'");
    private final Grid<Network> grid = new Grid<>(Network.class);
    private final TextField name = new TextField("Наименование сети");
    private final TextField statusField = new TextField("Статус");
    private final ComboBox<Location> locationComboBox = new ComboBox<>("Месторасположение");
    private final Button addButton = new Button("Добавить");
    private final Button updateButton = new Button("Изменить");
    private final Button updateDialog = new Button("Изменить");
    private final Button deleteButton = new Button("Удалить");
    private VerticalLayout mainLayout ;
    private HorizontalLayout headerLayout;

    @Autowired
    public NetworksView(NetworkService networkService, LocationService locationService) {
        this.networkService = networkService;
        this.locationService = locationService;
        configureGrid();
        configureForm();
        configureMainLayout();
        headerLayout = new HorizontalLayout(mainLabel);
        headerLayout.setWidth("100%");
        headerLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        Menu menu = new Menu();
        add (menu, headerLayout, mainLayout);
        updateGrid();
        networkService.updateNetworkStatus();
    }

    private void configureGrid() {
        grid.setColumns("id", "name");
        grid.addColumn(network -> Optional.ofNullable(network.getLocation()).map(Location::getName).orElse("")).setHeader("Месторасположение");
        grid.addColumn(Network::getStatus).setHeader("Статус");
        grid.addItemClickListener(event -> {
            Network network = event.getItem();
            if (network != null) {
                name.setValue(network.getName());
                locationComboBox.setValue(network.getLocation());
                statusField.setValue(network.getStatus());
            }
        });
    }

    private void createNetworkDialog(){
        Dialog dialog = new Dialog();
        VerticalLayout form = new VerticalLayout(label,name, locationComboBox, statusField, addButton);
        locationComboBox.setItemLabelGenerator(Location::getName);
        locationComboBox.setItems(locationService.findAll());
        addButton.addClickListener(e -> {
            Network network = new Network();
            network.setName(name.getValue());
            network.setLocation(locationComboBox.getValue());
            network.setStatus(statusField.getValue());
            networkService.save(network);
            networkService.updateNetworkStatus();
            updateGrid();
            name.clear();
            locationComboBox.clear();
            statusField.clear();
            dialog.close();
        });
        dialog.add(form);
        dialog.open();
    }
    private void configureMainLayout (){
        mainLayout = new VerticalLayout();
        HorizontalLayout buttonLayout = new HorizontalLayout();
        Button newButton = new Button("Добавить", event -> createNetworkDialog());
        buttonLayout.add(newButton,deleteButton, updateDialog);
        mainLayout.add(grid, buttonLayout);
        mainLayout.setFlexGrow(1, grid);
    }
    private void configureForm() {
        deleteButton.addClickListener(e -> {
            Network network = grid.asSingleSelect().getValue();
            if (network != null) {
                networkService.delete(network.getId());
                List<Network> networks = networkService.findAll();
                networkService.updateNetworkStatus();
                updateGrid();
            }
        });

        updateDialog.addClickListener(e->{
            Network network = grid.asSingleSelect().getValue();
            if (network != null) {
                name.setValue(network.getName());
                statusField.setValue(network.getStatus());
                locationComboBox.setValue(network.getLocation());
                updateNetworkDialog(network);
            }
        });
    }

    private void updateNetworkDialog(Network network){
        Dialog dialog = new Dialog();
        VerticalLayout form = new VerticalLayout(label,name,locationComboBox, statusField, updateButton);
        updateButton.addClickListener(e -> {
            network.setName(name.getValue());
            network.setStatus(statusField.getValue());
            network.setLocation(locationComboBox.getValue());
            networkService.save(network);
            updateGrid();
            dialog.close();
        });

        dialog.add(form);
        dialog.open();
    }

    private void updateGrid() {
        grid.setItems(networkService.findAll());
        locationComboBox.setItems(locationService.findAll());
    }
}