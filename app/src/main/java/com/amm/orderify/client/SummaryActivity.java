package com.amm.orderify.client;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

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
    LinearLayout orderListLinearLayout;
    List<Order> orders = new ArrayList<>();
    List<Wish> wishes = new ArrayList<>();
    List<Addon> addons = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.client_summary_activity);

        int tableID = 1;


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

        createOrderList();



        //====================================================================

        //do przerobienia!!!!!!!!!!!

        //====================================================================
        //update z 22/08/16:00
        //nie gwarantuje ze dane sa poprawne, przejrzę w wolnej chwili

        //====================================================================


    }
    public void createOrderList()
    {
        if(orderListLinearLayout != null) orderListLinearLayout.removeAllViews();
        orderListLinearLayout = findViewById(R.id.WishListLinearLayout);

        for (int orderNumber = 0; orderNumber < orders.size(); orderNumber++)
        {
            final Order order = orders.get(orderNumber);
            View orderElement = getLayoutInflater().inflate(R.layout.client_summary_order_element, null);

            TextView orderNumberTextView = orderElement.findViewById(R.id.OrderNumberTextView);
            orderNumberTextView.setText(order.id + "");

            TextView orderStateTextView = orderElement.findViewById(R.id.OrderStateTextView);
            orderStateTextView.setText(order.state + "");

            TextView orderSumNumberTextView = orderElement.findViewById(R.id.OrderSumNumberTextView);
            orderSumNumberTextView.setText(order.getTotalPrice() + "");

            LinearLayout wishListLinearLayout = orderElement.findViewById(R.id.WishListLinearLayout);

            for (int wishNumber = 0; wishNumber < order.wishes.size(); wishNumber++)
            {
                final Wish wish = order.wishes.get(wishNumber);

                View wishElement = getLayoutInflater().inflate(R.layout.client_summary_wish_element, null);
                TextView wishNameTextView = wishElement.findViewById(R.id.WishNameTextView);
                TextView wishPriceTextView = wishElement.findViewById(R.id.WishPriceTextView);

                wishNameTextView.setText(wish.dish.name);
                wishPriceTextView.setText(wish.getTotalPrice() + "");

                wishListLinearLayout.addView(wishElement);
            }
            orderListLinearLayout.addView(orderElement);
        }

    }
}
