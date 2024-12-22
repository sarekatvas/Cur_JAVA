package com.example.demo;

import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("menu")
public class Menu extends VerticalLayout {
    private static final int FontSize = 20;

    public Menu() {
        Anchor devicesLink = new Anchor("main", "Устройства");
        Anchor typesLink = new Anchor("types", "Типы устройств");
        Anchor manufacturersLink = new Anchor("manufacturers", "Производители");
        Anchor networksLink = new Anchor("networks", "Сети");
        Anchor locationsLink = new Anchor("locations", "Локации");
        Anchor dashboardLink = new Anchor("dashboard", "Графики");

        devicesLink.getStyle().setFontSize(FontSize + "px");
        typesLink.getStyle().setFontSize(FontSize + "px");
        manufacturersLink.getStyle().setFontSize(FontSize + "px");
        networksLink.getStyle().setFontSize(FontSize + "px");
        locationsLink.getStyle().setFontSize(FontSize + "px");
        dashboardLink.getStyle().setFontSize(FontSize + "px");

        devicesLink.getStyle().set("margin", "0 20px");
        typesLink.getStyle().set("margin", "0 20px");
        manufacturersLink.getStyle().set("margin", "0 20px");
        networksLink.getStyle().set("margin", "0 20px");
        locationsLink.getStyle().set("margin", "0 20px");
        dashboardLink.getStyle().set("margin", "0 20px");

        HorizontalLayout menuLayout = new HorizontalLayout(devicesLink, typesLink, manufacturersLink, networksLink, locationsLink, dashboardLink);
        menuLayout.setWidth("100%");
        menuLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        add(menuLayout);
    }
}