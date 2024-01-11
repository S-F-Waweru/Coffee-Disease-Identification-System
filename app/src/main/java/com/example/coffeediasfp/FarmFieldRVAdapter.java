package com.example.coffeediasfp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

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


    }

    @Override
    public int getItemCount() {
//        Return the size of te array list
        return farmFieldModalArrayList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView farmNameTV, farmSizeTV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            farmNameTV = itemView.findViewById(R.id.idTVfarmName);
            farmSizeTV = itemView.findViewById(R.id.idTVFarmSize);

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

}
