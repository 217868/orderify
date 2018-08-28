package com.amm.orderify.maintenance.editors;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.amm.orderify.R;
import com.amm.orderify.helpers.data.AddonCategory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.amm.orderify.helpers.JBDCDriver.*;

public class EditAddonCategoriesActivity extends AppCompatActivity {

    LinearLayout addonCategoriesLinearLayout;

    Switch multiChoiceToggleButton;
    EditText nameEditText;
    EditText descriptionEditText;

    Button actionButton;
    Button cancelButton;

    int editedAddonCategoryID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maintenance_edit_addoncategories_activity);

        addonCategoriesLinearLayout = findViewById(R.id.AddonCategoryLinearLayout);

        multiChoiceToggleButton = findViewById(R.id.MultiChoiceToggleButton);
        nameEditText = findViewById(R.id.NameEditText);
        descriptionEditText = findViewById(R.id.DescriptionEditText);

        updateAddonCategoryList(getAddonCategories());

        actionButton = findViewById(R.id.ActionButton);
        actionButton.setOnClickListener(e -> {
            if(editedAddonCategoryID == 0) {
                try {
                    ExecuteUpdate("INSERT INTO addonCategories (name, description, multiChoice)\n" +
                            "VALUES ('" + nameEditText.getText() + "', '" + descriptionEditText.getText() + "', " + multiChoiceToggleButton.isChecked() + ")");
                    Toast.makeText(this, "AddonCategory added!", Toast.LENGTH_SHORT).show();
                    updateAddonCategoryList(getAddonCategories());
                } catch (SQLException d) { Log.wtf("SQL Exception", d.getMessage()); }
            } else {
                try {
                    ExecuteUpdate("UPDATE addonCategories SET " + "name = '" + nameEditText.getText() + "', " + "description = '" + descriptionEditText.getText() + "', " +
                            "multiChoice = " + multiChoiceToggleButton.isChecked() + " WHERE ID = " + editedAddonCategoryID);
                    Toast.makeText(this, "AddonCategory edited!", Toast.LENGTH_SHORT).show();
                    cancelButton.callOnClick();
                    updateAddonCategoryList(getAddonCategories());
                } catch (SQLException d) { Log.wtf("SQLException", d.getMessage()); }
            }
        });

        cancelButton = findViewById(R.id.CancelButton);
        cancelButton.setVisibility(View.GONE);
        cancelButton.setOnClickListener(e -> {
            nameEditText.setText("");
            descriptionEditText.setText("");
            multiChoiceToggleButton.setChecked(false);
            editedAddonCategoryID = 0;
            cancelButton.setVisibility(View.GONE);
            actionButton.setText("Add addonCategory");
        });
    }


    private List<AddonCategory> getAddonCategories(){
        List<AddonCategory> addonCategories = new ArrayList<>();
        try {
            ResultSet addonCategoriesRS = ExecuteQuery("SELECT * FROM addonCategories");
            while (addonCategoriesRS.next())
                addonCategories.add(new AddonCategory(addonCategoriesRS.getInt("ID"), addonCategoriesRS.getString("name"), addonCategoriesRS.getString("description"), addonCategoriesRS.getBoolean("multiChoice"), null));
        } catch (SQLException ignored) {}
        return addonCategories;
    }

    public void updateAddonCategoryList(List<AddonCategory> addonCategories) {
        addonCategories.sort(Comparator.comparing(object -> String.valueOf(object.name)));
        addonCategoriesLinearLayout.removeAllViews();
        for (int addonCategoryNumber = -1; addonCategoryNumber < addonCategories.size(); addonCategoryNumber++){

            View addonCategoryElement = getLayoutInflater().inflate(R.layout.maintenance_element_addoncategory, null);
            TextView idTextView = addonCategoryElement.findViewById(R.id.IdTextView);
            TextView nameTextView = addonCategoryElement.findViewById(R.id.NameTextView);
            TextView descriptionTextView = addonCategoryElement.findViewById(R.id.DescriptionTextView);
            TextView multiChoiceTextView = addonCategoryElement.findViewById(R.id.MultiChoiceTextView);
            ImageButton editButton = addonCategoryElement.findViewById(R.id.EditButton);
            ImageButton deleteButton = addonCategoryElement.findViewById(R.id.DeleteButton);

            if(addonCategoryNumber == -1) {
                idTextView.setText("ID");
                nameTextView.setText("NAME");
                descriptionTextView.setText("DESCRIPTION");
                multiChoiceTextView.setText("MULTI");
                editButton.setImageAlpha(1);
                deleteButton.setImageAlpha(1);
                addonCategoriesLinearLayout.addView(addonCategoryElement);
                continue;
            }

            idTextView.setText(addonCategories.get(addonCategoryNumber).getIdString());
            nameTextView.setText(addonCategories.get(addonCategoryNumber).name);
            descriptionTextView.setText(addonCategories.get(addonCategoryNumber).description);
            String yes = "YES", no = "NO";
            if (addonCategories.get(addonCategoryNumber).multiChoice) multiChoiceTextView.setText(yes);
            else multiChoiceTextView.setText(no);

            final int finalAddonCategoryNumber = addonCategoryNumber;
            editButton.setOnClickListener(v -> {
                nameEditText.setText(addonCategories.get(finalAddonCategoryNumber).name);
                descriptionTextView.setText(addonCategories.get(finalAddonCategoryNumber).description);
                multiChoiceToggleButton.setChecked(addonCategories.get(finalAddonCategoryNumber).multiChoice);
                editedAddonCategoryID = addonCategories.get(finalAddonCategoryNumber).id;
                actionButton.setText("Edit addonCategory");
                cancelButton.setVisibility(View.VISIBLE);
            });
            deleteButton.setOnClickListener(v -> {
                try {
                    //delete addons as well?
                    ExecuteUpdate("DELETE FROM addonCategories WHERE ID = " + addonCategories.get(finalAddonCategoryNumber).id);
                    addonCategories.remove(addonCategories.get(finalAddonCategoryNumber));
                    updateAddonCategoryList(getAddonCategories());
                    Toast.makeText(this, "AddonCategory deleted!", Toast.LENGTH_SHORT).show();
                } catch (SQLException e) {
                    if(e.getErrorCode() == 1451) Toast.makeText(this, "AddonCategory " + addonCategories.get(finalAddonCategoryNumber).name + " has addons assigned!", Toast.LENGTH_SHORT).show();
                }
            });

            addonCategoriesLinearLayout.addView(addonCategoryElement);
        }
    }
}
