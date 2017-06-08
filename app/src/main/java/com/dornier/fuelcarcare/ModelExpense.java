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
        this.date           = ModelDataManager.StringToDate(date);
        this.car_id         = car_id;
        this.status         = status;
    }
}
