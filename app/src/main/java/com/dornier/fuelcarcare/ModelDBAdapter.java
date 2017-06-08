package com.dornier.fuelcarcare;

import android.database.sqlite.SQLiteDatabase;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by Dornier on 29/05/2017.
 */

public class ModelDBAdapter {

    private SQLiteDatabase database;
    private ModelDBManager dbManager;
    private String[] fill_ups_oolumns = {
            dbManager.fill_ups_local_id,
            dbManager.fill_ups_id,
            dbManager.fill_ups_car_id,
            dbManager.fill_ups_fill_date,
            dbManager.fill_ups_fuel_amount,
            dbManager.fill_ups_fuel,
            dbManager.fill_ups_fuel_price,
            dbManager.fill_ups_final_price,
            dbManager.fill_ups_location,
            dbManager.fill_ups_status
    };
    private String[] expenses_columns = {
            dbManager.expenses_local_id,
            dbManager.expenses_expense_date,
            dbManager.expenses_component_name,
            dbManager.expenses_car_id,
            dbManager.expenses_id,
            dbManager.expenses_quantity,
            dbManager.expenses_status,
            dbManager.expenses_price
    };
    private String[] vehicles_columns = {
            dbManager.vehicles_local_id,
            dbManager.vehicles_manufacturer,
            dbManager.vehicles_id,
            dbManager.vehicles_model,
            dbManager.vehicles_name,
            dbManager.vehicles_status,
            dbManager.vehicles_year
    };
    private String[] maintenance_columns = {
            dbManager.maintenances_local_id,
            dbManager.maintenances_car_id,
            dbManager.maintenances_id,
            dbManager.maintenances_item,
            dbManager.maintenances_kilometers,
            dbManager.maintenances_maintenance_date,
            dbManager.maintenances_status
    };

    public ModelDBAdapter(Context context){
        dbManager = new ModelDBManager(context);
    }

    /**Fill ups methods**/

    public ModelFillUp insertFillUp(String car_id,
                                    String fill_date,
                                    String fuel_amount,
                                    String fuel,
                                    String fuel_price,
                                    String final_price,
                                    String location,
                                    String status,
                                    String id) {

        ContentValues values = new ContentValues();
        values.put(ModelDBManager.fill_ups_car_id, car_id);
        values.put(ModelDBManager.fill_ups_fill_date,fill_date);
        values.put(ModelDBManager.fill_ups_final_price,final_price);
        values.put(ModelDBManager.fill_ups_fuel,fuel);
        values.put(ModelDBManager.fill_ups_fuel_amount,fuel_amount);
        values.put(ModelDBManager.fill_ups_fuel_price,fuel_price);
        values.put(ModelDBManager.fill_ups_id,id);
        values.put(ModelDBManager.fill_ups_location,location);
        values.put(ModelDBManager.fill_ups_status,status);

        long insertId = database.insert(ModelDBManager.table_name_fill_ups, null, values);
        Cursor cursor = database.query(ModelDBManager.table_name_fill_ups, fill_ups_oolumns, ModelDBManager.fill_ups_local_id + " = " +
                insertId, null,null, null, null);
        cursor.moveToFirst();
        return cursorToFillUp(cursor);
    }

    public void removeFillUp(String local_id){
        database.delete(ModelDBManager.table_name_fill_ups, ModelDBManager.fill_ups_local_id + " = " + local_id, null);
    }

    private ModelFillUp cursorToFillUp(Cursor cursor) {
        ModelFillUp fillUp = new ModelFillUp(cursor.getLong(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),
                cursor.getString(4),cursor.getString(5),cursor.getString(6),cursor.getString(7),cursor.getString(8),cursor.getString(9));
        return fillUp;
    }

    public Cursor getFillUps(){
        Cursor cursor = database.rawQuery("select * from " + ModelDBManager.table_name_fill_ups, null);
        return cursor;
    }

    public ModelFillUp getFillUp (int idFillUp){
        Cursor cursor = database.query(ModelDBManager.table_name_fill_ups, fill_ups_oolumns, ModelDBManager.fill_ups_local_id + " = " +
                idFillUp, null,null, null, null);
        cursor.moveToFirst();
        return cursorToFillUp(cursor);
    }
    public ModelFillUp[] getAllFillUps(){
        Cursor cursor = getFillUps();
        ModelFillUp toReturn[] = new ModelFillUp[cursor.getCount()];
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            toReturn[cursor.getPosition()] = cursorToFillUp(cursor);
            cursor.moveToNext();
        }
        return toReturn;
    }

    /**Expenses methods**/
    /**Vehicles methods**/
    /**Maintenances methods**/


}
