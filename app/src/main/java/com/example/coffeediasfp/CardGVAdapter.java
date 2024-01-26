package com.example.coffeediasfp;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
// button



import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CardGVAdapter  extends ArrayAdapter <CardModal> {

    public  CardGVAdapter (@NonNull Context context, ArrayList<CardModal> cardModalArrayList){
        super(context, 0, cardModalArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        ViewHolder holder;
        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.home_card, parent, false);

            holder = new ViewHolder();
            holder.cardTV = convertView.findViewById(R.id.TVcard);
            holder.cardIV = convertView.findViewById(R.id.IVimage);
            holder.cardBT = convertView.findViewById(R.id.BTcard);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        CardModal cardModal = getItem(position);
        if ( cardModal != null){
            holder.cardTV.setText(cardModal.getCard_name());
            holder.cardIV.setImageResource(cardModal.getImgid());
            holder.cardBT.setText(cardModal.getButton_name());


//            Set onclicklistener for the button
            holder.cardBT.setOnClickListener(view -> {
                //handle buttuon click here
                Context context = getContext();
                Intent intent = new Intent(context, cardModal.getTargetActivity());
                context.startActivity(intent);
            });
        }


     return  convertView;
    }

    public static class ViewHolder{
        TextView cardTV;
        ImageView cardIV;
        Button cardBT;
    }
}
