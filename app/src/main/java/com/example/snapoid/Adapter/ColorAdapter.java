package com.example.snapoid.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.snapoid.AddTextFragment;
import com.example.snapoid.R;

import java.util.ArrayList;
import java.util.List;

public class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.ColorViewholder> {

    Context context;
    List<Integer> colorList;
    ColorAdapterListener listener;

    public ColorAdapter(Context context, ColorAdapterListener listener) {
        this.context = context;
        this.colorList = genColorList();
        this.listener = listener;
    }

    public ColorAdapter(Context context, AddTextFragment addTextFragment) {
    }


    @NonNull
    @Override
    public ColorViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.color_item,parent,false);
        return new ColorViewholder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull ColorViewholder holder, int position) {

        holder.color_selection.setCardBackgroundColor(colorList.get(position));
    }


    @Override
    public int getItemCount() {
        return colorList.size();
    }

    public class ColorViewholder extends RecyclerView.ViewHolder{

      public   CardView color_selection;

        public ColorViewholder( View itemView) {
            super(itemView);
            color_selection = (CardView)itemView.findViewById(R.id.color_selection);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onColorSelected(colorList.get(getAdapterPosition()));
                }
            });
        }
    }
    private List<Integer> genColorList() {
        List<Integer> colorList = new ArrayList<>();

        colorList.add(Color.parseColor("#131722"));
        colorList.add(Color.parseColor("#403890"));
        colorList.add(Color.parseColor("#1b36eb"));
        colorList.add(Color.parseColor("#10d6a2"));
        colorList.add(Color.parseColor("#c0fff4"));
        colorList.add(Color.parseColor("#97ffff"));
        colorList.add(Color.parseColor("#ff1493"));
        colorList.add(Color.parseColor("#caff70"));

        colorList.add(Color.parseColor("#45b9d3"));
        colorList.add(Color.parseColor("#0c6483"));
        colorList.add(Color.parseColor("#487995"));
        colorList.add(Color.parseColor("#428fb9"));
        colorList.add(Color.parseColor("#a183b3"));
        colorList.add(Color.parseColor("#210333"));
        colorList.add(Color.parseColor("#99ffcc"));
        colorList.add(Color.parseColor("#b2b2b2"));

        colorList.add(Color.parseColor("#dab420"));
        colorList.add(Color.parseColor("#aa5511"));
        colorList.add(Color.parseColor("#314159"));


        return colorList;
    }
    public interface ColorAdapterListener{
        void onColorSelected(int color);
    }
}
