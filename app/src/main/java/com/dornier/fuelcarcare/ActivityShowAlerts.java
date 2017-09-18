package com.dornier.fuelcarcare;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class ActivityShowAlerts extends AppCompatActivity {

    ModelVehicle selectedVehicle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_alerts);

        int selectedVehicleIndex = (int) getIntent().getExtras().getLong("index");
        selectedVehicle = ModelDataManager.getInstance().getVehicles().get(selectedVehicleIndex);

        selectedVehicle.sortAlerts();
        String[] options = getAlertsToShow();

        ListView listView = (ListView) findViewById(R.id.ShowAlertsListView);

        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, options);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view,final int i, long l) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityShowAlerts.this);
                builder.setCancelable(true);
                builder.setTitle("Atenção");
                builder.setMessage("Deseja mesmo excluir o alerta selecionado?");
                builder.setPositiveButton("Sim",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                onConfirmDelete(i);
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

    String[] getAlertsToShow(){
        ArrayList<ModelMaintenanceAlert> alerts = selectedVehicle.getFilteredAlerts();
        String[] toReturn = new String[alerts.size()];
        for(int i = 0; i < alerts.size(); i++){
            ModelMaintenanceAlert temp = alerts.get(i);
            toReturn[i] =
                    "\n" +
                    "Item: " + temp.getItem() + "\n" +
                    "Kilometragem: " + temp.getKilometers() + "\n" +
                    "Data: " + ModelDataManager.dateToString(temp.getMaintenance_date()) + "\n\n" +
                    "Remover" + "\n";
        }
        return toReturn;
    }

    public void onConfirmDelete(int i){
        ModelMaintenanceAlert alert = selectedVehicle.getFilteredAlerts().get(i);
        alert.setStatus("-1");
        ModelDataManager.getInstance().addOrUpdateMaintenance(selectedVehicle,alert);
        finish();
    }



}
