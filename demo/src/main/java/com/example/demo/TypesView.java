package com.example.demo;

import com.example.demo.Entity.Type;
import com.example.demo.Service.TypeService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Route("types")
public class TypesView extends VerticalLayout {
    private final TypeService typeService;
    private Grid<Type> grid = new Grid<>(Type.class);
    private TextField name = new TextField("Name");
    private Button addButton = new Button("Add");
    private Button deleteButton = new Button("Delete");
    private HorizontalLayout form;

    @Autowired
    public TypesView(TypeService typeService) {
        this.typeService = typeService;
        configureGrid();
        configureForm();
        add(new Menu(), grid, form);
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

    private void configureForm() {
        form = new HorizontalLayout(name, addButton, deleteButton);
        addButton.addClickListener(e -> {
            Type type = new Type();
            type.setName(name.getValue());
            typeService.save(type);
            updateGrid();
            name.clear();
        });
        deleteButton.addClickListener(e -> {
            Type type = grid.asSingleSelect().getValue();
            if (type != null) {
                typeService.delete(type.getId());
                updateGrid();
            }
        });
    }

    private void updateGrid() {
        grid.setItems(typeService.findAll());
    }
}