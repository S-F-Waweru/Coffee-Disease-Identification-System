package com.example.coffeediasfp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class FarmFieldRVAdapter extends RecyclerView.Adapter<FarmFieldRVAdapter.ViewHolder> {

//    Variables
    private ArrayList<FarmFieldModal> farmFieldModalArrayList;
    private Context context;
    int lastPos = -1;
    private FarmFieldClickInterface farmFieldClickInterface;

//    the constructor
    public FarmFieldRVAdapter(ArrayList<FarmFieldModal> farmFieldModalArrayList, Context context, FarmFieldClickInterface farmFieldClickInterface) {
        this.farmFieldModalArrayList = farmFieldModalArrayList;
        this.context = context;
        this.farmFieldClickInterface = farmFieldClickInterface;

//        Log.d("FarmFieldRVAdapter", "Context: " + context);
    }

    @NonNull
    @Override
    public FarmFieldRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        implement the Layout that we have created
        View view = LayoutInflater.from(context).inflate(R.layout.farm_field_rv_item,parent, false);
        return  new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FarmFieldRVAdapter.ViewHolder holder, int position) {
//        setting up the data
        FarmFieldModal farmFieldModal = farmFieldModalArrayList.get(position);
//        Log.d("FarmFieldRVAdapter", "Farm Name: " + farmFieldModal.getFarmName());

        holder.farmNameTV.setText(farmFieldModal.getFarmName());
        holder.farmSizeTV.setText(farmFieldModal.getFarmSize());
//        set animation for item
        setAnimation(holder.itemView, position);

//        click listener for the item view
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int adapterPosition = holder.getAdapterPosition();

//                ensure that the adapter position is valid before calling the click interface.
//                This check is important to avoid potential issues
//                when clicking on items that might have been removed from the adapter.
                if(adapterPosition != RecyclerView.NO_POSITION){
                    farmFieldClickInterface.onFarmFieldClick(adapterPosition);
                }
            }
        });


//         check if location is set
        if (farmFieldModal.isLocationSet()){
            holder.addLocation.setVisibility(View.GONE);
            holder.viewLocation.setVisibility(View.VISIBLE);
            holder.viewLocation.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    Log.d("ViewButtin", "onClick: The button is clicked");
                    Toast.makeText(context, "View Btn Clicked", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, ViewFarmLocationMap.class);
                    intent.putExtra("farmModal",farmFieldModal);
                    context.startActivity(intent);
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
                    Toast.makeText(context, "add Btn Clicked", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, SetFarmAreaMapActivity.class);
                    intent.putExtra("farmModal",farmFieldModal);
                    context.startActivity(intent);
                }
            });
        }


    }

    @Override
    public int getItemCount() {
//        Return the size of te array list
        return farmFieldModalArrayList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView farmNameTV, farmSizeTV;

        ImageButton addLocation;
        ImageButton viewLocation;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            farmNameTV = itemView.findViewById(R.id.idTVfarmName);
            farmSizeTV = itemView.findViewById(R.id.idTVFarmSize);
            addLocation = itemView.findViewById(R.id.addLocation);
            viewLocation = itemView.findViewById(R.id.viewLocation);

        }
    }


//    interface
    public  interface  FarmFieldClickInterface{
        void onFarmFieldClick(int position);
}

    //       animation method for each item
    private void setAnimation(View itemView, int position){
        if(position > lastPos){
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            itemView.setAnimation(animation);
            lastPos = position;
        }

    }

    public  boolean isServiceOK(){
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context);
        if(available == ConnectionResult.SUCCESS){
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog((Activity) context, available, 9001);
            dialog.show();
        }else {
            Toast.makeText(context, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

}
