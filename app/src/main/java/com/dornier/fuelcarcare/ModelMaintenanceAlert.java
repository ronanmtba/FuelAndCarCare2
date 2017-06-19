package com.dornier.fuelcarcare;

import java.util.Date;

/**
 * Created by Dornier on 16/03/2017.
 */

public class ModelMaintenanceAlert {
    private long local_id;
    private String id;
    private String kilometers;
    private String item;
    private Date maintenance_date;
    private String car_id;
    private String status;


    public ModelMaintenanceAlert(long local_id, String id, String kilometers, String item, Date date, String car_id, String status) {
        this.local_id         = local_id;
        this.id               = id;
        this.kilometers       = kilometers;
        this.item             = item;
        this.maintenance_date = date;
        this.car_id           = car_id;
        this.status           = status;
    }

    public ModelMaintenanceAlert(long local_id, String id, String kilometers, String item, String date, String car_id, String status) {
        this.local_id         = local_id;
        this.id               = id;
        this.kilometers       = kilometers;
        this.item             = item;
        this.maintenance_date = ModelDataManager.stringToDate(date);
        this.car_id           = car_id;
        this.status           = status;
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

    public String getKilometers() {
        return kilometers;
    }

    public void setKilometers(String kilometers) {
        this.kilometers = kilometers;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public Date getMaintenance_date() {
        return maintenance_date;
    }

    public void setMaintenance_date(Date maintenance_date) {
        this.maintenance_date = maintenance_date;
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

