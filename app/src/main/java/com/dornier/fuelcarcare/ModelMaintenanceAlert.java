package com.dornier.fuelcarcare;

import java.util.Date;

/**
 * Created by Dornier on 16/03/2017.
 */

public class ModelMaintenanceAlert {
    private long local_id;
    private long id;
    private String kilometers;
    private String item;
    private Date maintenance_date;
    private long car_id;
    private int status;


    public ModelMaintenanceAlert(long local_id, long id, String kilometers, String item, Date date, long car_id, int status) {
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
        this.id               = Long.parseLong(id);
        this.kilometers       = kilometers;
        this.item             = item;
        this.maintenance_date = ModelDataManager.stringToDate(date);
        this.car_id           = Long.parseLong(car_id);
        this.status           = Integer.parseInt(status);
    }

    public long getLocal_id() {
        return local_id;
    }

    public void setLocal_id(long local_id) {
        this.local_id = local_id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public Long getCar_id() {
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
}

