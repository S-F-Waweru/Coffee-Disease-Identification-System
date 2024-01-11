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

import java.util.ArrayList;

public class DiseasesRVAdapter extends RecyclerView.Adapter<DiseasesRVAdapter.ViewHolder> {

    private ArrayList<DiseaseModal> diseaseModalArrayList;
    private Context context;

    int lastPos = -1;

    private DiseaseClickInterface diseaseClickInterface;
//  constructor

    public DiseasesRVAdapter(ArrayList<DiseaseModal> diseaseModalArrayList, Context context, DiseaseClickInterface diseaseClickInterface) {
        this.diseaseModalArrayList = diseaseModalArrayList;
        this.context = context;
        this.diseaseClickInterface = diseaseClickInterface;


    }

    @NonNull
    @Override
    public DiseasesRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.disease_rv_item, parent, false);
        return  new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DiseasesRVAdapter.ViewHolder holder, int position) {
//        set the data in the rv_item

        DiseaseModal diseaseModal = diseaseModalArrayList.get(position);
        holder.diseaseName.setText(diseaseModal.getDiseaseName());

        setAnimation(holder.itemView, position);

//        when the item is clicked
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int adapterPosition = holder.getAdapterPosition();

                if(adapterPosition != RecyclerView.NO_POSITION){
                    diseaseClickInterface.onDiseaseClick(adapterPosition);
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return diseaseModalArrayList.size();
    }




// animation
    private void setAnimation(View itemView, int position){
        if(position > lastPos){
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            itemView.setAnimation(animation);
            lastPos = position;
        }

    }




    //    interface
    public interface  DiseaseClickInterface{
        void onDiseaseClick(int position);
    }




//View holder
    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView diseaseName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            diseaseName = itemView.findViewById(R.id.idTVDiseaseName);
        }
    }
}
