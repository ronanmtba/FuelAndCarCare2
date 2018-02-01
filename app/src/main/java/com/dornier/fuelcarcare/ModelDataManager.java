package com.dornier.fuelcarcare;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.util.LongSparseArray;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Dornier on 27/03/2017.
 */

public class ModelDataManager {

    private Context actualContext;
    private static ModelDataManager singleton;
    private static final String TAG = ModelDataManager.class.getSimpleName();
    private ArrayList<ModelVehicle> vehicles;
    private String userId;
    public Context getActualContext() {
        return actualContext;
    }

    public void setActualContext(Context actualContext) {
        this.actualContext = actualContext;
    }

    /*************/
    /* SINGLETON */
    /*************/

    private ModelDataManager() {
        vehicles = new ArrayList<ModelVehicle>();
    }

    public static ModelDataManager getInstance(){
        if(singleton == null){
            singleton = new ModelDataManager();
        }
        return singleton;
    }

    /*********************************/
    /* Specific Server Communication */
    /*********************************/

    public void createUser(ReceiveFromServer o, String email, String password){
        JSONObject obj = new JSONObject();
        try {
            obj.put("email", email);
            obj.put("password", password);
        }
        catch (Exception e){
            printErrorToConsole(e);
        }
        ModelNetworkManager.getInstance().requestToServer("create_user.php", obj, o, "createUser");
    }

    public void login(ReceiveFromServer o, String email, String password){
        JSONObject obj = new JSONObject();
        try {
            obj.put("email", email);
            obj.put("password", password);
        }
        catch (Exception e){
            printErrorToConsole(e);
        }
        ModelNetworkManager.getInstance().requestToServer("login.php", obj, o, "login");
    }

    public void syncData(ReceiveFromServer o){
        JSONObject obj = new JSONObject();
        JSONArray vehicles = new JSONArray();
        try {

            for (ModelVehicle vehicle : this.vehicles) {
                JSONObject jsonVehicle = new JSONObject();
                JSONArray jsonExpenses = new JSONArray();
                JSONArray jsonMaintenances = new JSONArray();
                JSONArray jsonFillUps = new JSONArray();
                jsonVehicle.put("_id", vehicle.getId());
                jsonVehicle.put("status", vehicle.getStatus());
                if(vehicle.getStatus() == 0){
                    jsonVehicle.put("local_id", vehicle.getLocal_id());
                    jsonVehicle.put("model", vehicle.getModel());
                    jsonVehicle.put("manufacturer", vehicle.getManufacturer());
                    jsonVehicle.put("name", vehicle.getName());
                    jsonVehicle.put("year", vehicle.getYear());
                }
                //Add expenses
                for(ModelExpense expense: vehicle.getExpenses()){
                    JSONObject jsonExpense = new JSONObject();
                    jsonExpense.put("_id", expense.getId());
                    jsonExpense.put("status", expense.getStatus());
                    if(expense.getStatus() == 0){
                        jsonExpense.put("local_id", expense.getLocal_id());
                        jsonExpense.put("date", ModelDataManager.dateToMySQL(expense.getDate()));
                        jsonExpense.put("price", expense.getPrice());
                        jsonExpense.put("component_name", expense.getComponentName());
                        jsonExpense.put("quantity", expense.getQuantity());
                    }
                    jsonExpenses.put(jsonExpense);
                }
                //Add fill ups
                for(ModelFillUp fillUp: vehicle.getAllFillUps()){
                    JSONObject jsonFillUp = new JSONObject();
                    jsonFillUp.put("_id", fillUp.getId());
                    jsonFillUp.put("status", fillUp.getStatus());
                    if(fillUp.getStatus() == 0){
                        jsonFillUp.put("date", ModelDataManager.dateToMySQL(fillUp.getDate()));
                        jsonFillUp.put("local_id", fillUp.getLocal_id());
                        jsonFillUp.put("final_price", fillUp.getFinalPrice());
                        jsonFillUp.put("fuel", fillUp.getFuel());
                        jsonFillUp.put("fuel_amount", fillUp.getFuelAmount());
                        jsonFillUp.put("fuel_price", fillUp.getFuelPrice());
                        jsonFillUp.put("kilometers", fillUp.getKilometers());
                        jsonFillUp.put("location", fillUp.getLocation());
                    }
                    jsonFillUps.put(jsonFillUp);
                }
                //add maintenances
                for(ModelMaintenanceAlert alert: vehicle.getAllAlerts()){
                    JSONObject jsonAlert = new JSONObject();
                    jsonAlert.put("_id", alert.getId());
                    jsonAlert.put("status", alert.getStatus());
                    if(alert.getStatus() == 0){
                        jsonAlert.put("local_id",alert.getLocal_id());
                        jsonAlert.put("item",alert.getItem());
                        jsonAlert.put("kilometers",alert.getKilometers());
                        jsonAlert.put("date",ModelDataManager.dateToMySQL(alert.getMaintenance_date()));
                    }
                    jsonMaintenances.put(jsonAlert);
                }
                jsonVehicle.put("expenses", jsonExpenses);
                jsonVehicle.put("maintenances", jsonMaintenances);
                jsonVehicle.put("fill_ups",jsonFillUps);
                vehicles.put(jsonVehicle);

            }

            obj.put("user_email", getUserLogged());
            obj.put("vehicles", vehicles);
        }


        catch (Exception e){
            printErrorToConsole(e);
        }

        ModelNetworkManager.getInstance().requestToServer("sync.php", obj, o, "sync");
    }

