package com.dornier.fuelcarcare;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

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

    public void sortExpenses(){
        Collections.sort(expenses, new Comparator<ModelExpense>() {
            @Override
            public int compare(ModelExpense obj2, ModelExpense obj1)
            {
                Date a = obj1.getDate();
                Date b = obj2.getDate();
                if (a.before(b))
                    return 1;
                else if (a.after(b))
                    return -1;
                else
                    return 0;
            }
        });
    }

    public void sortfillUps(){
        Collections.sort(fillUps, new Comparator<ModelFillUp>() {
            @Override
            public int compare(ModelFillUp obj2, ModelFillUp obj1)
            {
                int a = obj1.getKilometers();
                int b = obj2.getKilometers();
                if (a < b)
                    return 1;
                else if (a > b)
                    return -1;
                else
                    return 0;
            }
        });
    }

    public void sortAlerts(){
        Collections.sort(alerts, new Comparator<ModelMaintenanceAlert>() {
            @Override
            public int compare(ModelMaintenanceAlert obj2, ModelMaintenanceAlert obj1)
            {
                Date a = obj1.getMaintenance_date();
                Date b = obj2.getMaintenance_date();
                if (a.before(b))
                    return 1;
                else if (a.after(b))
                    return -1;
                else
                    return 0;
            }
        });
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

    public ArrayList<ModelFillUp> getFilteredFillUps() {
        final ArrayList<ModelFillUp> toReturn = new ArrayList<ModelFillUp>();
        for(ModelFillUp fillUp: fillUps){
            if(Integer.parseInt(fillUp.getStatus()) >= 0)
                toReturn.add(fillUp);
        }
        return toReturn;
    }

    public ArrayList<ModelFillUp> getAllFillUps() {
        return fillUps;
    }

    public ArrayList<ModelExpense> getExpenses() {
        return expenses;
    }
}
