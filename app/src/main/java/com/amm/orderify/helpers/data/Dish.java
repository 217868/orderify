package com.amm.orderify.helpers.data;

import android.util.SparseArray;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Dish {

    public int id;
    public String name;
    public int number;
    public float price;
    public String descS;
    public String descL;
    public int dishCategoryID;
    public List<AddonCategory> addonCategories;
    public SparseArray<AddonCategory> addonCategories2;
    public List<Addon> chosenAddons = new ArrayList<>(); //for menu handling

    public String dishCategoryName;

    public Dish(int id, int number, String name, float price, String descS, String descL, int dishCategoryID, List<AddonCategory> addonCategories) {
        this.id = id;
        this.number = number;
        this.name = name;
        this.price = price;
        this.descS = descS;
        this.descL = descL;
        this.dishCategoryID = dishCategoryID;
        this.addonCategories = addonCategories;
    }
    public Dish(int id, int number, String name, float price, String descS, String descL, int dishCategoryID, SparseArray<AddonCategory> addonCategories) {
        this.id = id;
        this.number = number;
        this.name = name;
        this.price = price;
        this.descS = descS;
        this.descL = descL;
        this.dishCategoryID = dishCategoryID;
        this.addonCategories2 = addonCategories;
    }

    public String getPriceString(){
        DecimalFormat formatter = new DecimalFormat("0.00");
        return formatter.format(this.price) + " zł";
    }
    public String getPurePriceString(){
        return  this.price + "";
    }

    public String getIdString(){
        return id+"";
    }

}

//
//        if(descS == null) this.descS = "";
//        else this.descS = descS;
//        if(descL == null) this.descL = "";
//        else this.descL = descL;