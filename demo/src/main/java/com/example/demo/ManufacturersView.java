package com.example.demo;

import com.example.demo.Entity.Manufacturer;
import com.example.demo.Service.ManufacturerService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Route("manufacturers")
public class ManufacturersView extends VerticalLayout {
    private final ManufacturerService manufacturerService;
    private Grid<Manufacturer> grid = new Grid<>(Manufacturer.class);
    private TextField name = new TextField("Name");
    private Button addButton = new Button("Add");
    private Button deleteButton = new Button("Delete");
    HorizontalLayout form;

    @Autowired
    public ManufacturersView(ManufacturerService manufacturerService) {
        this.manufacturerService = manufacturerService;
        configureGrid();
        configureForm();
        add(new Menu(), grid, form);
        updateGrid();
    }

    private void configureGrid() {
        grid.setColumns("id", "name");
        grid.addItemClickListener(event -> {
            Manufacturer manufacturer = event.getItem();
            if (manufacturer != null) {
                name.setValue(manufacturer.getName());
            }
        });
    }

    private void configureForm() {
        form = new HorizontalLayout(name, addButton, deleteButton);
        addButton.addClickListener(e -> {
            Manufacturer manufacturer = new Manufacturer();
            manufacturer.setName(name.getValue());
            manufacturerService.save(manufacturer);
            updateGrid();
            name.clear();
        });
        deleteButton.addClickListener(e -> {
            Manufacturer manufacturer = grid.asSingleSelect().getValue();
            if (manufacturer != null) {
                manufacturerService.delete(manufacturer.getId());
                updateGrid();
            }
        });
    }

    private void updateGrid() {
        grid.setItems(manufacturerService.findAll());
    }
}