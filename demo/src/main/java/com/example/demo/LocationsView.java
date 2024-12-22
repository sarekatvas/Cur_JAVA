package com.example.demo;

import com.example.demo.Entity.Device;
import com.example.demo.Entity.Location;
import com.example.demo.Entity.Network;
import com.example.demo.Entity.Type;
import com.example.demo.Service.LocationService;
import com.vaadin.flow.component.button.Button;
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

import java.awt.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Route("locations")
public class LocationsView extends VerticalLayout {
    private final LocationService locationService;
    private final H2 mainLabel = new H2("Таблица 'Локации'");
    private final H5 label= new H5("Заполните поля для добавления локации");
    private final Grid<Location> grid1 = new Grid<>(Location.class);
    private final TextField name = new TextField("Локация");
    private final TextField streetField = new TextField("Улица");
    private final TextField buildingField = new TextField("Строение");
    private final Button addButton = new Button("Добавить");
    private final Button updateButton = new Button("Изменить");
    private final Button updateDialog = new Button("Изменить");
    private final Button deleteButton = new Button("Удалить");
    VerticalLayout mainLayout;
    private HorizontalLayout headerLayout;

    @Autowired
    public LocationsView(LocationService locationService) {
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
    }

    private void configureGrid() {
        grid1.setColumns("id", "name");
        grid1.addColumn(location -> Optional.ofNullable(location.getStreet()).orElse("")).setHeader("Улица");
        grid1.addColumn(location -> Optional.ofNullable(location.getBuilding()).orElse("")).setHeader("Строение");
        grid1.addItemClickListener(event -> {
            Location location = event.getItem();
            if (location != null) {
                name.setValue(location.getName());
                streetField.setValue(location.getStreet());
                buildingField.setValue(location.getBuilding());
            }
        });
    }

    private void configureMainLayout (){
        mainLayout = new VerticalLayout();
        HorizontalLayout buttonLayout = new HorizontalLayout();
        Button newButton = new Button("Добавить", event -> createLocationDialog());
        buttonLayout.add(newButton,deleteButton, updateDialog);
        mainLayout.add(grid1, buttonLayout);
        mainLayout.setFlexGrow(1, grid1);
    }
    private void createLocationDialog(){
        Dialog dialog = new Dialog();
        VerticalLayout form = new VerticalLayout(label,name,streetField, buildingField, addButton);
        addButton.addClickListener(e -> {
            Location location = new Location();
            location.setName(name.getValue());
            location.setStreet(streetField.getValue());
            location.setBuilding(buildingField.getValue());
            locationService.save(location);
            updateGrid();
            name.clear();
            streetField.clear();
            buildingField.clear();
            dialog.close();
        });

        dialog.add(form);
        dialog.open();
    }
    private void configureForm() {
        deleteButton.addClickListener(e -> {
            Location location = grid1.asSingleSelect().getValue();
            if (location != null) {
                locationService.delete(location.getId());
                updateGrid();
            }
        });

        updateDialog.addClickListener(e->{
            Location location = grid1.asSingleSelect().getValue();
            if (location != null) {
                name.setValue(location.getName());
                streetField.setValue(location.getStreet());
                buildingField.setValue(location.getBuilding());
                updateLocationDialog(location);
            }
        });
    }

    private void updateLocationDialog(Location location){
        Dialog dialog = new Dialog();
        VerticalLayout form = new VerticalLayout(label,name,streetField, buildingField,updateButton);
        updateButton.addClickListener(e -> {
            location.setName(name.getValue());
            location.setStreet(streetField.getValue());
            location.setBuilding(buildingField.getValue());
            locationService.save(location);
            updateGrid();
            dialog.close();
        });

        dialog.add(form);
        dialog.open();
    }
    private void updateGrid() {
        grid1.setItems(locationService.findAll());
    }
}