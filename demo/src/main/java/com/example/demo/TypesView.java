package com.example.demo;

import com.example.demo.Entity.Location;
import com.example.demo.Entity.Network;
import com.example.demo.Entity.Type;
import com.example.demo.Service.TypeService;
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

@Route("types")
public class TypesView extends VerticalLayout {
    private final TypeService typeService;
    private final H5 label= new H5("Заполните поля для добавления типа устройств");
    private final H2 mainLabel = new H2("Таблица 'Типы устройств'");
    private final Grid<Type> grid = new Grid<>(Type.class);
    private final TextField name = new TextField("Тип устройства");
    private final Button addButton = new Button("Добавить");
    private final Button updateButton = new Button("Изменить");
    private final Button updateDialog = new Button("Изменить");
    private final Button deleteButton = new Button("Удалить");
    private VerticalLayout mainLayout;
    private HorizontalLayout headerLayout;

    @Autowired
    public TypesView(TypeService typeService) {
        this.typeService = typeService;
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
            Type type = event.getItem();
            if (type != null) {
                name.setValue(type.getName());
            }
        });
    }

    private void createNetworkDialog(){
        Dialog dialog = new Dialog();
        VerticalLayout form = new VerticalLayout(label, name, addButton);
        addButton.addClickListener(e -> {
            Type type = new Type();
            type.setName(name.getValue());
            typeService.save(type);
            updateGrid();
            name.clear();
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
            Type type = grid.asSingleSelect().getValue();
            if (type != null) {
                typeService.delete(type.getId());
                updateGrid();
            }
        });

        updateDialog.addClickListener(e->{
            Type type = grid.asSingleSelect().getValue();
            if (type != null) {
                name.setValue(type.getName());
                updateTypeDialog(type);
            }
        });
    }

    private void updateTypeDialog(Type type){
        Dialog dialog = new Dialog();
        VerticalLayout form = new VerticalLayout(label,name,updateButton);
        updateButton.addClickListener(e -> {
            type.setName(name.getValue());
            typeService.save(type);
            updateGrid();
            dialog.close();
        });

        dialog.add(form);
        dialog.open();
    }

    private void updateGrid() {
        grid.setItems(typeService.findAll());
    }
}