package com.amm.orderify;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


import com.amm.orderify.structure.*;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.zip.Inflater;

import static com.amm.orderify.helpers.JBDCDriver.*;

public class MenuActivity2 extends AppCompatActivity {
    public LinearLayout orderListLinearLayout;
    public ListView menuListView;
    public android.support.v7.widget.GridLayout addonCategoriesGridLayout;
    public int marginCopy;
    public int activeMenuElementNumber = -1;

    public List<String> names = new ArrayList<>();
    public List<String> prices = new ArrayList<>();
    public List<String> amounts = new ArrayList<>();

    public int orderID = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu2);


//        List<Addon> addonsOLIST = new ArrayList<>();
//        List<Wish> wishesOLIST = new ArrayList<>();

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
        orderListLinearLayout = findViewById(R.id.OrderListLinearLayout);


        //=====================MENU LIST====================================
        menuListView = findViewById(R.id.MenuListView);

        //DODANE NR 1 POCZĄTEK !!!!!!!!!!!!!!!!!!!!!!!
//        menuListView.setRecyclerListener(new AbsListView.RecyclerListener() {
//            @Override
//            public void onMovedToScrapHeap(View view) {
//                try
//                {
//                    ConstraintLayout cl = view.findViewById(R.id.MenuExpand);
//                    cl.setVisibility(View.GONE);
//                }
//                catch(Exception e)
//                {
//                    Log.wtf("Error", "Cos nie tak");
//                }
//            }
//        });
        //DODANE NR 1 KONIEC !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

        List<Addon> addonsMENU = new ArrayList<>();
        List<AddonCategory> addonCategoryMENU = new ArrayList<>();
        List<Dish> dishesMENU = new ArrayList<>();
        List<DishCategory> dishCategoriesMENU = new ArrayList<>();


        try {
            Statement dishCategoriesS = getConnection().createStatement();
            ResultSet dishCategoriesRS = dishCategoriesS.executeQuery("SELECT * FROM dishesCategories");
            while (dishCategoriesRS.next()) {
                Statement dishesS = getConnection().createStatement();
                ResultSet dishesRS = dishesS.executeQuery("SELECT * FROM dishes \n" +
                        "WHERE categoryID = " + dishCategoriesRS.getInt("ID"));
                while (dishesRS.next()) {
                    Statement addonCategoriesS = getConnection().createStatement();
                    ResultSet addonCategoriesRS = addonCategoriesS.executeQuery("SELECT * FROM addonsCategoriesToDishes\n" +
                            "JOIN addonsCategories ON addonsCategories.ID = addonsCategoriesToDishes.addonCategoryID\n" +
                            "WHERE dishID = " + dishesRS.getInt("ID"));
                    while (addonCategoriesRS.next()) {
                        Statement addonsS = getConnection().createStatement();
                        ResultSet addonsRS = addonsS.executeQuery("SELECT * FROM addons \n" +
                                "WHERE addonCategoryID = " + addonCategoriesRS.getInt("addonCategoryID"));
                        while (addonsRS.next()) {
                            addonsMENU.add(new Addon(addonsRS.getInt("ID"), addonsRS.getString("name"), addonsRS.getFloat("price")));
                        }
                        addonCategoryMENU.add(new AddonCategory(addonCategoriesRS.getInt("ID"), addonCategoriesRS.getString("name"), addonsMENU));
                        addonsMENU = new ArrayList<>();
                    }
                    dishesMENU.add(new Dish(dishesRS.getInt("ID"), dishesRS.getString("name"),
                            dishesRS.getFloat("price"), dishesRS.getString("descS"),
                            dishesRS.getString("descL"), addonCategoryMENU));
                    addonCategoryMENU = new ArrayList<>();
                }
                dishCategoriesMENU.add(new DishCategory(dishCategoriesRS.getInt("ID"), dishCategoriesRS.getString("name"), dishesMENU));
                dishesMENU = new ArrayList<>();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }



        List<Object> menuList = new ArrayList<>();
        for(int i = 0; i < dishCategoriesMENU.size(); i++){
            menuList.add(dishCategoriesMENU.get(i));//send just category
            menuList.addAll(dishCategoriesMENU.get(i).dishes); //send item on category one by one
        }



        menuListView.setAdapter(new customMenuAdapter(this, menuList));


    }



    class customMenuAdapter extends BaseAdapter {
        List<Object> menuList = null;

        private static final int MENU_ITEM = 0;
        private static final int HEADER = 1;
        private LayoutInflater menuInflater;

        customMenuAdapter(Context context, List<Object> menuList)
        {
            this.menuList = menuList;
            menuInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public int getItemViewType(int i)
        {
            return i;

        }

        public int getItemType(int i)
        {
            if (menuList.get(i) instanceof DishCategory) return HEADER;
            else return MENU_ITEM;
        }

        @Override
        public int getViewTypeCount()
        {
            return getCount();
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
            if (view == null) {
                switch (getItemType(i)) {
                    case MENU_ITEM:
                        view = menuInflater.inflate(R.layout.menu_list_element, null);
                        break;

                    case HEADER:
                        view = menuInflater.inflate(R.layout.menu_list_header, null);
                        break;
                }
            }

            switch (getItemType(i))
            {
                case MENU_ITEM:
                    TextView nameTextView = view.findViewById(R.id.NameTextView);
                    TextView priceTextView = view.findViewById(R.id.PriceTextView);

                    priceTextView.setText(String.valueOf(((Dish)(menuList.get(i))).price));
                    nameTextView.setText(((Dish)menuList.get(i)).name);

                    ImageButton MenuBackgroundButton = view.findViewById(R.id.MenuBackgroundButton);
                    ConstraintLayout MenuExpand = view.findViewById(R.id.MenuExpand);



                    MenuBackgroundButton.setOnClickListener(v -> {
                        if(MenuExpand.getVisibility() == View.GONE)
                        {
//                                if (activeMenuElementNumber != -1)
//                                {
//                                    View vw = menuListView.getChildAt(activeMenuElementNumber);
//                                    ConstraintLayout cl = vw.findViewById(R.id.MenuExpand);
//                                    cl.setVisibility(View.GONE);
//                                    notifyDataSetChanged();
//
//                                }
//                            for (int ii = 0; i < menuList.size(); ii++)
//                            {
//                                if (getItemViewType(ii) == MENU_ITEM)
//                                {
//                                    View vw = menuListView.getChildAt(ii);
//                                    ConstraintLayout cl = vw.findViewById(R.id.MenuExpand);
//                                    cl.setVisibility(View.GONE);
//                                }
//
//                            }
                            MenuExpand.setVisibility(View.VISIBLE);
                            //activeMenuElementNumber = i;


                            notifyDataSetChanged();

                        }
                        else if (MenuExpand.getVisibility() == View.VISIBLE)
                        {
                            MenuExpand.setVisibility(View.GONE);
                            //activeMenuElementNumber = -1;
                        }
                        Log.wtf("Active element", activeMenuElementNumber + "");
                    });


                    if(MenuExpand.getVisibility() != View.GONE) {

                        addonCategoriesGridLayout = view.findViewById(R.id.AddonCategoriesGridLayout);
                        LayoutInflater gridInflater = getLayoutInflater();
                        addonCategoriesGridLayout.removeAllViews();

                        Dish d = (Dish)menuList.get(i);
                        AddonCategory ac = null;
                        Addon a = null;

                        for (int categoryIterator = 0; categoryIterator < d.addonCategories.size(); categoryIterator++) {
                            ac = d.addonCategories.get(categoryIterator);
                            View v = gridInflater.inflate(R.layout.expand_grid_element, null);
                            TextView CategoryNameTextView = v.findViewById(R.id.CategoryNameTextView);
                            LinearLayout AddonsLinearLayout = v.findViewById(R.id.AddonsLinearLayout);
                            CategoryNameTextView.setText(ac.name); //cat name
                            for (int addonIterator = 0; addonIterator < ac.addons.size(); addonIterator++ ) {
                                a = ac.addons.get(addonIterator);
                                View x = gridInflater.inflate(R.layout.expand_addon_list_element, null);
                                TextView AddonNameTextView = x.findViewById(R.id.AddonNameTextView);
                                AddonNameTextView.setText(a.name); //addon
                                AddonsLinearLayout.addView(x);
                            }
                            addonCategoriesGridLayout.addView(v);
                        }
                    }
                    break;

                case HEADER:
                    TextView headerTextView = view.findViewById(R.id.HeaderTextView);

                    headerTextView.setText(((DishCategory)menuList.get(i)).name);
                    break;



            }
            return view;
        }
    }

    void refreshOrderList (List<Wish> wishList)
    {
        orderListLinearLayout.removeAllViews();

        for (int wishNumber = 0; wishNumber < wishList.size(); wishNumber++)
        {
            LayoutInflater orderListInflater = getLayoutInflater();
            View x = orderListInflater.inflate(R.layout.order_list_element, null);
            TextView orderPriceTextView = x.findViewById(R.id.orderPriceTextView);
            TextView orderNameTextView = x.findViewById(R.id.orderNameTextView);
            ImageButton OrderCancelButton = x.findViewById(R.id.OrderCancelButton);

            OrderCancelButton.setOnClickListener(e -> {


            });

            orderPriceTextView.setText(wishList.get(wishNumber).dish.price + " zł");
            orderNameTextView.setText(wishList.get(wishNumber).dish.name);
            orderListLinearLayout.addView(x);
        }

    }







}

