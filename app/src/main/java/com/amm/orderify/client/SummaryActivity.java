package com.amm.orderify.client;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.amm.orderify.R;
import com.amm.orderify.helpers.data.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static com.amm.orderify.helpers.JBDCDriver.getConnection;

public class SummaryActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.client_summary_activity);

        int tableID = 1;

        List<Order> orders = new ArrayList<>();
        List<Wish> wishes = new ArrayList<>();
        List<Addon> addons = new ArrayList<>();

        try {
            Statement ordersS = getConnection().createStatement();
            ResultSet ordersRS = ordersS.executeQuery("SELECT * FROM orders\n" +
                    "WHERE tableID = " + tableID);
            while (ordersRS.next()){
                Statement wishesS = getConnection().createStatement();
                ResultSet wishesRS = wishesS.executeQuery("SELECT dishID, name, price, amount, orderID FROM wishes\n" +
                                                               "JOIN dishes ON dishes.ID = wishes.dishID\n" +
                                                               "WHERE orderID = " + ordersRS.getInt("ID"));
                while (wishesRS.next()){
                    Statement addonsS = getConnection().createStatement();
                    ResultSet addonsRS = addonsS.executeQuery("SELECT addonID, name, price FROM addonsToWishes\n" +
                            "JOIN addons ON addons.ID = addonsToWishes.addonID\n" +
                            "WHERE wishID = " + wishesRS.getInt("ID"));
                    while (addonsRS.next()){
                        addons.add(new Addon(addonsRS.getInt("addonID"), addonsRS.getString("name"), addonsRS.getFloat("price")));
                    }
                    Dish dish = new Dish(wishesRS.getInt("dishID"), wishesRS.getString("name"), wishesRS.getFloat("price"), null, null, null);
                    wishes.add(new Wish(dish, wishesRS.getInt("amount"), addons));
                    addons = new ArrayList<>();
                }
                orders.add(new Order(ordersRS.getInt("ID"), null, null, ordersRS.getInt("tableID"), ordersRS.getString("comments"), ordersRS.getInt("state"), wishes));
                wishes = new ArrayList<>();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }



        //====================================================================

        //do przerobienia!!!!!!!!!!!

        //====================================================================
        //update z 22/08/16:00
        //nie gwarantuje ze dane sa poprawne, przejrzę w wolnej chwili

        //====================================================================


    }
}
