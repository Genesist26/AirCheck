package com.example.aircheck.Interface;

import com.example.aircheck.pm25.Item;

import java.util.List;

public interface IFirebaseLoadDone {
    void onFirebaseLoadSuccess(List<Item> itemList);
    void onFirebaseLoadFailed(String message);
}
