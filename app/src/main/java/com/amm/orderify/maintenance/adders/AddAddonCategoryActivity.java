package com.amm.orderify.maintenance.adders;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
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
import com.amm.orderify.helpers.data.Table;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.amm.orderify.helpers.JBDCDriver.*;

public class AddAddonCategoryActivity extends AppCompatActivity {

    public LinearLayout addonCategoriesLinearLayout;
    public LayoutInflater addonCategoriesListInflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maintenance_add_addoncategory_activity);

        addonCategoriesLinearLayout = findViewById(R.id.AddonCategoryLinearLayout);
        addonCategoriesListInflater = getLayoutInflater();

        updateAddonCategoryList(getAddonCategories());

        EditText addonCategoryNameEditText = findViewById(R.id.AddonCategoryNameEditText);
        Switch multiChoiceToggleButton = findViewById(R.id.MultiChoiceToggleButton);

        Button addAddonCategoryButton = findViewById(R.id.AddAddonCategoryButton);
        addAddonCategoryButton.setOnClickListener(e -> {
            try {
                ExecuteUpdate("INSERT INTO addonCategories (name, multiChoice)\n" +
                        "VALUES ('" + addonCategoryNameEditText.getText().toString() + "', " + multiChoiceToggleButton.isChecked() + ")");
                Toast.makeText(this, "AddonCategory added!", Toast.LENGTH_SHORT).show();
                updateAddonCategoryList(getAddonCategories());
            } catch (SQLException ignored) { }
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

            View addonCategoryElement = addonCategoriesListInflater.inflate(R.layout.maintenance_addoncategory_element, null);
            TextView idTextView = addonCategoryElement.findViewById(R.id.TablesTextView);
            TextView nameTextView = addonCategoryElement.findViewById(R.id.NameTextView);
            TextView descriptionTextView = addonCategoryElement.findViewById(R.id.DescriptionTextView);
            TextView multiChoiceTextView = addonCategoryElement.findViewById(R.id.MultiChoiceTextView);
            ImageButton deleteButton = addonCategoryElement.findViewById(R.id.ActionButton);

            if(addonCategoryNumber == -1) {
                idTextView.setText("ID");
                nameTextView.setText("NAME");
                descriptionTextView.setText("DESCRIPTION");
                multiChoiceTextView.setText("MULTI");
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

            final int finaladdonCategoryNumber = addonCategoryNumber;
            deleteButton.setImageDrawable(this.getDrawable(R.drawable.bar_delete_order_button));
            deleteButton.setOnClickListener(v -> {
                try {
                    ExecuteUpdate("DELETE FROM addonCategories WHERE ID = " + addonCategories.get(finaladdonCategoryNumber).id);
                    addonCategories.remove(addonCategories.get(finaladdonCategoryNumber));
                    updateAddonCategoryList(getAddonCategories());
                    Toast.makeText(this, "AddonCategory deleted!", Toast.LENGTH_SHORT).show();
                } catch (SQLException e) {
                    if(e.getErrorCode() == 1451) Toast.makeText(this, "AddonCategory " + addonCategories.get(finaladdonCategoryNumber).name + " has addons assigned!", Toast.LENGTH_SHORT).show();
                }
            });

            addonCategoriesLinearLayout.addView(addonCategoryElement);
        }
    }
}
