package com.metapp.goodmoods;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MainListAdapter extends RecyclerView.Adapter<MainListAdapter.MainViewHolder> {

    private Context context;
    private ArrayList<String> urls;
    private ImageClickListener listener;

    public MainListAdapter(Context context, ArrayList<String> urls) {
        this.context = context;
        this.urls = urls;
        listener = (ImageClickListener) context;
    }

    @NonNull
    @Override
    public MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MainViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.main_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MainViewHolder holder, int position) {
        holder.setData(urls.get(position));
    }

    @Override
    public int getItemCount() {
        return urls.size();
    }

    public class MainViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imageView;

        public MainViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
            imageView = itemView.findViewById(R.id.img_list);
        }

        @Override
        public void onClick(View v) {
            listener.imageClicked(urls.get(getAdapterPosition()));

        }

        public void setData(String url) {
            Picasso.get().load(url).fit().centerCrop().into(imageView);
        }
    }

    public interface ImageClickListener{
        void imageClicked(String url);
    }

}
