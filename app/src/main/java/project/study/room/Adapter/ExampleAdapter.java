package project.study.room.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import project.study.room.R;


public class ExampleAdapter extends RecyclerView.Adapter<ExampleAdapter.ExampleViewHolder> {
    private ArrayList<ExampleItem> mExampleList;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);

        //void onDeleteClick(int position);

        //void onLongItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public static class ExampleViewHolder extends RecyclerView.ViewHolder {
        public TextView id;
        public TextView userId;
        public TextView title;
        public TextView text;


        public ExampleViewHolder(View itemView,final OnItemClickListener listener) {
            super(itemView);
            id = itemView.findViewById(R.id.id);
            userId = itemView.findViewById(R.id.userId);
            title = itemView.findViewById(R.id.title);
            text = itemView.findViewById(R.id.text);

        }
    }


    public ExampleAdapter(ArrayList<ExampleItem> exampleList) {
        mExampleList = exampleList;
    }

    @Override
    public ExampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_list, parent, false);
        ExampleViewHolder evh = new ExampleViewHolder(v, mListener);
        return evh;
    }

    @Override
    public void onBindViewHolder(ExampleViewHolder holder, int position) {
        ExampleItem currentItem = mExampleList.get(position);

        holder.id.setText(String.valueOf(currentItem.getId()));
        holder.userId.setText(String.valueOf(currentItem.getUserId()));
        holder.title.setText(currentItem.getTitle());
        holder.text.setText(currentItem.getText());


    }

    @Override
    public int getItemCount() {
        return mExampleList.size();
    }

}
