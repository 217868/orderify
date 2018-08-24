package com.amm.orderify.helpers.data;

import com.amm.orderify.R;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Order {
    public int id;
    public Date time;
    public Date date;
    public String comments;
    public int state;
    public int clientID;
    public int tableID;
    public List<Wish> wishes;

    public Order(int id, Date time, Date date, String comments, int state, int clientID, int tableID, List<Wish> wishes) {
        this.id = id;
        this.time = time;
        this.date = date;
        this.comments = comments;
        this.state = state;
        this.clientID = clientID;
        this.tableID = tableID;
        this.wishes = wishes;
    }

    public float getTotalPrice(){
        float totalPrice = 0;

        for (int wishNumber = 0; wishNumber < wishes.size(); wishNumber++)
            totalPrice += wishes.get(wishNumber).getTotalPrice();

        return totalPrice;
    }

    public String getState(){
        String state;
        switch (this.state){
            case 1: state = String.valueOf(R.string.lifecycle_order_preparation); break;
            case 2: state = String.valueOf(R.string.lifecycle_order_delivered); break;
            case 3: state = String.valueOf(R.string.lifecycle_order_payment); break;
            case 4: state = String.valueOf(R.string.lifecycle_order_paid); break;
            default: state = "HEART BROKEN - contact dev!";
        }
        return state;
    }

    public String getWaitingTime(){
        Date curr = Calendar.getInstance().getTime();
        Date orderTime = new Date(this.date.getTime() + this.time.getTime());
        Date diff = new Date(curr.getTime() - orderTime.getTime() + 7200000);
        String seconds = String.format("%02d", (int) (diff.getTime() / 1000) % 60);
        String minutes = String.format("%02d", (int) ((diff.getTime() / (1000 * 60)) % 60));
        String hours = String.format("%02d", (int) ((diff.getTime() / (1000 * 60 * 60)) % 24));
        String days = String.valueOf((int) ((diff.getTime() / (1000 * 60 * 60 * 24))));
        return days + " days, " + hours + ":" + minutes + ":" + seconds;
    }
}