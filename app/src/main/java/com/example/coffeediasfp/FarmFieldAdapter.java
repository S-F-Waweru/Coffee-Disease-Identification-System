package com.example.coffeediasfp;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

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
           holder.addLocation = convertView.findViewById(R.id.addLocation);
           holder.viewLocation = convertView.findViewById(R.id.viewLocation);
//           holder.mapBtn = convertView.findViewById(R.id.idmap);


//           convertView.setTag((holder));
       }else{
           holder = (ViewHolder) convertView.getTag();
       }
       FarmFieldModal farmFieldModal = getItem(position);
       if(farmFieldModal != null){
           holder.fieldTV.setText(farmFieldModal.getFarmName());

//         check if location is set
           if (farmFieldModal.isLocationSet()){
               holder.addLocation.setVisibility(convertView.GONE);
               holder.viewLocation.setVisibility(convertView.VISIBLE);
               holder.viewLocation.setOnClickListener(new View.OnClickListener() {

                   @Override
                   public void onClick(View view) {
                       Log.d("ViewButtin", "onClick: The button is clicked");
                       Toast.makeText(getContext(), "View Btn Clicked", Toast.LENGTH_SHORT).show();
                       Intent intent = new Intent(getContext(), ViewFarmLocationMap.class);
                       intent.putExtra("farmModal",farmFieldModal);
                       getContext().startActivity(intent);
                   }
               });
           }else {
//               holder.viewLocation.setVisibility(convertView.GONE);
               holder.addLocation.setVisibility(View.VISIBLE);
               holder.viewLocation.setVisibility(View.GONE);
                    holder.addLocation.setOnClickListener(new View.OnClickListener() {


                        @Override
                        public void onClick(View view) {
                            Log.d("ViewButtin", "onClick: The button is clicked");
                            Toast.makeText(getContext(), "add Btn Clicked", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getContext(), SetFarmAreaMapActivity.class);
                            intent.putExtra("farmModal",farmFieldModal);
                            getContext().startActivity(intent);
                        }
                    });
           }


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
        ImageButton addLocation;
        ImageButton viewLocation;

    }

    public  boolean isServiceOK(){
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getContext());
        if(available == ConnectionResult.SUCCESS){
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog((Activity) getContext(), available, 9001);
            dialog.show();
        }else {
            Toast.makeText(getContext(), "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return true;
    }
}
