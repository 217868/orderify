package com.amm.orderify.maintenance.editors;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amm.orderify.R;
import com.amm.orderify.helpers.data.DishCategory;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.amm.orderify.helpers.JBDCDriver.*;

public class EditDishCategoriesActivity extends AppCompatActivity {

    LinearLayout dishCategoriesLinearLayout;

    EditText nameEditText;

    Button actionButton;
    Button cancelButton;

    int editedDishCategoryID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maintenance_edit_dishcategories_activity);

        dishCategoriesLinearLayout = findViewById(R.id.DishCategoryLinearLayout);

        nameEditText = findViewById(R.id.NameEditText);
        updateDishCategoryList(getDishCategories());

        actionButton = findViewById(R.id.ActionButton);
        actionButton.setOnClickListener(e -> {
            if(editedDishCategoryID == 0) {
                try {
                    ExecuteUpdate("INSERT INTO dishCategories (name)\n" +
                            "VALUES ('" + nameEditText.getText().toString() + "')");
                    Toast.makeText(this, "DishCategory added!", Toast.LENGTH_SHORT).show();
                    updateDishCategoryList(getDishCategories());
                } catch (SQLException d) { Log.wtf("SQL Exception", d.getMessage()); }
            } else {
                try {
                    ExecuteUpdate("UPDATE dishCategories SET name = '" + nameEditText.getText().toString() + "' WHERE ID = " + editedDishCategoryID);
                    Toast.makeText(this, "DishCategory edited!", Toast.LENGTH_SHORT).show();
                    cancelButton.callOnClick();
                    updateDishCategoryList(getDishCategories());
                } catch (SQLException d) { Log.wtf("SQLException", d.getMessage()); }
            }
        });

        cancelButton = findViewById(R.id.CancelButton);
        cancelButton.setVisibility(View.GONE);
        cancelButton.setOnClickListener(e -> {
            nameEditText.setText("");
            editedDishCategoryID = 0;
            cancelButton.setVisibility(View.GONE);
            actionButton.setText("Add dishCategory");
        });
    }


    private ArrayMap<Integer, DishCategory> getDishCategories(){
        ArrayMap<Integer, DishCategory> dishCategories = new ArrayMap<>();
        try {
            ResultSet dishCategoriesRS = ExecuteQuery("SELECT * FROM dishCategories");
            while (dishCategoriesRS.next())
                dishCategories.put(dishCategoriesRS.getInt("ID"), new DishCategory(dishCategoriesRS.getInt("ID"), dishCategoriesRS.getString("name"), null));
        } catch (SQLException ignored) {}
        Log.wtf("ef", dishCategories.size()+"");
        return dishCategories;
    }

    public void updateDishCategoryList(ArrayMap<Integer, DishCategory> dishCategories) {
        //dishCategories.sort(Comparator.comparing(object -> String.valueOf(object.name)));
        dishCategoriesLinearLayout.removeAllViews();
        for (int dishCategoryNumber = -1; dishCategoryNumber < dishCategories.size(); dishCategoryNumber++){

            View dishCategoryElement = getLayoutInflater().inflate(R.layout.maintenance_element_dishcategory, null);
            TextView idTextView = dishCategoryElement.findViewById(R.id.IdTextView);
            TextView nameTextView = dishCategoryElement.findViewById(R.id.NameTextView);
            ImageButton editButton = dishCategoryElement.findViewById(R.id.EditButton);
            ImageButton deleteButton = dishCategoryElement.findViewById(R.id.DeleteButton);

            if(dishCategoryNumber == -1) {
                idTextView.setText("ID");
                nameTextView.setText("NAME");
                deleteButton.setImageAlpha(1);
                editButton.setImageAlpha(1);
                dishCategoriesLinearLayout.addView(dishCategoryElement);
                continue;
            }

            DishCategory dishCategory = dishCategories.valueAt(dishCategoryNumber);

            idTextView.setText(dishCategory.getIdString());
            nameTextView.setText(dishCategory.name);

            editButton.setOnClickListener(v -> {
                nameEditText.setText(dishCategory.name);
                editedDishCategoryID = dishCategory.id;
                actionButton.setText("Edit dishCategory");
                cancelButton.setVisibility(View.VISIBLE);
            });
            deleteButton.setOnClickListener(v -> {
                try {
                    ExecuteUpdate("DELETE FROM dishCategories WHERE ID = " + dishCategory.id);
                    dishCategories.remove(dishCategory.id);
                    updateDishCategoryList(getDishCategories());
                    Toast.makeText(this, "DishCategory deleted!", Toast.LENGTH_SHORT).show();
                } catch (SQLException e) {
                    if(e.getErrorCode() == 1451) Toast.makeText(this, "DishCategory " +dishCategory.name + " has dishes assigned!", Toast.LENGTH_SHORT).show();
                }
            });

            dishCategoriesLinearLayout.addView(dishCategoryElement);
        }
    }
}
