package com.example.coffeediasfp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecommendationRVAdapter extends RecyclerView.Adapter<RecommendationRVAdapter.ViewHolder> {

    private ArrayList<RecommendationModal>recommendationModalArrayList;
    private Context context;
    int lastPos = -1;

    private RecommendationClickInterface recommendationClickInterface;

    public RecommendationRVAdapter(ArrayList<RecommendationModal> recommendationModalArrayList, Context context, RecommendationClickInterface recommendationClickInterface) {
        this.recommendationModalArrayList = recommendationModalArrayList;
        this.context = context;
        this.recommendationClickInterface = recommendationClickInterface;
    }

    @NonNull
    @Override
    public RecommendationRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recommendation_rv_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecommendationRVAdapter.ViewHolder holder, int position) {
//        setdata
        RecommendationModal recommendationModal = recommendationModalArrayList.get(position);
        holder.recommendationTV.setText(recommendationModal.getRecommendationText());

//         set Animation
        setAnimation(holder.itemView, position);
//holder.itemView.setOnClickListener(new View.OnClickListener() {
//    @Override
//    public void onClick(View view) {
//        int adapterPosition = holder.getAdapterPosition();
//        if(adapterPosition != RecyclerView.NO_POSITION){
//            recommendationClickInterface.onRecommendationClick(adapterPosition);
//        }
//    }
//});
    }

    @Override
    public int getItemCount() {
        return recommendationModalArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView recommendationTV;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            recommendationTV = itemView.findViewById(R.id.idTVrecommendationName);
        }
    }
    private void setAnimation(View itemView, int position){
        if(position > lastPos){
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            itemView.setAnimation(animation);
            lastPos = position;
        }

    }

    public  interface  RecommendationClickInterface{
        void onRecommendationClick(int position);
    }
}
