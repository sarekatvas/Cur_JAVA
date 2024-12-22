package com.example.demo;

import com.example.demo.Entity.Location;
import com.example.demo.Entity.Manufacturer;
import com.example.demo.Entity.Network;
import com.example.demo.Service.ManufacturerService;
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

@Route("manufacturers")
public class ManufacturersView extends VerticalLayout {
    private final ManufacturerService manufacturerService;
    private final H5 label= new H5("Заполните поля для добавления производителя");
    private final H2 mainLabel = new H2("Таблица 'Производители'");
    private final Grid<Manufacturer> grid = new Grid<>(Manufacturer.class);
    private final TextField name = new TextField("Производитель");
    private final Button addButton = new Button("Добавить");
    private final Button updateButton = new Button("Изменить");
    private final Button updateDialog = new Button("Изменить");
    private final Button deleteButton = new Button("Удалить");
    private VerticalLayout mainLayout ;
    private HorizontalLayout headerLayout;

    @Autowired
    public ManufacturersView(ManufacturerService manufacturerService) {
        this.manufacturerService = manufacturerService;
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
        grid.setColumns("id", "name");
        grid.addItemClickListener(event -> {
            Manufacturer manufacturer = event.getItem();
            if (manufacturer != null) {
                name.setValue(manufacturer.getName());
            }
        });
    }
    private void createManufacturerDialog(){
        Dialog dialog = new Dialog();
        VerticalLayout form = new VerticalLayout(label, name, addButton);
        addButton.addClickListener(e -> {
            Manufacturer manufacturer = new Manufacturer();
            manufacturer.setName(name.getValue());
            manufacturerService.save(manufacturer);
            updateGrid();
            name.clear();
        });
        dialog.add(form);
        dialog.open();
    }
    private void configureMainLayout (){
        mainLayout = new VerticalLayout();
        HorizontalLayout buttonLayout = new HorizontalLayout();
        Button newButton = new Button("Добавить", event -> createManufacturerDialog());
        buttonLayout.add(newButton,deleteButton, updateDialog);
        mainLayout.add(grid, buttonLayout);
        mainLayout.setFlexGrow(1, grid);
    }
    private void configureForm() {
        deleteButton.addClickListener(e -> {
            Manufacturer manufacturer = grid.asSingleSelect().getValue();
            if (manufacturer != null) {
                manufacturerService.delete(manufacturer.getId());
                updateGrid();
            }
        });

        updateDialog.addClickListener(e->{
            Manufacturer manufacturer = grid.asSingleSelect().getValue();
            if (manufacturer != null) {
                name.setValue(manufacturer.getName());
                updateManufacturerDialog(manufacturer);
            }
        });
    }

    private void updateManufacturerDialog(Manufacturer manufacturer){
        Dialog dialog = new Dialog();
        VerticalLayout form = new VerticalLayout(label,name,updateButton);
        updateButton.addClickListener(e -> {
            manufacturer.setName(name.getValue());
            manufacturerService.save(manufacturer);
            updateGrid();
            dialog.close();
        });

        dialog.add(form);
        dialog.open();
    }
    private void updateGrid() {
        grid.setItems(manufacturerService.findAll());
    }
}