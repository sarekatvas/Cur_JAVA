package com.example.demo;

import com.example.demo.Entity.Device;
import com.example.demo.Entity.Location;
import com.example.demo.Entity.Type;
import com.example.demo.Service.DeviceService;
import com.example.demo.Service.LocationService;
import com.example.demo.Service.TypeService;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.*;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Route("dashboard")
public class DashboardView extends VerticalLayout {

    private final DeviceService deviceService;
    private final LocationService locationService;
    private final TypeService typeService;

    public DashboardView(DeviceService deviceService, LocationService locationService, TypeService typeService) {
        this.deviceService = deviceService;
        this.locationService = locationService;
        this.typeService = typeService;
        initializeComponents();
    }

    private void initializeComponents() {
        H2 title = new H2("Графики");
        add(title);

        Chart devicesByTypeChart = createDevicesByTypeChart();
        Chart devicesByManufacturerChart = createDevicesByManufacturerChart();
        Chart d = createHeatMap();
        HorizontalLayout chartLayout = new HorizontalLayout(devicesByTypeChart, devicesByManufacturerChart);
        HorizontalLayout chartLayout2 = new HorizontalLayout(d);
        VerticalLayout mainLayout = new VerticalLayout(chartLayout, chartLayout2);
        chartLayout.setSizeFull();
        add(mainLayout);
    }

    private Chart createDevicesByTypeChart() {
        Chart chart = new Chart(ChartType.PIE);
        Configuration configuration = chart.getConfiguration();
        configuration.setTitle("Устройства по типам");

        DataSeries dataSeries = new DataSeries();
        List<Device> devices = deviceService.findAll();

        devices.stream()
                .collect(Collectors.groupingBy(Device::getType, Collectors.counting()))
                .forEach((type, count) -> {
                    DataSeriesItem item = new DataSeriesItem(type == null ? "Неизвестный тип устройства" : type.getName(), count);
                    dataSeries.add(item);
                });

        configuration.setSeries(dataSeries);

        PlotOptionsPie plotOptions = new PlotOptionsPie();
        plotOptions.setAllowPointSelect(true);
        plotOptions.setShowInLegend(true);

        configuration.setPlotOptions(plotOptions);

        Legend legend = new Legend();
        legend.setEnabled(true);
        configuration.setLegend(legend);

        return chart;
    }

    private Chart createDevicesByManufacturerChart() {
        Chart chart = new Chart(ChartType.BAR);
        Configuration configuration = chart.getConfiguration();
        configuration.setTitle("Устройства по производителям");

        DataSeries dataSeries = new DataSeries();
        List<Device> devices = deviceService.findAll();

        devices.stream()
                .collect(Collectors.groupingBy(Device::getManufacturer, Collectors.counting()))
                .forEach((manufacturer, count) -> {
                    DataSeriesItem item = new DataSeriesItem(manufacturer == null ? "Неизвестный производитель" : manufacturer.getName(), count);
                    dataSeries.add(item);
                });

        configuration.setSeries(dataSeries);

        PlotOptionsBar plotOptions = new PlotOptionsBar();
        plotOptions.setAllowPointSelect(true);
        plotOptions.setShowInLegend(true);

        configuration.setPlotOptions(plotOptions);

        // Настройка оси X
        XAxis xAxis = new XAxis();
        xAxis.setTitle("Производитель");
        String[] categories = dataSeries.getData().stream()
                .map(DataSeriesItem::getName)
                .toArray(String[]::new);
        xAxis.setCategories(categories);
        configuration.addxAxis(xAxis);

        // Настройка оси Y
        YAxis yAxis = new YAxis();
        yAxis.setTitle("Количество устройств");
        configuration.addyAxis(yAxis);

        return chart;
    }

    private Chart createHeatMap() {
        Chart chart = new Chart(ChartType.HEATMAP);
        chart.setWidth("100%");
        chart.setHeight("400px");

        Configuration configuration = chart.getConfiguration();
        configuration.setTitle("Device Density by Location and Type");

        // Настройка оси X
        XAxis xAxis = new XAxis();
        List<String> locations = locationService.findAll().stream().map(Location::getName).collect(Collectors.toList());
        xAxis.setCategories(locations.toArray(new String[0]));
        configuration.addxAxis(xAxis);

        // Настройка оси Y
        YAxis yAxis = new YAxis();
        List<String> types = typeService.findAll().stream().map(Type::getName).collect(Collectors.toList());
        yAxis.setCategories(types.toArray(new String[0]));
        configuration.addyAxis(yAxis);

        // Создаем серию данных
        DataSeries series = new DataSeries("Device Density");

        // Пример данных
        List<Device> devices = deviceService.findAll();
        Map<String, Map<String, Long>> data = devices.stream()
                .collect(Collectors.groupingBy(device -> device.getLocation().getName(),
                        Collectors.groupingBy(device -> device.getType().getName(), Collectors.counting())));

        data.forEach((location, typeMap) -> {
            typeMap.forEach((type, count) -> {
                int x = locations.indexOf(location);
                int y = types.indexOf(type);
                series.add(new DataSeriesItem(x, y, count));
            });
        });

        configuration.addSeries(series);

        return chart;
    }
}