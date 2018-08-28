package com.amm.orderify.maintenance.adders;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amm.orderify.R;
import com.amm.orderify.helpers.data.Table;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.amm.orderify.helpers.JBDCDriver.*;

public class AddTableActivity extends AppCompatActivity {

    public LinearLayout tablesLinearLayout;
    static LayoutInflater tableListInflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maintenance_add_table_activity);

        tablesLinearLayout = findViewById(R.id.TableLinearLayout);
        tableListInflater = getLayoutInflater();
        updateTableList(getTables());


        EditText tableNumberEditText = findViewById(R.id.TableNumberEditText);
        EditText tableDescriptionEditText = findViewById(R.id.TableDescriptionEditText);

        Button addTableButton = findViewById(R.id.AddTableButton);
        addTableButton.setOnClickListener(v -> {
            try {
                ExecuteUpdate("INSERT INTO tables (number, description, state)\n" +
                        "VALUES  (" + tableNumberEditText.getText() + ", '" + tableDescriptionEditText.getText() + "', 1);");
                tableNumberEditText.setText("");
                tableDescriptionEditText.setText("");
                Toast.makeText(this, "Table added!", Toast.LENGTH_SHORT).show();
                updateTableList(getTables());
            } catch (SQLException d) { Log.wtf("SQL Exception", d.getMessage()); }
        });
    }

    private List<Table> getTables(){
        List<Table> tables = new ArrayList<>();
        try {
            ResultSet tablesRS = ExecuteQuery("SELECT * FROM tables");
            while (tablesRS.next())
                tables.add(new Table(tablesRS.getInt("ID"), tablesRS.getInt("number"), tablesRS.getString("description"), 1, null));
        } catch (SQLException ignored) {}
        return tables;
    }

    public void updateTableList(List<Table> tables) {
        tablesLinearLayout.removeAllViews();
        for (int tableNumber = -1; tableNumber < tables.size(); tableNumber++){
            View tableElement = tableListInflater.inflate(R.layout.maintenance_table_element, null);
            TextView idTextView = tableElement.findViewById(R.id.TablesTextView);
            TextView numberTextView = tableElement.findViewById(R.id.NumberTextView);
            TextView descriptionTextView = tableElement.findViewById(R.id.DescriptionTextView);
            ImageButton deleteButton = tableElement.findViewById(R.id.ActionButton);

            if(tableNumber == -1) {
                idTextView.setText("ID");
                numberTextView.setText("NUMBER");
                descriptionTextView.setText("DESCRIPTION");
                deleteButton.setImageAlpha(1);
                tablesLinearLayout.addView(tableElement);
                continue;
            }

            idTextView.setText(tables.get(tableNumber).getIdString());
            numberTextView.setText(tables.get(tableNumber).getPlaneNumberString());
            descriptionTextView.setText(tables.get(tableNumber).description);
            final int finalTableNumber = tableNumber;
            deleteButton.setOnClickListener(v -> {
                try {
                    ExecuteUpdate("DELETE FROM tables WHERE ID = " + tables.get(finalTableNumber).id);
                    tables.remove(tables.get(finalTableNumber));
                    Toast.makeText(this, "Table deleted!", Toast.LENGTH_SHORT).show();
                    updateTableList(getTables());
                } catch (SQLException e) {
                    if(e.getErrorCode() == 1451) Toast.makeText(this, "Table " + tables.get(finalTableNumber).number + " has order assigned!", Toast.LENGTH_SHORT).show();
                    else { Log.wtf("SQLException", e.getMessage()); }
                }
            });
            tablesLinearLayout.addView(tableElement);
        }
    }
}
