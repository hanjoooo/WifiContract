package com.example.khanj.wificontract.adapter;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.khanj.wificontract.R;
import com.example.khanj.wificontract.model.WalletModel;
import com.example.khanj.wificontract.model.WalletViewHolerModel;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by minseop on 2018-06-08.
 */

public class WalletRecyclerViewAdapter extends  RecyclerView.Adapter<WalletRecyclerViewAdapter.WalletViewHoler>{

    private OnItemClickListener mListener;
    private final List<WalletViewHolerModel> modelList;

    public interface OnItemClickListener{
        void onItemClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    public WalletRecyclerViewAdapter(List<WalletViewHolerModel> modelList) {
        this.modelList = modelList;
    }

    @NonNull
    @Override
    public WalletViewHoler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View view = inflater.inflate(R.layout.model_wallet_viewholer, parent, false);
        return new WalletViewHoler(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull WalletViewHoler holder, int position) {
        final WalletViewHolerModel model = modelList.get(position);
        holder.txt_walletName.setText(model.getWalletName());

        if (position%2==0) holder.imageView.setImageResource(R.drawable.wallet1);
        else holder.imageView.setImageResource(R.drawable.wallet2);

        if(model.getWalletBalance()==""){
            holder.txt_walletBalance.setText("getting Balance from the net....");
        }else holder.txt_walletBalance.setText(model.getWalletBalance());
        holder.txt_walletAddress.setText(model.getWalletAddress());
    }

    @Override
    public int getItemCount() {return modelList.size();}

    public void removeItem(int position) {
        removeRealmObject(modelList.get(position));
        modelList.remove(position);
        notifyItemRemoved(position);
    }

    private void removeRealmObject(WalletViewHolerModel model){
        Realm mRealm = Realm.getDefaultInstance();
        mRealm.beginTransaction();
        RealmResults<WalletModel> rows = mRealm.where(WalletModel.class).equalTo("walletName",model.getWalletName()).findAll();
        rows.deleteAllFromRealm();
        mRealm.commitTransaction();
    }
/*
    public void restoreItem(WalletViewHolerModel item, int position) {
        modelList.add(position, item);
        notifyItemInserted(position);
    }*/


    public static class WalletViewHoler extends RecyclerView.ViewHolder{

        private ImageView imageView;
        private TextView txt_walletName, txt_walletAddress, txt_walletBalance;
        public RelativeLayout viewForeground;
        public ConstraintLayout viewBackground;

        public WalletViewHoler(View itemView, OnItemClickListener listener) {
            super(itemView);

            imageView = itemView.findViewById(R.id.img_model_wallet_viewholder);
            txt_walletName = itemView.findViewById(R.id.txt_model_walletname);
            txt_walletAddress= itemView.findViewById(R.id.txt_model_walletaddress);
            txt_walletBalance= itemView.findViewById(R.id.txt_model_walletbalance);
            viewBackground = itemView.findViewById(R.id.view_background);
            viewForeground = itemView.findViewById(R.id.view_foreground);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}

