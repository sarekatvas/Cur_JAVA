package com.example.demo;

import com.example.demo.Entity.Device;
import com.example.demo.Service.DeviceService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.*;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import java.util.List;
import java.util.stream.Collectors;

@Route("dashboard")
public class DashboardView extends VerticalLayout {

    private final DeviceService deviceService;

    public DashboardView(DeviceService deviceService) {
        this.deviceService = deviceService;
        initializeComponents();
    }

    private void initializeComponents() {
        H2 title = new H2("Network Device Dashboard");
        add(title);

        Chart devicesByTypeChart = createDevicesByTypeChart();
        Chart devicesByManufacturerChart = createDevicesByManufacturerChart();

        HorizontalLayout chartLayout = new HorizontalLayout(devicesByTypeChart, devicesByManufacturerChart);
        chartLayout.setSizeFull();
        add(chartLayout);
    }

    private Chart createDevicesByTypeChart() {
        Chart chart = new Chart(ChartType.PIE);
        Configuration configuration = chart.getConfiguration();
        configuration.setTitle("Devices by Type");

        DataSeries dataSeries = new DataSeries();
        List<Device> devices = deviceService.findAll();

        devices.stream()
                .collect(Collectors.groupingBy(Device::getType, Collectors.counting()))
                .forEach((type, count) -> {
                    DataSeriesItem item = new DataSeriesItem(type == null ? "Unknown" : type.getName(), count);
                    dataSeries.add(item);
                });

        configuration.setSeries(dataSeries);

        PlotOptionsPie plotOptions = new PlotOptionsPie();
        plotOptions.setAllowPointSelect(true);
       // plotOptions.setCursor(Cursor.valueOf("pointer"));
        plotOptions.setShowInLegend(true);
        //plotOptions.setDataLabels(new Labels(true));

        configuration.setPlotOptions(plotOptions);

        Legend legend = new Legend();
        legend.setEnabled(true);
        configuration.setLegend(legend);

        return chart;
    }

    private Chart createDevicesByManufacturerChart() {
        Chart chart = new Chart(ChartType.BAR);
        Configuration configuration = chart.getConfiguration();
        configuration.setTitle("Devices by Manufacturer");

        DataSeries dataSeries = new DataSeries();
        List<Device> devices = deviceService.findAll();

        devices.stream()
                .collect(Collectors.groupingBy(device -> device.getManufacturer() == null ? "Unknown" : device.getManufacturer().getName(), Collectors.counting()))
                .forEach((manufacturer, count) -> {
                    DataSeriesItem item = new DataSeriesItem(manufacturer, count);
                    dataSeries.add(item);
                });

        configuration.setSeries(dataSeries);

        PlotOptionsPie plotOptions = new PlotOptionsPie();
        plotOptions.setAllowPointSelect(true);
        //plotOptions.setCursor(Cursor.valueOf("pointer"));
        plotOptions.setShowInLegend(true);
        //plotOptions.setDataLabels(new Labels(true));

        configuration.setPlotOptions(plotOptions);

        Legend legend = new Legend();
        legend.setEnabled(true);
        configuration.setLegend(legend);

        return chart;
    }
}