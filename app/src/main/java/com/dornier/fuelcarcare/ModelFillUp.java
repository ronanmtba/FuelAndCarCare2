package com.dornier.fuelcarcare;


import java.util.Date;

/**
 * Created by Dornier on 16/03/2017.
 */

public class ModelFillUp {
    private long local_id;
    private String id;
    private double finalPrice;
    private double fuelPrice;
    private double fuelAmount;
    private String location;
    private Date date;
    private String fuel;
    private String car_id;
    private String status;

    public ModelFillUp(long local_id, String id, double finalPrice, double fuelPrice, double fuelAmount, String location, Date date, String fuel, String car_id, String status) {
        this.local_id   = local_id;
        this.finalPrice = finalPrice;
        this.fuelPrice  = fuelPrice;
        this.fuelAmount = fuelAmount;
        this.location   = location;
        this.date       = date;
        this.fuel       = fuel;
        this.id         = id;
        this.car_id     = car_id;
        this.status     = status;
    }

    public ModelFillUp(long local_id, String id, String finalPrice, String fuelPrice, String fuelAmount, String location, String date, String fuel, String car_id, String status) {
        this.local_id   = local_id;
        this.finalPrice = Double.parseDouble(finalPrice);
        this.fuelPrice  = Double.parseDouble(fuelPrice);
        this.fuelAmount = Double.parseDouble(fuelAmount);
        this.location   = location;
        this.date       = ModelDataManager.StringToDate(date);
        this.fuel       = fuel;
        this.id         = id;
        this.car_id     = car_id;
        this.status     = status;
    }
}
