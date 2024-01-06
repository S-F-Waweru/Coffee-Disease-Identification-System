package com.example.coffeediasfp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;

public class FarmFieldAdapter extends ArrayAdapter <FarmFieldModal>  {

    private  OnItemClickListener onItemClickListener;

    public FarmFieldAdapter(@NonNull Context context, ArrayList<FarmFieldModal> farmFieldModalArrayList) {
        super(context, 0, farmFieldModalArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
       ViewHolder holder;
       if(convertView == null){
           convertView = LayoutInflater.from(getContext()).inflate(R.layout.farm_field_card, parent, false);

           holder = new ViewHolder();
           holder.fieldTV = convertView.findViewById(R.id.TVfieldCard);


           convertView.setTag((holder));
       }else{
           holder = (ViewHolder) convertView.getTag();
       }
       FarmFieldModal farmFieldModal = getItem(position);
       if(farmFieldModal != null){
           holder.fieldTV.setText(farmFieldModal.getFarmName());

//           set click listener on the card
           convertView.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   if(onItemClickListener != null){
                       onItemClickListener.onItemClick(farmFieldModal.getFarmName(),farmFieldModal);
                   }
               }
           });

       }
//        handle the  toolbar here
        Toolbar toolbar = convertView.findViewById(R.id.farmfieldTB);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int ItemId = item.getItemId();
                if(onItemClickListener != null){
                    onItemClickListener.onToolbarMenuItemClick(ItemId, farmFieldModal);
                }
                return true;
            }
        });

        return convertView;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public static class ViewHolder{
        TextView fieldTV;
    }
}
