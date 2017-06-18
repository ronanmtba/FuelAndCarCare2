package com.dornier.fuelcarcare;

import java.util.ArrayList;

/**
 * Created by Dornier on 16/03/2017.
 */

public class ModelVehicle {
    private long local_id;
    private String id;
    private String name;
    private String manufacturer;
    private String model;
    private String year;
    private String status;
    private ArrayList<ModelMaintenanceAlert> alerts;
    private ArrayList<ModelFillUp> fillUps;
    private ArrayList<ModelExpense> expenses;

    public ModelVehicle(String name) {
        this.local_id   = 0;
        this.id         = "";
        this.name       = name;
        this.manufacturer= "";
        this.model      = "";
        this.year       = "";
        this.status     = "0";
        this.alerts     = new ArrayList<ModelMaintenanceAlert>();
        this.fillUps    = new ArrayList<ModelFillUp>();
        this.expenses   = new ArrayList<ModelExpense>();
    }

    public ModelVehicle(long local_id, String id, String name, String manufacturer, String model, String year, String status) {
        this.local_id       = local_id;
        this.id             = id;
        this.name           = name;
        this.manufacturer   = manufacturer;
        this.model          = model;
        this.year           = year;
        this.status         = status;
        this.alerts         = new ArrayList<ModelMaintenanceAlert>();
        this.fillUps        = new ArrayList<ModelFillUp>();
        this.expenses       = new ArrayList<ModelExpense>();
    }

    public long getLocal_id() {
        return local_id;
    }

    public void setLocal_id(long local_id) {
        this.local_id = local_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public String toString(){
        return getName();
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public ArrayList<ModelMaintenanceAlert> getAlerts() {
        return alerts;
    }

    public ArrayList<ModelFillUp> getFillUps() {
        return fillUps;
    }

    public ArrayList<ModelExpense> getExpenses() {
        return expenses;
    }
}
