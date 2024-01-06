package com.example.coffeediasfp;

public interface OnItemClickListener {
    void onItemClick(String cardName, FarmFieldModal farmFieldModal);

    // for the toolbat
    void onToolbarMenuItemClick(int menuItemID, FarmFieldModal farmFieldModal);
}
