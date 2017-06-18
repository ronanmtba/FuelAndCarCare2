package com.dornier.fuelcarcare;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;

public class ActivityShowFillUps extends AppCompatActivity {
    ModelVehicle selectedVehicle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_fill_ups);
        selectedVehicle = ModelDataManager.getInstance().getVehicles().get((int) getIntent().getExtras().getLong("index"));

        LineChart lineChart = (LineChart) findViewById(R.id.ShowFillUpsChart);

        ArrayList<ModelFillUp> fillUps = selectedVehicle.getFillUps();

        ArrayList<Entry> entries = new ArrayList<>();

        ArrayList<String> labels = new ArrayList<String>();

        int colors[] = new int[fillUps.size()];

        entries.add(new Entry(0.0f,0));
        labels.add(ModelDataManager.dateToString(fillUps.get(0).getDate()));
        colors[0] = getFuelColor(fillUps.get(0));

        for(int i = 1; i < fillUps.size(); i++){
            ModelFillUp tempFillUp = fillUps.get(i);
            ModelFillUp previous = fillUps.get(i-1);
            float distance = tempFillUp.getKilometers()-(float)previous.getKilometers();
            float consumption = (float) (distance/tempFillUp.getFuelAmount());
            entries.add(new Entry(consumption,i));
            labels.add(ModelDataManager.dateToString(tempFillUp.getDate()));
            colors[i] = getFuelColor(tempFillUp);
        }

        LineDataSet dataset = new LineDataSet(entries, "Km/L");

        LineData data = new LineData(labels, dataset);
        //dataset.setColors(ColorTemplate.COLORFUL_COLORS); //
        //dataset.setDrawCubic(true);
        dataset.setCircleColors(colors);
        dataset.setDrawFilled(true);

        lineChart.setData(data);
        lineChart.animateY(5000);
    }

    public int getFuelColor(ModelFillUp fill_up){
        int colorToReturn = 0;
        switch (fill_up.getFuel()){
            case "Gasolina":
                colorToReturn = Color.RED;
                break;
            case "Etanol":
                colorToReturn = Color.GREEN;
                break;
            case "Diesel":
                colorToReturn = Color.BLACK;
                break;
            case "GNV":
                colorToReturn = Color.BLUE;
                break;
        }
        return colorToReturn;
    }
}
