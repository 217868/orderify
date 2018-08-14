package com.amm.orderify;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.amm.orderify.helpers.*;


import org.w3c.dom.Text;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.amm.orderify.helpers.JBDCDriver.*;

public class MenuActivity2 extends AppCompatActivity
{
    public ListView orderListView;
    public ListView menuListView;
    public android.support.v7.widget.GridLayout addonCategoriesGridLayout;
    public int marginCopy;

    public List<String> names = new ArrayList<>();
    public List<String> prices = new ArrayList<>();
    public List<String> amounts = new ArrayList<>();

    public int orderID = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu2);

        List<String> SaucesList = new ArrayList<>();
        SaucesList.add("Czosnkowy");
        SaucesList.add("Koperkowy");
        SaucesList.add("Ostry");
        SaucesList.add("Pomidorowy");

        List<String> SizesList = new ArrayList<>();
        SizesList.add("Duży");
        SizesList.add("Mały");
        SizesList.add("Średni");

        List<String> MeatsList = new ArrayList<>();
        MeatsList.add("Surowe");
        MeatsList.add("Słabo wysmażone");
        MeatsList.add("Średnio wysmażone");
        MeatsList.add("Mocno wysmażone");

        List<String> SaladsList = new ArrayList<>();
        SaladsList.add("Z ogórka");
        SaladsList.add("Z kapusty");
        SaladsList.add("Z małych dzieci");
        SaladsList.add("Z chuj wie czego");

        List<List<String>> AddonsLists = new ArrayList<>();
        AddonsLists.add(SaucesList);
        AddonsLists.add(SaladsList);
        AddonsLists.add(MeatsList);
        AddonsLists.add(SizesList);

        List<String> CategoryNames = new ArrayList<>();
        CategoryNames.add("Sosy");
        CategoryNames.add("Sałatki");
        CategoryNames.add("Mięsa");
        CategoryNames.add("Rozmiar");


        try {
            ResultSet resultSet = ExecuteQuery("SELECT dishes.name AS name, max(wishes.amount) AS amount, sum(dishes.price) AS dishPrice, sum(addons.price) AS addonsPrice\n" +
                    "FROM addonsToWishes\n" +
                    "JOIN addons ON addons.ID = addonsToWishes.addonID\n" +
                    "RIGHT JOIN wishes ON wishes.ID = addonsToWishes.wishID\n" +
                    "JOIN dishes ON dishes.ID = wishes.dishID\n" +
                    "WHERE orderID = " + orderID + "\n" +
                    "GROUP BY dishes.name;");

            while (resultSet.next()) {
                names.add(resultSet.getString("name"));
                prices.add(String.valueOf(String.format(Locale.FRANCE, "%.2f", resultSet.getFloat("dishPrice") + resultSet.getFloat("addonsPrice"))) + " zł");
                amounts.add(resultSet.getString("amount"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //====================ORDER LIST=================================
        orderListView = findViewById(R.id.OrderListView);
        ViewGroup.LayoutParams lp = orderListView.getLayoutParams();
        ViewGroup.LayoutParams lpb = lp;
        if (names.size() <= 3) lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        orderListView.setLayoutParams(lp);
        orderListView.setAdapter(new customAdapter(names, prices));

        //=====================MENU LIST====================================
        menuListView = findViewById(R.id.MenuListView);

        List<Object> menuList = new ArrayList<>();
        menuList.add(new String ("Dania główne"));
        menuList.add(new WishItem("Szparagi", "25,00 zł"));
        menuList.add(new WishItem("Kotlet schabowy", "18,00 zł"));
        menuList.add(new WishItem("Kurczak w cieście", "20,00 zł"));
        menuList.add(new String ("Zupy"));
        menuList.add(new WishItem("Ogórkowa", "12,00 zł"));
        menuList.add(new WishItem("Rosół", "10,00 zł"));


        // podgląd - GridAdapter (Context context, List<ListView> listViews, List<String> categoryNames, List<List<String>> listViewsLists)





        menuListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                try
                {
                    ConstraintLayout menuExpand = view.findViewById(R.id.MenuExpand);

                    ConstraintLayout.MarginLayoutParams params = (ConstraintLayout.MarginLayoutParams) menuExpand.getLayoutParams();
                    if (params.bottomMargin == 0)
                    {
                        params.bottomMargin = marginCopy;
                    } else
                    {
                        marginCopy = params.bottomMargin;
                        params.bottomMargin = 0;
                    }
                    menuExpand.setLayoutParams(params);
                }
                catch(Exception e)
                {

                }
            }
        });

        menuListView.setAdapter(new customMenuAdapter(this, menuList, AddonsLists, CategoryNames));



    }

    class customAdapter extends BaseAdapter {

        List<String> names = null;
        List<String> prices = null;

        customAdapter(List<String> val1, List<String> val2) {
            names = val1;
            prices = val2;

        }

        public int getCount() {
            return names.size();
        }

        public Object getItem(int arg0) {
            return null;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.order_list_element, null);
            TextView nameTextView = convertView.findViewById(R.id.orderNameTextView);
            TextView priceTextView = convertView.findViewById(R.id.orderPriceTextView);
            nameTextView.setText(names.get(position));
            priceTextView.setText(prices.get(position));
            return convertView;
        }
    }


    class customMenuAdapter extends BaseAdapter
    {
        List<Object> menuList = null;

        private static final int MENU_ITEM = 0;
        private static final int HEADER = 1;
        private LayoutInflater menuInflater;
        private List<List<String>> ListViewLists;
        private List<String> AddonsCategoryList;


        customMenuAdapter(Context context, List<Object> menuList, List<List<String>> ListViewsLists, List<String> AddonCategoryList)
        {
            this.menuList = menuList;
            menuInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.ListViewLists = ListViewsLists;
            this.AddonsCategoryList = AddonCategoryList;
        }

        @Override
        public int getItemViewType(int i)
        {
            if (menuList.get(i) instanceof WishItem)
            {
                return MENU_ITEM;
            }
            else
            {
                return HEADER;
            }
        }

        @Override
        public int getViewTypeCount()
        {
            return 2;
        }

        @Override
        public int getCount()
        {
            return menuList.size();
        }

        @Override
        public Object getItem(int i)
        {
            return menuList.get(i);
        }

        @Override
        public long getItemId(int i)
        {
            return 1;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup)
        {

            if (view == null)
            {
                switch (getItemViewType(i))
                {
                    case MENU_ITEM:
                        view = menuInflater.inflate(R.layout.menu_list_element, null);
                        break;

                    case HEADER:
                        view = menuInflater.inflate(R.layout.menu_list_header, null);
                        break;

                }
            }

            switch (getItemViewType(i))
            {
                case MENU_ITEM:
                    TextView nameTextView = view.findViewById(R.id.NameTextView);
                    TextView priceTextView = view.findViewById(R.id.PriceTextView);

                    //GridAdapter gridAdapter = new GridAdapter(MenuActivity2.this, AddonsCategoryList, ListViewLists);

                    addonCategoriesGridLayout = view.findViewById(R.id.AddonCategoriesGridLayout);
                    LayoutInflater gridInflater = getLayoutInflater();
                    addonCategoriesGridLayout.removeAllViews();

                    for (int gridNumber = 0; gridNumber < ListViewLists.size(); gridNumber++)
                    {
                        View v = gridInflater.inflate(R.layout.expand_grid_element, null);
                        TextView CategoryNameTextView = v.findViewById(R.id.CategoryNameTextView);
                        LinearLayout AddonsLinearLayout = v.findViewById(R.id.AddonsLinearLayout);
                        CategoryNameTextView.setText(AddonsCategoryList.get(gridNumber));
                        for (int listNumber = 0; listNumber < (ListViewLists.get(gridNumber)).size(); listNumber++ )
                        {
                            View x = gridInflater.inflate(R.layout.expand_addon_list_element, null);
                            TextView AddonNameTextView = x.findViewById(R.id.AddonNameTextView);
                            AddonNameTextView.setText(ListViewLists.get(gridNumber).get(listNumber));
                            AddonsLinearLayout.addView(x);

                        }
                        addonCategoriesGridLayout.addView(v);

                    }


                    priceTextView.setText( ( (WishItem)menuList.get(i) ).getWishPrice()  );
                    nameTextView.setText( ( (WishItem)menuList.get(i) ).getWishName()  );
                    break;

                case HEADER:
                    TextView headerTextView = view.findViewById(R.id.HeaderTextView);

                    headerTextView.setText( (String)menuList.get(i) );
                    break;



            }
            return view;
        }
    }
    public class WishItem
    {
        private String name;
        private String price;

        public WishItem (String name, String price)
        {
            this.name = name;
            this.price = price;

        }
        public String getWishName()
        {
            return this.name;
        }
        public String getWishPrice()
        {
            return this.price;
        }


    }


}

