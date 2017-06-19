package com.dornier.fuelcarcare;

import java.util.Date;

/**
 * Created by Dornier on 27/03/2017.
 */

public class ModelExpense {
    private long local_id;
    private String id;
    private double price;
    private double quantity;
    private String componentName;
    private Date date;
    private String car_id;
    private String status;

    public ModelExpense(long local_id, String id, double price, double quantity, String componentName, Date date, String car_id, String status) {
        this.local_id       = local_id;
        this.id             = id;
        this.price          = price;
        this.quantity       = quantity;
        this.componentName  = componentName;
        this.date           = date;
        this.car_id         = car_id;
        this.status         = status;
    }

    public ModelExpense(long local_id, String id, String price, String quantity, String componentName, String date, String car_id, String status) {
        this.local_id       = local_id;
        this.id             = id;
        this.price          = Double.parseDouble(price);
        this.quantity       = Double.parseDouble(quantity);
        this.componentName  = componentName;
        this.date           = ModelDataManager.stringToDate(date);
        this.car_id         = car_id;
        this.status         = status;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getCar_id() {
        return car_id;
    }

    public void setCar_id(String car_id) {
        this.car_id = car_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
