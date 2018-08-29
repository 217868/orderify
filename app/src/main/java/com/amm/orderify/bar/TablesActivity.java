package com.amm.orderify.bar;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amm.orderify.R;
import com.amm.orderify.helpers.data.*;

import java.sql.SQLException;

import static com.amm.orderify.helpers.DataManagement.*;
import static com.amm.orderify.helpers.JBDCDriver.*;

public class TablesActivity extends AppCompatActivity {

    static boolean blMyAsyncTask;
    LinearLayout tablesLinearLayout;
    UpdateTableTask task = new UpdateTableTask(TablesActivity.this);

    ArrayMap<Integer,Table> globalTables = new ArrayMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bar_tables_activity);

        Button refreshButton = findViewById(R.id.RefreshButton);
        refreshButton.setOnClickListener(v -> {
            generateTablesView();
            try {
                addNewOrdersView(getAllOrders());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        generateTablesView();
    }

    //do sortowania - funkcja która sprawdza czy jest nowy order/wezwanier kelnera/platnosc, usuwa wszystkie ordery z danego stolika i wkleja na nowo, posortowane

    private void generateTablesView() {
        globalTables = getOnlyTables();
        if(tablesLinearLayout != null) tablesLinearLayout.removeAllViews();
        tablesLinearLayout = findViewById(R.id.TablesLinearLayout);

        for (int tableNumber = 0; tableNumber < globalTables.size(); tableNumber++) {
            Table globalTable = globalTables.valueAt(tableNumber);
            globalTable.tableElement = getLayoutInflater().inflate(R.layout.bar_tables_element_table, null);
            TextView tableNumberTextView = globalTable.tableElement.findViewById(R.id.TableNumberTextView);
            TextView tableStateTextView = globalTable.tableElement.findViewById(R.id.TableStateTextView);
            Button acceptRequestButton = globalTable.tableElement.findViewById(R.id.AcceptRequestButton);
            Button freezeStateButton = globalTable.tableElement.findViewById(R.id.FreezeStateButton);
            Button expandCollapseButton = globalTable.tableElement.findViewById(R.id.ExpandCollapseButton);
            LinearLayout ordersLinearLayout = globalTable.tableElement.findViewById(R.id.OrdersLinearLayout);

            tableNumberTextView.setText(globalTable.getNumberString());
            tableStateTextView.setText(globalTable.getState());
            acceptRequestButton.setOnClickListener(v -> {
                try {
                    globalTable.state = 1;
                    for(int i = 0; i < globalTable.clients.size(); i++) globalTable.clients.valueAt(i).state = 1;
                    ExecuteUpdate("UPDATE globalTables SET state = 1 WHERE ID = " + globalTable.id);
                    ExecuteUpdate("UPDATE clients SET state = 1 WHERE tableID = " + globalTable.id);
                } catch (SQLException ignored) {}
            });
            freezeStateButton.setOnClickListener(v -> {
                try {
                    if (globalTable.state == 1) {
                        globalTable.state = 2;
                        freezeStateButton.setText(R.string.bar_tables_table_state_unfreeze_button);
                        tableStateTextView.setText(R.string.lifecycle_table_freezed);
                    } else if(globalTable.state == 2){
                        globalTable.state = 1;
                        freezeStateButton.setText(R.string.bar_tables_table_state_freeze_button);
                        tableStateTextView.setText(R.string.lifecycle_table_ready);
                    }
                    ExecuteUpdate("UPDATE globalTables SET state = " + globalTable.state + " WHERE ID = " + globalTable.id);
                } catch (SQLException ignored) { }
            });
            expandCollapseButton.setOnClickListener(v -> {
                if (ordersLinearLayout.getVisibility() == View.GONE)
                    ordersLinearLayout.setVisibility(View.VISIBLE);
                else ordersLinearLayout.setVisibility(View.GONE);
            });
            tablesLinearLayout.addView(globalTable.tableElement);
        }
    }

    private void addNewOrdersView(ArrayMap<Integer,Order> orders) {
        if(!orders.equals(new ArrayMap<>())) {
            for(int i = 0; i < orders.size(); i++){
                this.globalTables.get(orders.valueAt(i).tableID).clients.get(orders.valueAt(i).clientID).orders.put(orders.valueAt(i).id, orders.valueAt(i));
            }
        }
        for (int orderNumber = 0; orderNumber < orders.size(); orderNumber++) {
            Order order = orders.valueAt(orderNumber);

            Order globalOrder = this.globalTables.get(order.tableID).clients.get(order.clientID).orders.get(order.id);
            Table globalTable = this.globalTables.get(order.tableID);

            globalOrder.orderElement = getLayoutInflater().inflate(R.layout.bar_tables_element_order, null);

            TextView orderNumberTextView = globalOrder.orderElement.findViewById(R.id.OrderNumberTextView);
            TextView orderWaitingTimeTextView = globalOrder.orderElement.findViewById(R.id.OrderWaitingTimeTextView);
            TextView orderPriceTextView = globalOrder.orderElement.findViewById(R.id.OrderPriceTextView);
            TextView orderStateTextView = globalOrder.orderElement.findViewById(R.id.OrderStateTextView);
            TextView commentsTextView = globalOrder.orderElement.findViewById(R.id.CommentsTextView);
            ImageButton deleteOrderButton = globalOrder.orderElement.findViewById(R.id.DeleteOrderButton);
            Button changeOrderStateButton = globalOrder.orderElement.findViewById(R.id.ChangeOrderStateButton);
            LinearLayout ordersLinearLayout = globalTable.tableElement.findViewById(R.id.OrdersLinearLayout);

            runOnUiThread(() -> {
                orderNumberTextView.setText(order.getOrderNumberString());
                orderWaitingTimeTextView.setText(order.getWaitingTime());
                orderPriceTextView.setText(order.getTotalPriceString());
                orderStateTextView.setText(order.getState());
                commentsTextView.setText(order.comments);
            });

            deleteOrderButton.setOnClickListener(v -> {
                ordersLinearLayout.removeView(order.orderElement);
                this.globalTables.get(order.tableID).clients.get(order.clientID).orders.remove(order.id);
                deleteOrder(order);
            });
            if (order.state == 1) {
                runOnUiThread(() -> {
                    orderStateTextView.setText(R.string.lifecycle_order_preparation);
                    changeOrderStateButton.setText(R.string.bar_tables_order_state_prepared_button);
                    changeOrderStateButton.setVisibility(View.VISIBLE);
                });
            } else if (order.state == 2) {
                runOnUiThread(() -> {
                    orderStateTextView.setText(R.string.lifecycle_order_delivered);
                    changeOrderStateButton.setText(R.string.bar_tables_order_state_paid_button);
                    changeOrderStateButton.setVisibility(View.GONE);
                });
            } else if (order.state == 3) {
                runOnUiThread(() -> {
                    orderStateTextView.setText(R.string.lifecycle_order_payment);
                    changeOrderStateButton.setVisibility(View.VISIBLE);
                });
            } else {
                runOnUiThread(() -> {
                    orderStateTextView.setText(R.string.lifecycle_order_paid);
                    changeOrderStateButton.setVisibility(View.GONE);
                });
            }
            changeOrderStateButton.setOnClickListener(v -> {
                try {
                    if (globalOrder.state == 1) {
                        globalOrder.state = 2;
                        runOnUiThread(() -> {
                            orderStateTextView.setText(R.string.lifecycle_order_delivered);
                            changeOrderStateButton.setVisibility(View.GONE);
                            changeOrderStateButton.setText(R.string.bar_tables_order_state_paid_button);
                        });
                    } else if(globalOrder.state == 3){
                        globalOrder.state = 4;
                        runOnUiThread(() -> {
                            changeOrderStateButton.setVisibility(View.GONE);
                            orderStateTextView.setText(R.string.lifecycle_order_paid);
                        });
                    }
                    ExecuteUpdate("UPDATE orders SET state = " + globalOrder.state + " WHERE ID = " + order.id);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });

            LinearLayout wishesLinearLayout = order.orderElement.findViewById(R.id.WishesLinearLayout);
            for (int wishNumber = 0; wishNumber < order.wishes.size(); wishNumber++) {
                View wishElement = getLayoutInflater().inflate(R.layout.bar_tables_element_wish, null);
                TextView dishNameTextView = wishElement.findViewById(R.id.DishNameTextView);
                LinearLayout addonsLinearLayout = wishElement.findViewById(R.id.AddonsLinearLayout);

                Wish wish = order.wishes.valueAt(wishNumber);

                runOnUiThread(() -> dishNameTextView.setText(wish.dish.name + " x" + wish.amount));

                //wish.addons.sort(Comparator.comparing(object -> String.valueOf(object.addonCategoryID))); //sort

                for (int addonNumber = 0; addonNumber < wish.addons.size(); addonNumber++) {
                    Addon addon = wish.addons.valueAt(addonNumber);
                    View addonElement = getLayoutInflater().inflate(R.layout.bar_tables_element_addon, null);
                    TextView addonNameTextView = addonElement.findViewById(R.id.AddonNameTextView);

                    runOnUiThread(() -> {
                        addonNameTextView.setText(addon.name);
                        addonsLinearLayout.addView(addonElement);
                    });
                }
                runOnUiThread(() -> wishesLinearLayout.addView(wishElement));
            }
            runOnUiThread(() -> ordersLinearLayout.addView(globalOrder.orderElement));
        }
    }


    private void updateTablesView() {
        ArrayMap<Integer,Table> tables = getFullTablesData();
        for(int tableNumber = 0; tableNumber < tables.size(); tableNumber++) {
            Table table = tables.valueAt(tableNumber);
            Table globalTable = this.globalTables.get(table.id);
            try {
                TextView tableStateTextView = globalTable.tableElement.findViewById(R.id.TableStateTextView);
                TextView overallPriceTextView = globalTable.tableElement.findViewById(R.id.OverallPriceTextView);
                runOnUiThread(() -> tableStateTextView.setText(table.getState()));
                runOnUiThread(() -> overallPriceTextView.setText(table.getTotalPriceString()));

            } catch (Exception ignored) { }

            for (int clientNumber = 0; clientNumber < table.clients.size(); clientNumber++) {
                Client client = table.clients.valueAt(clientNumber);
                for (int orderNumber = 0; orderNumber < client.orders.size(); orderNumber++) {
                    Order order = client.orders.valueAt(orderNumber);
                    Order globalOrder = this.globalTables.get(order.tableID).clients.get(order.clientID).orders.get(order.id);
                    try{
                        Thread.sleep(10);
                        Button changeOrderStateButton = globalOrder.orderElement.findViewById(R.id.ChangeOrderStateButton);
                        TextView orderStateTextView = globalOrder.orderElement.findViewById(R.id.OrderStateTextView);
                        TextView orderWaitingTimeTextView = globalOrder.orderElement.findViewById(R.id.OrderWaitingTimeTextView);

                        this.globalTables.get(order.tableID).clients.get(order.clientID).orders.get(order.id).state = order.state;
                        if(order.state == 2) runOnUiThread(() -> {
                            orderStateTextView.setText(order.getState());
                            changeOrderStateButton.setVisibility(View.GONE); });
                        else if(order.state == 3) runOnUiThread(() -> {
                            orderStateTextView.setText(order.getState());
                            changeOrderStateButton.setText(R.string.lifecycle_order_paid);
                            changeOrderStateButton.setVisibility(View.VISIBLE); });
                        else if(order.state == 4) runOnUiThread(() -> {
                            orderStateTextView.setText(order.getState());
                            changeOrderStateButton.setVisibility(View.GONE); });

                        runOnUiThread(() -> orderWaitingTimeTextView.setText(order.getWaitingTime()));
                    } catch(Exception e){
                        Log.wtf("ex",  e.getMessage()+"");
                    }
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (blMyAsyncTask)   {
            blMyAsyncTask = false;
            task.cancel(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        task = new UpdateTableTask(TablesActivity.this);
        task.execute();
    }

    protected class UpdateTableTask extends AsyncTask<Void, Void, Void> {
        Context context;

        UpdateTableTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            blMyAsyncTask = true;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                addNewOrdersView(getAllOrders());
            } catch (Exception e) {
                e.printStackTrace();
            }
            while(true) {
                try {
                    Thread.sleep(1000);
                    updateTablesView();
                    Thread.sleep(1000);
                    addNewOrdersView(getNewOrders());

                    } catch (Exception ignored) {}
                if(Thread.interrupted()) break;
                if (!blMyAsyncTask) break;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
            blMyAsyncTask = false;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }



}