    /***********/
    /* Statics */
    /***********/

    public static void printErrorToConsole(Exception e){
        Log.e("Exception: ", e.toString());
    }

    public static String getActualDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(new Date());
    }

    public static String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String dateToString(Date date) throws NullPointerException{
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String reportDate = df.format(date);
        return reportDate;
    }

    public static String dateToMySQL(Date date) throws NullPointerException{
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String reportDate = df.format(date);
        return reportDate;
    }

    public static Date stringToDate(String string) {
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.CANADA);
        if(string.length() > 11)
            format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CANADA);
        else if(string.contains("-")){
            format = new SimpleDateFormat("yyyy-MM-dd", Locale.CANADA);
        }
        try {
            Date date = format.parse(string);
            return date;
        } catch (ParseException e) {
            return new Date(0);
        }

    }

    /***********/
    /* Set/Get */
    /***********/

    public ArrayList<ModelVehicle> getVehicles() {
        final ArrayList<ModelVehicle> toReturn = new ArrayList<ModelVehicle>();
        for(ModelVehicle vehicle: vehicles){
            if((vehicle.getStatus()) >= 0)
                toReturn.add(vehicle);
        }
        return toReturn;
    }

    public String getUserLogged(){
        return userId;
    }

    /****************/
    /* DBManagement */
    /****************/

    public void loadFromDB(){
        vehicles = ModelDBAdapter.getInstance(actualContext).getAllVehicles();
        ArrayList<ModelFillUp> fill_ups = ModelDBAdapter.getInstance(actualContext).getAllFillUps();
        ArrayList<ModelExpense> expenses = ModelDBAdapter.getInstance(actualContext).getAllExpenses();
        ArrayList<ModelMaintenanceAlert> alerts = ModelDBAdapter.getInstance(actualContext).getAllMaintenances();

        for(ModelVehicle vehicle: vehicles){

            for(ModelFillUp fill: fill_ups){
                long car_id = (fill.getCar_id());
                if(car_id == vehicle.getLocal_id()){
                    vehicle.getAllFillUps().add(fill);
                    //fill_ups.remove(fill);
                }
            }
            for(ModelMaintenanceAlert alert: alerts){
                long car_id = (alert.getCar_id());
                if(car_id == vehicle.getLocal_id()){
                    vehicle.getAllAlerts().add(alert);
                    //alerts.remove(alert);
                }
            }
            for(ModelExpense expense: expenses){
                long car_id = (expense.getCar_id());
                if(car_id == vehicle.getLocal_id()){
                    vehicle.getExpenses().add(expense);
                    //expenses.remove(expense);
                }
            }
        }
    }

    public void addOrUpdateVehicle(ModelVehicle vehicle){
        ModelDataManager.getInstance().getVehicles().remove(vehicle);
        vehicles.remove(vehicle);
        vehicles.add(ModelDBAdapter.getInstance(actualContext).insertVehicle(vehicle));
    }

    public void addOrUpdateFillUp(ModelVehicle vehicle, ModelFillUp fill_up){
        vehicle.getAllFillUps().remove(fill_up);
        fill_up.setCar_id(vehicle.getLocal_id());
        vehicle.getAllFillUps().add(ModelDBAdapter.getInstance(actualContext).insertFillUp(fill_up));
        //Verifies if there is any alert to show
        ArrayList<ModelMaintenanceAlert> alerts = vehicle.getFilteredAlerts();
        for(ModelMaintenanceAlert alert: alerts){
            if(Integer.parseInt(alert.getKilometers()) < fill_up.getKilometers()){
                alert.setMaintenance_date(new Date());
                addOrUpdateMaintenance(vehicle,alert);
            }
        }
    }

    public void addOrUpdateExpense(ModelVehicle vehicle, ModelExpense expense){
        vehicle.getExpenses().remove(expense);
        expense.setCar_id(vehicle.getLocal_id());
        vehicle.getExpenses().add(ModelDBAdapter.getInstance(actualContext).insertExpense(expense));
    }

    public void addOrUpdateMaintenance(ModelVehicle vehicle, ModelMaintenanceAlert alert){
        vehicle.getAllAlerts().remove(alert);
        alert.setCar_id(vehicle.getLocal_id());
        vehicle.getAllAlerts().add(ModelDBAdapter.getInstance(actualContext).insertMaintenance(alert));
        scheduleAlert(vehicle, alert);
    }

    private void writeToFile(Context c, String text){
        String FILENAME = "auto_login.dat";
        try {
            FileOutputStream fos = c.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.write(text.getBytes());
            fos.close();
        }
        catch (Exception e){
            printErrorToConsole(e);
        }
    }

    public void createAutoLogin(Context c, String userId){
        writeToFile(c, userId);
        this.userId = userId;
    }

    public void removeAutoLogin(Context c){
        writeToFile(c, "0");
    }

    public boolean verifyAutoLogin(Context c){
        StringBuffer datax = new StringBuffer("");
        try {
            FileInputStream fIn = c.openFileInput( "auto_login.dat" ) ;
            InputStreamReader isr = new InputStreamReader ( fIn ) ;
            BufferedReader buffreader = new BufferedReader ( isr ) ;

            String readString = buffreader.readLine ( ) ;
            while ( readString != null ) {
                datax.append(readString);
                readString = buffreader.readLine ( ) ;
            }

            isr.close ( ) ;
            String s = datax.toString();
            if(s.equals("0"))
                return false;
            userId = s;
        } catch ( Exception e ) {
            return false;
        }
        return true ;
    }

    public void clearDB(){
        ModelDBAdapter.getInstance(actualContext).clearDB();
    }


    /*****************/
    /* Alarm Manager */
    /*****************/

    public void scheduleAlert(ModelVehicle vehicle, ModelMaintenanceAlert alert)
    {

        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(alert.getMaintenance_date());
        Long time = gc.getTimeInMillis()+5000;

        Intent intentAlarm = new Intent(this.actualContext, ModelAlarmReceiver.class);
        intentAlarm.putExtra("notification", "Manutenção agendada");
        intentAlarm.putExtra("detail", "Revisar item " + alert.getItem() +
                ", no veiculo " + vehicle.getName() );
        intentAlarm.putExtra("vehicle_id", vehicle.getLocal_id());
        intentAlarm.putExtra("alert_id", alert.getLocal_id());

        AlarmManager alarmManager = (AlarmManager) actualContext.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP,time, PendingIntent.getBroadcast(actualContext,
                (int)alert.getLocal_id(),  intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));

    }

    /*******************/
    /* Receive from DB */
    /*******************/

    public void syncFromDB(String response){
        try {
            JSONObject fromDB = new JSONObject(response);
            JSONArray vehicles_to_remove = new JSONArray(fromDB.getString("vehicles_to_remove"));
            JSONArray expenses_to_remove = new JSONArray(fromDB.getString("expenses_to_remove"));
            JSONArray maintenances_to_remove = new JSONArray(fromDB.getString("maintenances_to_remove"));
            JSONArray fill_ups_to_remove = new JSONArray(fromDB.getString("fill_ups_to_remove"));
            JSONArray vehicles_to_update = new JSONArray(fromDB.getString("vehicles_to_update"));
            JSONArray expenses_to_update = new JSONArray(fromDB.getString("expenses_to_update"));
            JSONArray maintenances_to_update = new JSONArray(fromDB.getString("maintenances_to_update"));
            JSONArray fill_ups_to_update = new JSONArray(fromDB.getString("fill_ups_to_update"));
            JSONArray vehicles_to_insert = new JSONArray(fromDB.getString("vehicles_to_insert"));
            JSONArray expenses_to_insert = new JSONArray(fromDB.getString("expenses_to_insert"));
            JSONArray maintenances_to_insert = new JSONArray(fromDB.getString("maintenances_to_insert"));
            JSONArray fill_ups_to_insert = new JSONArray(fromDB.getString("fill_ups_to_insert"));

            updateVehicles(vehicles_to_update);
            insertVehicles(vehicles_to_insert);
            removeVehicles(vehicles_to_remove);

            updateExpenses(expenses_to_update);
            insertExpenses(expenses_to_insert);
            removeExpenses(expenses_to_remove);

            updateMaintenances(maintenances_to_update);
            insertMaintenances(maintenances_to_insert);
            removeMaintenances(maintenances_to_remove);

            updateFillUps(fill_ups_to_update);
            insertFillUps(fill_ups_to_insert);
            removeFillUps(fill_ups_to_remove);

        }
        catch (Exception e){
            printErrorToConsole(e);
        }
    }

    /********************/
    /* Auxiliar methods */
    /********************/

    private ModelVehicle findVehicleByLocalId(long local_id){
        for(ModelVehicle vehicle: vehicles){
            if(vehicle.getLocal_id() == local_id){
                return vehicle;
            }
        }
        return null;
    }

    private ModelVehicle findVehicleById(long id){
        for(ModelVehicle vehicle: vehicles){
            if(vehicle.getId() == (id)){
                return vehicle;
            }
        }
        return null;
    }

    private ModelExpense findExpenseByLocalId(long local_id){
        for(ModelVehicle vehicle: vehicles){
            for(ModelExpense expense: vehicle.getExpenses()){
                if(expense.getLocal_id() == local_id){
                    return expense;
                }
            }
        }
        return null;
    }

    private ModelMaintenanceAlert findMaintenanceByLocalId(long local_id){
        for(ModelVehicle vehicle: vehicles){
            for(ModelMaintenanceAlert maintenance: vehicle.getAllAlerts()){
                if(maintenance.getLocal_id() == local_id){
                    return maintenance;
                }
            }
        }
        return null;
    }

    private ModelFillUp findFillUpByLocalId(long local_id){
        for(ModelVehicle vehicle: vehicles){
            for(ModelFillUp fillUp: vehicle.getAllFillUps()){
                if(fillUp.getLocal_id() == local_id){
                    return fillUp;
                }
            }
        }
        return null;
    }

    private void removeVehicles(JSONArray vehicles_to_remove){
        try {
            for (int i = 0; i < vehicles_to_remove.length(); i++) {
                JSONObject vehicleDB = new JSONObject(vehicles_to_remove.getString(i));
                ModelVehicle temp = findVehicleByLocalId(vehicleDB.getLong("local_id"));
                temp.setStatus(-1);
                getInstance().addOrUpdateVehicle(temp);
            }
        }
        catch (Exception e){
            printErrorToConsole(e);
        }
    }

    private void removeExpenses(JSONArray expenses_to_remove){
        try{
            for(int i = 0; i < expenses_to_remove.length(); i++){
                JSONObject expenseDB = new JSONObject(expenses_to_remove.getString(i));
                ModelExpense expense = findExpenseByLocalId(expenseDB.getLong("local_id"));
                ModelVehicle vehicle = findVehicleByLocalId((expense.getCar_id()));
                expense.setStatus(-1);
                getInstance().addOrUpdateExpense(vehicle,expense);
            }
        }
        catch (Exception e){
            printErrorToConsole(e);
        }
    }

    private void removeMaintenances(JSONArray maintenances_to_remove){
        try{
            for(int i = 0; i < maintenances_to_remove.length(); i++){
                JSONObject maintenanceDB = new JSONObject(maintenances_to_remove.getString(i));
                ModelMaintenanceAlert alert = findMaintenanceByLocalId(maintenanceDB.getLong("local_id"));
                ModelVehicle vehicle = findVehicleByLocalId((alert.getCar_id()));
                alert.setStatus(-1);
                getInstance().addOrUpdateMaintenance(vehicle, alert);
            }
        }
        catch (Exception e){
            printErrorToConsole(e);
        }
    }

    private void removeFillUps(JSONArray fill_ups_to_remove){
        try{
            for(int i = 0; i < fill_ups_to_remove.length(); i++){
                JSONObject fillUpDB = new JSONObject(fill_ups_to_remove.getString(i));
                ModelFillUp fillUp = findFillUpByLocalId(fillUpDB.getLong("local_id"));
                ModelVehicle vehicle = findVehicleByLocalId((fillUp.getCar_id()));
                fillUp.setStatus(-1);
                getInstance().addOrUpdateFillUp(vehicle, fillUp);
            }
        }
        catch (Exception e){
            printErrorToConsole(e);
        }
    }

    private void insertVehicles(JSONArray vehicles_to_insert){
        try{
            for(int i = 0; i < vehicles_to_insert.length(); i++){
                JSONObject vehicleDB = new JSONObject(vehicles_to_insert.getString(i));
                ModelVehicle temp = new ModelVehicle(0,
                        vehicleDB.getString("_id"),
                        vehicleDB.getString("name"),
                        vehicleDB.getString("manufacturer"),
                        vehicleDB.getString("model"),
                        vehicleDB.getString("year"),
                        vehicleDB.getString("status"));
                addOrUpdateVehicle(temp);
            }
        }
        catch (Exception e){
            printErrorToConsole(e);
        }
    }

    private void insertExpenses(JSONArray expenses_to_insert){
        try{
            for(int i = 0; i < expenses_to_insert.length(); i++){
                JSONObject expenseDB = new JSONObject(expenses_to_insert.getString(i));
                ModelExpense temp = new ModelExpense(0,
                        expenseDB.getString("_id"),
                        expenseDB.getString("price"),
                        expenseDB.getString("quantity"),
                        expenseDB.getString("component_name"),
                        expenseDB.getString("date"),
                        expenseDB.getString("car_id"),
                        expenseDB.getString("status"));
                ModelVehicle vehicle = findVehicleById((temp.getCar_id()));
                addOrUpdateExpense(vehicle,temp);
            }
        }
        catch (Exception e){
            printErrorToConsole(e);
        }
    }

    private void insertMaintenances(JSONArray maintenances_to_insert){
        try{
            for(int i = 0; i < maintenances_to_insert.length(); i++){
                JSONObject maintenanceDB = new JSONObject(maintenances_to_insert.getString(i));
                ModelMaintenanceAlert temp = new ModelMaintenanceAlert(0,
                        maintenanceDB.getString("_id"),
                        maintenanceDB.getString("kilometers"),
                        maintenanceDB.getString("item"),
                        maintenanceDB.getString("date"),
                        maintenanceDB.getString("car_id"),
                        maintenanceDB.getString("status"));
                ModelVehicle vehicle = findVehicleById((temp.getCar_id()));
                addOrUpdateMaintenance(vehicle,temp);
            }
        }
        catch (Exception e){
            printErrorToConsole(e);
        }
    }

    private void insertFillUps(JSONArray fill_ups_to_insert){
        try{
            for(int i = 0; i < fill_ups_to_insert.length(); i++){
                JSONObject fillUpsDB = new JSONObject(fill_ups_to_insert.getString(i));
                ModelFillUp temp = new ModelFillUp(0,
                        fillUpsDB.getString("_id"),
                        fillUpsDB.getString("final_price"),
                        fillUpsDB.getString("fuel_price"),
                        fillUpsDB.getString("fuel_amount"),
                        fillUpsDB.getString("location"),
                        fillUpsDB.getString("date"),
                        fillUpsDB.getString("fuel"),
                        fillUpsDB.getString("car_id"),
                        fillUpsDB.getString("status"),
                        fillUpsDB.getString("kilometers"));
                ModelVehicle vehicle = findVehicleById((temp.getCar_id()));
                addOrUpdateFillUp(vehicle,temp);
            }
        }
        catch (Exception e){
            printErrorToConsole(e);
        }
    }

    private void updateVehicles(JSONArray vehicles_to_update){
        for(int i = 0; i < vehicles_to_update.length(); i++){
            try {
                JSONObject vehicleDB = new JSONObject(vehicles_to_update.getString(i));
                ModelVehicle temp = findVehicleByLocalId(vehicleDB.getLong("local_id"));
                temp.setStatus(1);
                temp.setId(vehicleDB.getLong("_id"));
                getInstance().addOrUpdateVehicle(temp);
            }
            catch (Exception e){
                printErrorToConsole(e);
            }
        }
    }

    private void updateExpenses(JSONArray expenses_to_update){
        try{
            for(int i = 0; i < expenses_to_update.length(); i++){
                JSONObject expenseDB = new JSONObject(expenses_to_update.getString(i));
                ModelExpense expense = findExpenseByLocalId(expenseDB.getLong("local_id"));
                ModelVehicle vehicle = findVehicleByLocalId((expense.getCar_id()));
                expense.setStatus(1);
                expense.setId(expenseDB.getLong("_id"));
                getInstance().addOrUpdateExpense(vehicle,expense);
            }
        }
        catch (Exception e){
            printErrorToConsole(e);
        }
    }

    private void updateMaintenances(JSONArray maintenances_to_update){
        try{
            for(int i = 0; i < maintenances_to_update.length(); i++){
                JSONObject maintenanceDB = new JSONObject(maintenances_to_update.getString(i));
                ModelMaintenanceAlert temp = findMaintenanceByLocalId(maintenanceDB.getLong("local_id"));
                ModelVehicle vehicle = findVehicleByLocalId((temp.getCar_id()));
                temp.setStatus(1);
                temp.setId(maintenanceDB.getLong("_id"));
                getInstance().addOrUpdateMaintenance(vehicle,temp);
            }
        }
        catch (Exception e){
            printErrorToConsole(e);
        }
    }

    private void updateFillUps(JSONArray fill_ups_to_update){
        try{
            for(int i = 0; i < fill_ups_to_update.length(); i++){
                JSONObject fillUpDB = new JSONObject(fill_ups_to_update.getString(i));
                ModelFillUp temp = findFillUpByLocalId(fillUpDB.getLong("local_id"));
                ModelVehicle vehicle = findVehicleByLocalId((temp.getCar_id()));
                temp.setStatus(1);
                temp.setId(fillUpDB.getString("_id"));
                getInstance().addOrUpdateFillUp(vehicle,temp);
            }
        }
        catch (Exception e){
            printErrorToConsole(e);
        }
    }

}
