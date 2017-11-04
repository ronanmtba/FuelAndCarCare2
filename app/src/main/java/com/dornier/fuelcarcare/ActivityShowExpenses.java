package com.dornier.fuelcarcare;

import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.ViewGroup.LayoutParams;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ActivityShowExpenses extends AppCompatActivity implements OnChartValueSelectedListener {
    ModelVehicle selectedVehicle;
    JSONArray monthsAndData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_expenses);
        ModelDataManager.getInstance().setActualContext(ActivityShowExpenses.this);

        selectedVehicle = ModelDataManager.getInstance().getVehicles().get((int) getIntent().getExtras().getLong("index"));
        selectedVehicle.sortExpenses();

        LineChart lineChart = (LineChart) findViewById(R.id.ShowExpensesChart);

        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<String>();

        monthsAndData = expensesAndValues(selectedVehicle.getExpenses());

        try {

            for (int i = 0; i < monthsAndData.length(); i++) {
                JSONObject obj = (JSONObject) monthsAndData.get(i);
                entries.add(new Entry(((Double)obj.get("value")).floatValue(), i, obj.get("expenses")));
                Date date = (Date)obj.get("date");
                String dateInString = ModelDataManager.dateToString(date).substring(3);
                labels.add(dateInString);
            }
        }
        catch (JSONException e){

        }

        LineDataSet dataset = new LineDataSet(entries, "Gasto mensal");
        LineData data = new LineData(labels, dataset);

        dataset.setDrawFilled(true);

        lineChart.setDescription("");
        lineChart.setData(data);
        lineChart.animateY(2000);
        lineChart.setOnChartValueSelectedListener(this);
    }



    public JSONArray expensesAndValues(ArrayList<ModelExpense> expenses){
        try {
            JSONArray toReturn = new JSONArray();
            JSONObject item = new JSONObject();

            for(int i = 0; i < expenses.size(); i++){
                if((expenses.get(i).getStatus()) >= 0) {
                    if(item.length() == 0){
                        item.put("value", expenses.get(i).getPrice());
                        item.put("date", expenses.get(i).getDate());
                        item.put("expenses", new ArrayList<ModelExpense>());
                        ((ArrayList<ModelExpense>) item.get("expenses")).add(expenses.get(i));
                    }
                    else if (
                            (expenses.get(i).getDate().getMonth() == ((Date)item.get("date")).getMonth())
                                    &&
                                    (expenses.get(i).getDate().getYear() == ((Date)item.get("date")).getYear())
                            ) {
                        ((ArrayList<ModelExpense>) item.get("expenses")).add(expenses.get(i));
                        item.put("value", (double) item.get("value") + expenses.get(i).getPrice());
                    } else {
                        toReturn.put(item);
                        item = new JSONObject();

                        item.put("value", expenses.get(i).getPrice());
                        item.put("date", expenses.get(i).getDate());
                        item.put("expenses", new ArrayList<ModelExpense>());
                        ((ArrayList<ModelExpense>) item.get("expenses")).add(expenses.get(i));
                    }
                }
            }
            toReturn.put(item);
            return toReturn;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;


    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        View linearLayout =  findViewById(R.id.ShowExpensesChartLL);
        ((LinearLayout)linearLayout).removeAllViews();

        ArrayList<ModelExpense> expenses = (ArrayList<ModelExpense>) e.getData();
        for(final ModelExpense expense: expenses){
            printOnScreen("\n",linearLayout);
            printOnScreen("Nome: " + expense.getComponentName(),linearLayout);
            printOnScreen("Quantidade: " + expense.getQuantity(), linearLayout);
            printOnScreen("Valor final: " + expense.getPrice(),linearLayout);
            printOnScreen("Data: " + ModelDataManager.dateToString(expense.getDate()),linearLayout);

            //Creates remove button
            Button buttonRemove = new Button(this);
            buttonRemove.setText("remover");
            buttonRemove.setLayoutParams(new LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            ((LinearLayout)linearLayout).addView(buttonRemove);
            buttonRemove.setTextColor(Color.parseColor("#ffffff"));
            buttonRemove.setBackgroundColor(Color.parseColor("#0a445c"));
            buttonRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ActivityShowExpenses.this);
                    builder.setCancelable(true);
                    builder.setTitle("Atenção");
                    builder.setMessage("Deseja mesmo excluir a despesa selecionada?");
                    builder.setPositiveButton("Sim",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    onConfirmDelete(expense);
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
            });
        }
    }





    public void onConfirmDelete(ModelExpense expense){
        expense.setStatus(-1);
        ModelDataManager.getInstance().addOrUpdateExpense(selectedVehicle,expense);
        finish();
    }

    public void printOnScreen(String text, View view){
        TextView valueTV = new TextView(this);
        valueTV.setText(text);
        valueTV.setTextColor(Color.BLACK);
        valueTV.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));

        ((LinearLayout) view).addView(valueTV);
    }

    @Override
    public void onNothingSelected() {

    }
}
