package com.example.demo;

import com.example.demo.Entity.Location;
import com.example.demo.Service.LocationService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Route("locations")
public class LocationsView extends VerticalLayout {
    private final LocationService locationService;
    private Grid<Location> grid1 = new Grid<>(Location.class);
    private TextField name = new TextField("Name");
    private Button addButton = new Button("Add");
    private Button deleteButton = new Button("Delete");
    HorizontalLayout form = new HorizontalLayout();

    @Autowired
    public LocationsView(LocationService locationService) {
        this.locationService = locationService;
        configureGrid();
        configureForm();
        add(new Menu(), grid1, form);
        updateGrid();
    }

    private void configureGrid() {
        grid1.setColumns("id", "name");
        grid1.addItemClickListener(event -> {
            Location location = event.getItem();
            if (location != null) {
                name.setValue(location.getName());
            }
        });
    }

    private void configureForm() {
        form = new HorizontalLayout(name, addButton, deleteButton);
        addButton.addClickListener(e -> {
            Location location = new Location();
            location.setName(name.getValue());
            locationService.save(location);
            updateGrid();
            name.clear();
        });
        deleteButton.addClickListener(e -> {
            Location location = grid1.asSingleSelect().getValue();
            if (location != null) {
                locationService.delete(location.getId());
                updateGrid();
            }
        });
    }

    private void updateGrid() {
        grid1.setItems(locationService.findAll());
    }
}