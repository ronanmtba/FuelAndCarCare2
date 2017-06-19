package com.dornier.fuelcarcare;

import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.FloatRange;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ActivityMenuVehicle extends AppCompatActivity {

    ListView listView;
    TextView vehicle_name;
    ModelVehicle selectedVehicle;
    ArrayAdapter<String> adapter;
    String[] options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_vehicle);
        ModelDataManager.getInstance().setActualContext(ActivityMenuVehicle.this);
        selectedVehicle = ModelDataManager.getInstance().getVehicles().get((int) getIntent().getExtras().getLong("index"));
        vehicle_name = (TextView) findViewById(R.id.MenuCarVehicleName);
        vehicle_name.setText(selectedVehicle.getName());
        options = new String[] {
                "Adicionar abastecimento",
                "Adicionar alerta de manutenção",
                "Adicionar despesa",
                "Histórico consumo",
                "Consultar despesas",
                "Editar veiculo",
        };

        listView = (ListView) findViewById(R.id.MenuCarListView);

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, options);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:
                        addFillUpActions();
                        break;
                    case 1:
                        addMaintenanceActions();
                        break;
                    case 2:
                        addExpenseActions();
                        break;
                    case 3:
                        showFillUpHistoryActions();
                        break;
                    case 4:
                        showExpensesActions();
                        break;
                    case 5:
                        editVehicleActions();
                }
            }
        });
    }

    private void addFillUpActions(){
        final Dialog dialog = new Dialog(ActivityMenuVehicle.this);
        dialog.setContentView(R.layout.dialog_add_fillup);
        dialog.setTitle("Adicionar abastecimento");

        // set the custom dialog components - text, image and button
        final EditText fuelPrice          = (EditText) dialog.findViewById(R.id.DialogFillUpFuelPrice);
        final EditText fillUpTotalPrice   = (EditText) dialog.findViewById(R.id.DialogFillUpTotalPrice);
        final EditText fillUpLiters       = (EditText) dialog.findViewById(R.id.DialogFillUpLiters);
        final EditText location           = (EditText) dialog.findViewById(R.id.DialogFillUpLocation);
        final EditText kilometers         = (EditText) dialog.findViewById(R.id.DialogFillUpKilometers);
        final EditText date               = (EditText) dialog.findViewById(R.id.DialogFillUpDate);
        final Spinner  fuel               = (Spinner)  dialog.findViewById(R.id.DialogFillUpFuel);
        Button   confirmButton      = (Button)   dialog.findViewById(R.id.DialogFillUpButtonOK);

        date.setText(ModelDataManager.getActualDate());

        fuelPrice.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(
                        (fuelPrice.length() > 0)
                        &&
                        (fillUpTotalPrice.length() > 0)
                 )
                fillUpLiters.setText(String.valueOf(
                        Float.parseFloat(fillUpTotalPrice.getText().toString())
                        /
                        Float.parseFloat(fuelPrice.getText().toString())
                        )
                );
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ModelFillUp fill_up = new ModelFillUp(fillUpTotalPrice.getText().toString(),
                        fuelPrice.getText().toString(),
                        fillUpLiters.getText().toString(),
                        location.getText().toString(),
                        date.getText().toString(),
                        fuel.getSelectedItem().toString(),
                        selectedVehicle.getLocal_id()+"",
                        kilometers.getText().toString());
                ModelDataManager.getInstance().addFillUp(selectedVehicle, fill_up);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void addMaintenanceActions(){
        final Dialog dialog = new Dialog(ActivityMenuVehicle.this);
        dialog.setContentView(R.layout.dialog_add_maintenance);
        dialog.setTitle("Adicionar alerta de manutenção");

        // set the custom dialog components - text, image and button
        final EditText kilometers   = (EditText) dialog.findViewById(R.id.DialogMaintenanceKilometers);
        final EditText date         = (EditText) dialog.findViewById(R.id.DialogMaintenanceDate);
        final EditText item         = (EditText) dialog.findViewById(R.id.DialogMaintenanceItem);
        Button   confirmButton      = (Button)   dialog.findViewById(R.id.DialogMaintenanceButtonOK);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void addExpenseActions(){
        final Dialog dialog = new Dialog(ActivityMenuVehicle.this);
        dialog.setContentView(R.layout.dialog_add_expense);
        dialog.setTitle("Adicionar despesa");

        // set the custom dialog components - text, image and button
        final EditText item         = (EditText) dialog.findViewById(R.id.DialogExpenseComponentName);
        final EditText date         = (EditText) dialog.findViewById(R.id.DialogExpenseDate);
        final EditText price        = (EditText) dialog.findViewById(R.id.DialogExpensePrice);
        final EditText quantity     = (EditText) dialog.findViewById(R.id.DialogExpenseQuantity);
        Button   confirmButton      = (Button)   dialog.findViewById(R.id.DialogExpenseButtonOK);

        date.setText(ModelDataManager.getActualDate());

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void showFillUpHistoryActions(){
        if(selectedVehicle.getFillUps().size() > 1) {
            Intent i = new Intent(this, ActivityShowFillUps.class);
            Bundle b = new Bundle();
            b.putLong("index", getIntent().getExtras().getLong("index"));
            i.putExtras(b);
            startActivity(i);
        }
        else{
            Toast.makeText(this,"Precisamos de ao menos 2 abastecimentos cadastrados para calcular as médias!", Toast.LENGTH_LONG).show();
        }
    }

    private void showExpensesActions(){

    }

    private void editVehicleActions(){
        final Dialog dialog = new Dialog(ActivityMenuVehicle.this);
        dialog.setContentView(R.layout.dialog_edit_vehicle);
        dialog.setTitle("Editar veiculo");

        // set the custom dialog components - text, image and button
        final EditText manufacturer = (EditText) dialog.findViewById(R.id.DialogEditVehicleManufacturer);
        final EditText model        = (EditText) dialog.findViewById(R.id.DialogEditVehicleModel);
        final EditText name         = (EditText) dialog.findViewById(R.id.DialogEditVehicleName);
        final EditText year         = (EditText) dialog.findViewById(R.id.DialogEditVehicleYear);
        final CheckBox check        = (CheckBox) dialog.findViewById(R.id.DialogEditVehiclecheckBoxRemove);
        Button confirmButton        = (Button)   dialog.findViewById(R.id.DialogEditVehicleButtonOK);

        manufacturer.setText(selectedVehicle.getManufacturer());
        model.setText(selectedVehicle.getModel());
        name.setText(selectedVehicle.getName());
        year.setText(selectedVehicle.getYear());

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedVehicle.setManufacturer(manufacturer.getText().toString());
                selectedVehicle.setModel(model.getText().toString());
                selectedVehicle.setName(name.getText().toString());
                selectedVehicle.setYear(year.getText().toString());
                if(check.isChecked())
                    selectedVehicle.setStatus("-1");
                ModelDataManager.getInstance().addOrUpdateVehicle(selectedVehicle);
                dialog.dismiss();
                if(selectedVehicle.getStatus().equals("-1")){
                    startActivity(new Intent(ActivityMenuVehicle.this, ActivitySelectVehicle.class));
                    ActivityMenuVehicle.this.finish();
                }
            }
        });

        dialog.show();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, ActivitySelectVehicle.class));
        finish();
    }
}
