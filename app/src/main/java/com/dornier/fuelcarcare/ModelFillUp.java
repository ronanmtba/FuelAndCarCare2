package com.dornier.fuelcarcare;


import java.util.Date;

/**
 * Created by Dornier on 16/03/2017.
 */

public class ModelFillUp {
    private long local_id;
    private String id;
    private int kilometers;
    private double finalPrice;
    private double fuelPrice;
    private double fuelAmount;
    private String location;
    private Date date;
    private String fuel;
    private long car_id;
    private int status;

    public ModelFillUp(long local_id, String id, double finalPrice, double fuelPrice, double fuelAmount, String location,
                       Date date, String fuel, long car_id, int status, int kilometers) {
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
        this.kilometers = kilometers;
    }

    public ModelFillUp(long local_id, String id, String finalPrice, String fuelPrice, String fuelAmount, String location, String date, String fuel, String car_id, String status, String kilometers) {
        this.local_id   = local_id;
        this.finalPrice = Double.parseDouble(finalPrice);
        this.fuelPrice  = Double.parseDouble(fuelPrice);
        this.fuelAmount = Double.parseDouble(fuelAmount);
        this.location   = location;
        this.date       = ModelDataManager.stringToDate(date);
        this.fuel       = fuel;
        this.id         = id;
        this.car_id     = Long.parseLong(car_id);
        this.status     = Integer.parseInt(status);
        this.kilometers = Integer.parseInt(kilometers);
    }

    public ModelFillUp(String finalPrice, String fuelPrice, String fuelAmount, String location, String date, String fuel, String car_id, String kilometers) {
        this.local_id   = 0;
        this.finalPrice = Double.parseDouble(finalPrice);
        this.fuelPrice  = Double.parseDouble(fuelPrice);
        this.fuelAmount = Double.parseDouble(fuelAmount);
        this.location   = location;
        this.date       = ModelDataManager.stringToDate(date);
        this.fuel       = fuel;
        this.id         = "0";
        this.car_id     = Long.parseLong(car_id);
        this.status     = 0;
        this.kilometers = Integer.parseInt(kilometers);
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

    public double getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(double finalPrice) {
        this.finalPrice = finalPrice;
    }

    public double getFuelPrice() {
        return fuelPrice;
    }

    public void setFuelPrice(double fuelPrice) {
        this.fuelPrice = fuelPrice;
    }

    public double getFuelAmount() {
        return fuelAmount;
    }

    public void setFuelAmount(double fuelAmount) {
        this.fuelAmount = fuelAmount;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getFuel() {
        return fuel;
    }

    public void setFuel(String fuel) {
        this.fuel = fuel;
    }

    public long getCar_id() {
        return car_id;
    }

    public void setCar_id(long car_id) {
        this.car_id = car_id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getKilometers() {
        return kilometers;
    }

    public void setKilometers(int kilometers) {
        this.kilometers = kilometers;
    }
}
