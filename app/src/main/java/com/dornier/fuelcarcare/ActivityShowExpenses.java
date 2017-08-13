package com.dornier.fuelcarcare;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ActivityShowExpenses extends AppCompatActivity{
    ModelVehicle selectedVehicle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_expenses);

        selectedVehicle = ModelDataManager.getInstance().getVehicles().get((int) getIntent().getExtras().getLong("index"));
        selectedVehicle.sortExpenses();

        LineChart lineChart = (LineChart) findViewById(R.id.ShowExpensesChart);

        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<String>();

        Map<String, Double> monthsAndData = getMonthsAndData();

        int counter = 0;
        for ( String key : monthsAndData.keySet() ) {
            entries.add(new Entry(monthsAndData.get(key).floatValue(), counter));
            labels.add(key);
            counter++;
        }

        LineDataSet dataset = new LineDataSet(entries, "Gasto mensal");
        LineData data = new LineData(labels, dataset);

        dataset.setDrawFilled(true);

        lineChart.setDescription("");
        lineChart.setData(data);
        lineChart.animateY(2000);
    }

    private Map<String, Double> getMonthsAndData(){
        ArrayList<ModelExpense> expenses = selectedVehicle.getExpenses();
        Map<String, Double> toReturn = new HashMap<String, Double>();
        for(ModelExpense expense: expenses){
            Date date = expense.getDate();
            String dateInString = ModelDataManager.dateToString(date);
            dateInString = dateInString.substring(3);
            if(toReturn.containsKey(dateInString)){
                toReturn.put(dateInString, toReturn.get(dateInString) + expense.getPrice());
            }
            else{
                toReturn.put(dateInString, expense.getPrice());
            }
        }
        return toReturn;
    }
}
