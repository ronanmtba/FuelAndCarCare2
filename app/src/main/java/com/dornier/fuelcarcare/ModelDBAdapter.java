package com.dornier.fuelcarcare;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dornier on 29/05/2017.
 */

public class ModelDBAdapter {

    private SQLiteDatabase database;
    private static ModelDBAdapter singleton;
    private ModelDBManager dbManager;
    private String[] fill_ups_columns = {
            dbManager.fill_ups_local_id,
            dbManager.fill_ups_id,
            dbManager.fill_ups_car_id,
            dbManager.fill_ups_fill_date,
            dbManager.fill_ups_fuel_amount,
            dbManager.fill_ups_fuel,
            dbManager.fill_ups_fuel_price,
            dbManager.fill_ups_final_price,
            dbManager.fill_ups_location,
            dbManager.fill_ups_status,
            dbManager.fill_ups_kilometers
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

    private ModelDBAdapter(Context context){
        dbManager = new ModelDBManager(context);
    }

    public static ModelDBAdapter getInstance(Context context){
        if(singleton == null){
            singleton = new ModelDBAdapter(context);
            singleton.open();
        }
        return singleton;
    }

    public void open() throws SQLException {
        database = dbManager.getWritableDatabase();
    }

    /**Expenses methods**/

    public ModelExpense insertExpense(ModelExpense expense) {

        ContentValues values = new ContentValues();
        values.put(ModelDBManager.expenses_expense_date,    ModelDataManager.dateToString(expense.getDate()));
        values.put(ModelDBManager.expenses_component_name,  expense.getComponentName());
        values.put(ModelDBManager.expenses_car_id,          expense.getCar_id());
        values.put(ModelDBManager.expenses_id,              expense.getId());
        values.put(ModelDBManager.expenses_quantity,        expense.getQuantity());
        values.put(ModelDBManager.expenses_status,          expense.getStatus());
        values.put(ModelDBManager.expenses_price,           expense.getPrice());

        if(expense.getLocal_id() > 0){
            database.update(ModelDBManager.table_name_expenses,values,ModelDBManager.expenses_local_id+"="+expense.getLocal_id(), null);
            return expense;
        }

        long insertId = database.insert(ModelDBManager.table_name_expenses, null, values);
        Cursor cursor = database.query(ModelDBManager.table_name_expenses, expenses_columns, ModelDBManager.expenses_local_id + " = " +
                insertId, null,null, null, null);
        cursor.moveToFirst();
        return cursorToExpense(cursor);
    }

    public void removeExpense(String local_id){
        database.delete(ModelDBManager.table_name_expenses, ModelDBManager.expenses_local_id + " = " + local_id, null);
    }

    private ModelExpense cursorToExpense(Cursor cursor) {
        ModelExpense expense =
                new ModelExpense(cursor.getLong(cursor.getColumnIndex(ModelDBManager.expenses_local_id)),
                cursor.getString(cursor.getColumnIndex(ModelDBManager.expenses_id)),
                cursor.getString(cursor.getColumnIndex(ModelDBManager.expenses_price)),
                cursor.getString(cursor.getColumnIndex(ModelDBManager.expenses_quantity)),
                cursor.getString(cursor.getColumnIndex(ModelDBManager.expenses_component_name)),
                cursor.getString(cursor.getColumnIndex(ModelDBManager.expenses_expense_date)),
                cursor.getString(cursor.getColumnIndex(ModelDBManager.expenses_car_id)),
                cursor.getString(cursor.getColumnIndex(ModelDBManager.expenses_status)));
        return expense;
    }

    public Cursor getExpenses(){
        Cursor cursor = database.rawQuery("select * from " + ModelDBManager.table_name_expenses, null);
        return cursor;
    }

    public ModelExpense getExpense (long idExpense){
        Cursor cursor = database.query(ModelDBManager.table_name_expenses, expenses_columns, ModelDBManager.expenses_local_id + " = " +
                idExpense, null,null, null, null);
        cursor.moveToFirst();
        return cursorToExpense(cursor);
    }

    public ArrayList<ModelExpense> getAllExpenses(){
        Cursor cursor = getExpenses();
        ArrayList<ModelExpense> toReturn = new ArrayList<ModelExpense>();
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                toReturn.add(cursorToExpense(cursor));
                cursor.moveToNext();
            }
        }
        return toReturn;
    }

    /**Fill Up methods**/

    public ModelFillUp insertFillUp(ModelFillUp fillUp) {

        ContentValues values = new ContentValues();
        values.put(ModelDBManager.fill_ups_car_id,      fillUp.getCar_id());
        values.put(ModelDBManager.fill_ups_fill_date,   ModelDataManager.dateToString(fillUp.getDate()));
        values.put(ModelDBManager.fill_ups_final_price, fillUp.getFinalPrice());
        values.put(ModelDBManager.fill_ups_fuel,        fillUp.getFuel());
        values.put(ModelDBManager.fill_ups_fuel_amount, fillUp.getFuelAmount());
        values.put(ModelDBManager.fill_ups_fuel_price,  fillUp.getFuelPrice());
        values.put(ModelDBManager.fill_ups_id,          fillUp.getId());
        values.put(ModelDBManager.fill_ups_location,    fillUp.getLocation());
        values.put(ModelDBManager.fill_ups_status,      fillUp.getStatus());
        values.put(ModelDBManager.fill_ups_kilometers,  fillUp.getKilometers());

        if(fillUp.getLocal_id() > 0){
            database.update(ModelDBManager.table_name_fill_ups,values,ModelDBManager.fill_ups_local_id+"="+fillUp.getLocal_id(), null);
            return fillUp;
        }

        long insertId = database.insert(ModelDBManager.table_name_fill_ups, null, values);
        Cursor cursor = database.query(ModelDBManager.table_name_fill_ups, fill_ups_columns, ModelDBManager.fill_ups_local_id + " = " +
                insertId, null,null, null, null);
        cursor.moveToFirst();
        return cursorToFillUp(cursor);
    }

    public void removeFillUp(String local_id){
        database.delete(ModelDBManager.table_name_fill_ups, ModelDBManager.fill_ups_local_id + " = " + local_id, null);
    }

    private ModelFillUp cursorToFillUp(Cursor cursor) {
        ModelFillUp fillUp = new ModelFillUp(cursor.getLong(cursor.getColumnIndex(ModelDBManager.fill_ups_local_id)),
                cursor.getString(cursor.getColumnIndex(ModelDBManager.fill_ups_id)),
                cursor.getString(cursor.getColumnIndex(ModelDBManager.fill_ups_final_price)),
                cursor.getString(cursor.getColumnIndex(ModelDBManager.fill_ups_fuel_price)),
                cursor.getString(cursor.getColumnIndex(ModelDBManager.fill_ups_fuel_amount)),
                cursor.getString(cursor.getColumnIndex(ModelDBManager.fill_ups_location)),
                cursor.getString(cursor.getColumnIndex(ModelDBManager.fill_ups_fill_date)),
                cursor.getString(cursor.getColumnIndex(ModelDBManager.fill_ups_fuel)),
                cursor.getString(cursor.getColumnIndex(ModelDBManager.fill_ups_car_id)),
                cursor.getString(cursor.getColumnIndex(ModelDBManager.fill_ups_status)),
                cursor.getString(cursor.getColumnIndex(ModelDBManager.fill_ups_kilometers)));
        return fillUp;
    }

    public Cursor getFillUps(){
        Cursor cursor = database.rawQuery("select * from " + ModelDBManager.table_name_fill_ups, null);
        return cursor;
    }

    public ModelFillUp getFillUp (long idFillUp){
        Cursor cursor = database.query(ModelDBManager.table_name_fill_ups, fill_ups_columns, ModelDBManager.fill_ups_local_id + " = " +
                idFillUp, null,null, null, null);
        cursor.moveToFirst();
        return cursorToFillUp(cursor);
    }

    public ArrayList<ModelFillUp> getAllFillUps(){
        Cursor cursor = getFillUps();
        ArrayList<ModelFillUp> toReturn = new ArrayList<ModelFillUp>();
        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                toReturn.add(cursorToFillUp(cursor));
                cursor.moveToNext();
            }
        }
        return toReturn;
    }

    /**Vehicles methods**/

    public ModelVehicle insertVehicle(ModelVehicle vehicle) {

        ContentValues values = new ContentValues();
        values.put(ModelDBManager.vehicles_id,          vehicle.getId());
        values.put(ModelDBManager.vehicles_name,        vehicle.getName());
        values.put(ModelDBManager.vehicles_manufacturer,vehicle.getManufacturer());
        values.put(ModelDBManager.vehicles_model,       vehicle.getModel());
        values.put(ModelDBManager.vehicles_year,        vehicle.getYear());
        values.put(ModelDBManager.vehicles_status,      vehicle.getStatus());
        if(vehicle.getLocal_id() > 0){
            database.update(ModelDBManager.table_name_vehicles,values,ModelDBManager.vehicles_local_id+
                    "="+vehicle.getLocal_id(), null);
            return vehicle;
        }
        long insertId = database.insert(ModelDBManager.table_name_vehicles, null, values);
        Cursor cursor = database.query(ModelDBManager.table_name_vehicles, vehicles_columns,
                ModelDBManager.vehicles_local_id + " = " +
                insertId, null,null, null, null);
        cursor.moveToFirst();
        return cursorToVehicle(cursor);
    }

    public void removeVehicle(String local_id){
        database.delete(ModelDBManager.table_name_vehicles, ModelDBManager.vehicles_local_id + " = " + local_id, null);
    }

    private ModelVehicle cursorToVehicle(Cursor cursor) {
        //public ModelVehicle(long local_id, String id, String name, String manufacturer, String model, String year, String status) {
        ModelVehicle vehicle = new ModelVehicle(cursor.getLong(cursor.getColumnIndex("local_id")),
                cursor.getString(cursor.getColumnIndex("_id")),
                cursor.getString(cursor.getColumnIndex("name")),
                cursor.getString(cursor.getColumnIndex("manufacturer")),
                cursor.getString(cursor.getColumnIndex("model")),
                cursor.getString(cursor.getColumnIndex("year")),
                cursor.getString(cursor.getColumnIndex("status")));

        return vehicle;
    }

    public Cursor getVehicles(){
        Cursor cursor = database.rawQuery("select * from " + ModelDBManager.table_name_vehicles, null);
        return cursor;
    }

    public ModelVehicle getVehicle (long idVehicle){
        Cursor cursor = database.query(ModelDBManager.table_name_vehicles, vehicles_columns, ModelDBManager.vehicles_local_id + " = " +
                idVehicle, null,null, null, null);
        cursor.moveToFirst();
        return cursorToVehicle(cursor);
    }

    public ArrayList<ModelVehicle> getAllVehicles(){
        Cursor cursor = getVehicles();
        ArrayList<ModelVehicle> toReturn = new ArrayList<ModelVehicle>();
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                toReturn.add(cursorToVehicle(cursor));
                cursor.moveToNext();
            }
        }
        return toReturn;
    }
    
    /**Maintenances methods**/

    public ModelMaintenanceAlert insertMaintenance(ModelMaintenanceAlert maintenance) {

        ContentValues values = new ContentValues();
        values.put(ModelDBManager.maintenances_id,              maintenance.getId());
        values.put(ModelDBManager.maintenances_kilometers,      maintenance.getKilometers());
        values.put(ModelDBManager.maintenances_item,            maintenance.getItem());
        values.put(ModelDBManager.maintenances_maintenance_date,ModelDataManager.dateToString(maintenance.getMaintenance_date()));
        values.put(ModelDBManager.maintenances_car_id,          maintenance.getCar_id());
        values.put(ModelDBManager.maintenances_status,          maintenance.getStatus());

        if(maintenance.getLocal_id() > 0){
            database.update(ModelDBManager.table_name_maintenances,values,ModelDBManager.maintenances_local_id+"="+maintenance.getLocal_id(), null);
            return maintenance;
        }

        long insertId = database.insert(ModelDBManager.table_name_maintenances, null, values);
        Cursor cursor = database.query(ModelDBManager.table_name_maintenances, maintenance_columns, ModelDBManager.maintenances_local_id + " = " +
                insertId, null,null, null, null);
        cursor.moveToFirst();
        return cursorToMaintenance(cursor);
    }

    public void removeMaintenance(String local_id){
        database.delete(ModelDBManager.table_name_maintenances, ModelDBManager.maintenances_local_id + " = " + local_id, null);
    }

    private ModelMaintenanceAlert cursorToMaintenance(Cursor cursor) {
        ModelMaintenanceAlert maintenance = new ModelMaintenanceAlert(cursor.getLong(cursor.getColumnIndex(ModelDBManager.maintenances_local_id)),
                cursor.getString(cursor.getColumnIndex(ModelDBManager.maintenances_id)),
                cursor.getString(cursor.getColumnIndex(ModelDBManager.maintenances_kilometers)),
                cursor.getString(cursor.getColumnIndex(ModelDBManager.maintenances_item)),
                cursor.getString(cursor.getColumnIndex(ModelDBManager.maintenances_maintenance_date)),
                cursor.getString(cursor.getColumnIndex(ModelDBManager.maintenances_car_id)),
                cursor.getString(cursor.getColumnIndex(ModelDBManager.maintenances_status)));

        return maintenance;
    }

    public Cursor getMaintenances(){
        Cursor cursor = database.rawQuery("select * from " + ModelDBManager.table_name_maintenances, null);
        return cursor;
    }

    public ModelMaintenanceAlert getMaintenance (long idMaintenance){
        Cursor cursor = database.query(ModelDBManager.table_name_maintenances, maintenance_columns, ModelDBManager.maintenances_local_id + " = " +
                idMaintenance, null,null, null, null);
        cursor.moveToFirst();
        return cursorToMaintenance(cursor);
    }

    public ArrayList<ModelMaintenanceAlert> getAllMaintenances(){
        Cursor cursor = getMaintenances();
        ArrayList<ModelMaintenanceAlert> toReturn = new ArrayList<ModelMaintenanceAlert>();
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                toReturn.add(cursorToMaintenance(cursor));
                cursor.moveToNext();
            }
        }
        return toReturn;
    }

    public void clearDB(){
        dbManager.dropTables(database);
        dbManager.createTables(database);
    }

}
