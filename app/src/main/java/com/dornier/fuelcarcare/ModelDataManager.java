package com.dornier.fuelcarcare;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

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
    private RequestQueue mRequestQueue;
    private Context actualContext;
    private static ModelDataManager singleton;
    private Boolean sessionStart;
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
        CookieManager cm = new CookieManager();
        CookieHandler.setDefault(cm);
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
        requestToServer("create_user.php", obj, o, "createUser");
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
        requestToServer("login.php", obj, o, "login");
    }

    public void syncData(ReceiveFromServer o){
        JSONObject obj = new JSONObject();
        JSONArray vehicles = new JSONArray();
        try {
            for (ModelVehicle vehicle : this.getVehicles()) {
                JSONObject jsonVehicle = new JSONObject();
                JSONArray jsonExpenses = new JSONArray();
                JSONArray jsonMaintenances = new JSONArray();
                JSONArray jsonFillUps = new JSONArray();
                jsonVehicle.put("_id", vehicle.getId());
                jsonVehicle.put("status", vehicle.getStatus());
                if(vehicle.getStatus().equals("0")){
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
                    if(expense.getStatus().equals("0")){
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
                    if(fillUp.getStatus().equals("0")){
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
                    if(alert.getStatus().equals("0")){
                        jsonAlert.put("local_id",alert.getLocal_id());
                        jsonAlert.put("item",alert.getItem());
                        jsonAlert.put("kilometers",alert.getKilometers());
                        jsonAlert.put("date",ModelDataManager.dateToMySQL(alert.getMaintenance_date()));
                    }
                }
                jsonVehicle.put("expenses", jsonExpenses);
                jsonVehicle.put("maintenances", jsonMaintenances);
                jsonVehicle.put("fill_ups",jsonFillUps);
                vehicles.put(jsonVehicle);

            }

            obj.put("user_email", this.userId);
            obj.put("vehicles", vehicles);
        }


        catch (Exception e){
            printErrorToConsole(e);
        }

        requestToServer("sync.php", obj, o, "sync");
    }

    /**************************************/
    /* Shared Server Comunication methods */
    /**************************************/

    public void requestToServer(final String page, final JSONObject array, ReceiveFromServer o, final String identifier) {
        final ReceiveFromServer requestOwner = o;

      /*  if(!sessionStart)
            setSession();*/
        Log.v("identifier", " => " +array);

        StringRequest sr = new StringRequest(Request.Method.POST, "http://192.168.95.128/fcc/" + page, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.v(TAG,page+" => "+response);
                requestOwner.serverCall(response, identifier);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                requestOwner.serverCall(null, identifier);
                sessionStart = false;
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                for(int i = 0; i<array.names().length(); i++){
                    try {
                        params.put(array.names().getString(i), array.getString(array.names().getString(i)));
                    }
                    catch (Exception e){
                        ModelDataManager.printErrorToConsole(e);
                    }
                }
                return params;
            }

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                params.put("Content-Length",String.valueOf(getBody().length));
                return params;
            }

        };

        // queue.add(sr);

        sr.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        sr.setShouldCache(true);

        getInstance().addToRequestQueue(sr);
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(actualContext);
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
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
            if(Integer.parseInt(vehicle.getStatus()) >= 0)
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
                long car_id = Long.parseLong(fill.getCar_id());
                if(car_id == vehicle.getLocal_id()){
                    vehicle.getAllFillUps().add(fill);
                    //fill_ups.remove(fill);
                }
            }
            for(ModelMaintenanceAlert alert: alerts){
                long car_id = Long.parseLong(alert.getCar_id());
                if(car_id == vehicle.getLocal_id()){
                    vehicle.getAllAlerts().add(alert);
                    //alerts.remove(alert);
                }
            }
            for(ModelExpense expense: expenses){
                long car_id = Long.parseLong(expense.getCar_id());
                if(car_id == vehicle.getLocal_id()){
                    vehicle.getExpenses().add(expense);
                    //expenses.remove(expense);
                }
            }
        }
    }

    public void addOrUpdateVehicle(ModelVehicle vehicle){
        ModelDataManager.getInstance().getVehicles().remove(vehicle);
        vehicles.add(ModelDBAdapter.getInstance(actualContext).insertVehicle(vehicle));
    }

    public void addOrUpdateFillUp(ModelVehicle vehicle, ModelFillUp fill_up){
        vehicle.getAllFillUps().remove(fill_up);
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
        vehicle.getExpenses().add(ModelDBAdapter.getInstance(actualContext).insertExpense(expense));
    }

    public void addOrUpdateMaintenance(ModelVehicle vehicle, ModelMaintenanceAlert alert){
        vehicle.getAllAlerts().remove(alert);
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
        alarmManager.set(AlarmManager.RTC_WAKEUP,time, PendingIntent.getBroadcast(actualContext,(int)alert.getLocal_id(),  intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));

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

            for(int i = 0; i < vehicles_to_update.length(); i++){
                JSONObject vehicleDB = new JSONObject(vehicles_to_update.getString(i));
                ModelVehicle temp = ModelDBAdapter.getInstance(getActualContext()).getVehicle(vehicleDB.getLong("local_id"));
                temp.setId(vehicleDB.getString("_id"));
                getInstance().addOrUpdateVehicle(temp);
            }

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
}
