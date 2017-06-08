package com.dornier.fuelcarcare;

import java.util.Date;

/**
 * Created by Dornier on 16/03/2017.
 */

public class ModelMaintenanceAlert {
    private long id;
    private String kilometers;
    private String item;
    private Date date;
    private String car_id;
    private String status;

    public ModelMaintenanceAlert(long id, String kilometers, String item, Date date, String car_id, String status) {
        this.id         = id;
        this.kilometers = kilometers;
        this.item       = item;
        this.date       = date;
        this.car_id     = car_id;
        this.status     = status;
    }

    public ModelMaintenanceAlert(long id, String kilometers, String item, String date, String car_id, String status) {
        this.id         = id;
        this.kilometers = kilometers;
        this.item       = item;
        this.date       = ModelDataManager.StringToDate(date);
        this.car_id     = car_id;
        this.status     = status;
    }
}

