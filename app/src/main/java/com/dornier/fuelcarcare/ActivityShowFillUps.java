package com.dornier.fuelcarcare;

import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;

public class ActivityShowFillUps extends AppCompatActivity implements OnChartValueSelectedListener{
    ModelVehicle selectedVehicle;
    TextView total_price, fuel_price, liters, kilometers, location, date, fuel;
    LineChart lineChart;
    Button delete;
    ModelFillUp selectedFillUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_fill_ups);

        int selectedVehicleIndex = (int) getIntent().getExtras().getLong("index");
        selectedVehicle = ModelDataManager.getInstance().getVehicles().get(selectedVehicleIndex);
        selectedVehicle.sortfillUps();

        lineChart   = (LineChart)findViewById(R.id.ShowFillUpsChart);
        total_price = (TextView) findViewById(R.id.ShowFillUpsTotalPrice);
        fuel_price  = (TextView) findViewById(R.id.ShowFillUpsFuelPrice);
        liters      = (TextView) findViewById(R.id.ShowFillUpsLiters);
        kilometers  = (TextView) findViewById(R.id.ShowFillUpsKilometers);
        location    = (TextView) findViewById(R.id.ShowFillUpsLocation);
        date        = (TextView) findViewById(R.id.ShowFillUpsDate);
        fuel        = (TextView) findViewById(R.id.ShowFillUpsFuel);
        delete      = (Button)   findViewById(R.id.ShowFillUpsDelete);
        delete.setVisibility(View.INVISIBLE);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDeleteCLick();
            }
        });

        ArrayList<ModelFillUp> fillUps = selectedVehicle.getFilteredFillUps();
        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<String>();

        int colors[] = new int[fillUps.size()];

        //entries.add(new Entry(0.0f,0));
        //labels.add(ModelDataManager.dateToString(fillUps.get(0).getDate()));
        colors[0] = getFuelColor(fillUps.get(0));

        for(int i = 1; i < fillUps.size(); i++){
            ModelFillUp tempFillUp = fillUps.get(i);
            ModelFillUp previous = fillUps.get(i-1);
            float distance = tempFillUp.getKilometers()-(float)previous.getKilometers();
            float consumption = (float) (distance/tempFillUp.getFuelAmount());
            entries.add(new Entry(consumption,i-1,tempFillUp));
            labels.add(ModelDataManager.dateToString(tempFillUp.getDate()));
            colors[i] = getFuelColor(tempFillUp);
        }

        LineDataSet dataset = new LineDataSet(entries, "Consumo");
        LineData data = new LineData(labels, dataset);

        //dataset.setColors(ColorTemplate.COLORFUL_COLORS); //
        //dataset.setDrawCubic(true);
        dataset.setCircleColors(colors);
        dataset.setDrawFilled(true);
        lineChart.setOnChartValueSelectedListener(this);

        lineChart.setDescription("");
        lineChart.setData(data);
        lineChart.animateY(2000);
    }

    private int getFuelColor(ModelFillUp fill_up){
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

    public void onDeleteCLick(){
        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityShowFillUps.this);
        builder.setCancelable(true);
        builder.setTitle("Atenção");
        builder.setMessage("Deseja mesmo excluir o abastecimento selecionado?");
        builder.setPositiveButton("Sim",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onConfirmDelete();
                    }
                });
        builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void onConfirmDelete(){
        selectedFillUp.setStatus("-1");
        ModelDataManager.getInstance().addOrUpdateFillUp(selectedVehicle, selectedFillUp);
        finish();
        //startActivity(new Intent(ActivityShowFillUps.this, ActivityMenuVehicle.class));
        //ActivityShowFillUps.this.finish();
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        try {
            selectedFillUp = (ModelFillUp) e.getData();
            if(selectedFillUp != null) {
                total_price.setText("Valor total: " + selectedFillUp.getFinalPrice());
                fuel_price.setText("Preço: " + selectedFillUp.getFuelPrice());
                liters.setText("Quantidade: " + selectedFillUp.getFuelAmount());
                kilometers.setText("Kilometragem: " + selectedFillUp.getKilometers());
                location.setText("Local: " + selectedFillUp.getLocation());
                date.setText("Data: " + ModelDataManager.dateToString(selectedFillUp.getDate()));
                fuel.setText("Combustivel: " + selectedFillUp.getFuel());
                delete.setVisibility(View.VISIBLE);
            }
            else{
                total_price.setText("");
                fuel_price.setText("");
                liters.setText("");
                kilometers.setText("");
                location.setText("");
                date.setText("");
                fuel.setText("");
                delete.setVisibility(View.INVISIBLE);
            }

        }
        catch (Exception err){
            delete.setVisibility(View.INVISIBLE);
            err.printStackTrace();
        }
    }

    @Override
    public void onNothingSelected() {

    }
}
