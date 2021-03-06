package com.dornier.fuelcarcare;

import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dornier on 29/05/2017.
 */

public class ModelDBManager extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "local_db.db";
    private static final int DATABASE_VERSION = 1;

    public ModelDBManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        createTables(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        dropTables(sqLiteDatabase);
    }

    public void createTables(SQLiteDatabase sqLiteDatabase){
        sqLiteDatabase.execSQL(database_create_fill_ups);
        sqLiteDatabase.execSQL(database_create_expenses);
        sqLiteDatabase.execSQL(database_create_maintenances);
        sqLiteDatabase.execSQL(database_create_vehicles);
    }

    public void dropTables(SQLiteDatabase sqLiteDatabase){
        List<String> tables = new ArrayList<>();

        tables.add("fill_ups");
        tables.add("expenses");
        tables.add("maintenances");
        tables.add("vehicles");

        for (String table : tables) {
            String dropQuery = "DROP TABLE IF EXISTS " + table;
            sqLiteDatabase.execSQL(dropQuery);
        }
    }

    /**Tabela fill_ups**/

    public static final String table_name_fill_ups =    "fill_ups";
    public static final String fill_ups_local_id =      "local_id";
    public static final String fill_ups_id =            "_id";
    public static final String fill_ups_final_price =   "final_price";
    public static final String fill_ups_fuel_price =    "fuel_price";
    public static final String fill_ups_fuel_amount =   "fuel_amount";
    public static final String fill_ups_location =      "location";
    public static final String fill_ups_fill_date =     "fill_date";
    public static final String fill_ups_fuel =          "fuel";
    public static final String fill_ups_car_id =        "car_id";
    public static final String fill_ups_status =        "status";
    public static final String fill_ups_kilometers =    "kilometers";

    private static final String database_create_fill_ups =  "create table "
            + table_name_fill_ups + "( " +
            fill_ups_local_id       +" integer primary key autoincrement, " +
            fill_ups_id             +" integer not null, " +
            fill_ups_final_price    +" real not null, " +
            fill_ups_fuel_price     +" real not null, " +
            fill_ups_fuel_amount    +" real not null, " +
            fill_ups_kilometers     +" integer not null, " +
            fill_ups_location       +" text not null, " +
            fill_ups_fill_date      +" text not null, " +
            fill_ups_fuel           +" text not null, " +
            fill_ups_car_id         +" integer not null, " +
            fill_ups_status         +" integer not null );";

    /**Tabela expenses**/

    public static final String table_name_expenses =    "expenses";
    public static final String expenses_local_id =      "local_id";
    public static final String expenses_id =            "_id";
    public static final String expenses_price =         "price";
    public static final String expenses_quantity =      "quantity";
    public static final String expenses_component_name ="component_name";
    public static final String expenses_expense_date =  "expense_date";
    public static final String expenses_car_id =        "car_id";
    public static final String expenses_status =        "status";

    private static final String database_create_expenses =  "create table "
            + table_name_expenses + "( " +
            expenses_local_id       +" integer primary key autoincrement, " +
            expenses_id             +" integer not null, " +
            expenses_price          +" real not null, " +
            expenses_quantity       +" real not null, " +
            expenses_component_name +" text not null, " +
            expenses_expense_date   +" text not null, " +
            expenses_car_id         +" integer not null, " +
            expenses_status         +" integer not null );";

    /**tabela maintenances**/

    public static final String table_name_maintenances =        "maintenances";
    public static final String maintenances_local_id =          "local_id";
    public static final String maintenances_id =                "_id";
    public static final String maintenances_kilometers =        "kilometers";
    public static final String maintenances_item =              "item";
    public static final String maintenances_maintenance_date =  "maintenance_date";
    public static final String maintenances_car_id =            "car_id";
    public static final String maintenances_status =            "status";

    private static final String database_create_maintenances =  "create table "
            + table_name_maintenances + "( " +
            maintenances_local_id           +" integer primary key autoincrement, " +
            maintenances_id                 +" integer not null, " +
            maintenances_kilometers         +" integer not null, " +
            maintenances_item               +" text not null, " +
            maintenances_maintenance_date   +" text not null, " +
            maintenances_car_id             +" integer not null, " +
            maintenances_status             +" integer not null );";

    /**tabela vehicles**/

    public static final String table_name_vehicles =    "vehicles";
    public static final String vehicles_local_id =      "local_id";
    public static final String vehicles_id =            "_id";
    public static final String vehicles_name =          "name";
    public static final String vehicles_manufacturer =  "manufacturer";
    public static final String vehicles_model =         "model";
    public static final String vehicles_year =          "year";
    public static final String vehicles_status =        "status";

    private static final String database_create_vehicles =  "create table "
            + table_name_vehicles + "( " +
            vehicles_local_id       +" integer primary key autoincrement, " +
            vehicles_id             +" integer not null, " +
            vehicles_name           +" text not null, " +
            vehicles_manufacturer   +" text not null, " +
            vehicles_model          +" text not null, " +
            vehicles_year           +" integer not null, " +
            vehicles_status         +" integer not null );";
}
