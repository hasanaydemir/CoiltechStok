package cf.coiltech.com.stok.adapter;

/**
 * Created by Hasan Aydemir 10.04.2018
 * */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import cf.coiltech.com.stok.R;
import cf.coiltech.com.stok.network.model.Contact;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.MyViewHolder> {
    private Context context;
    private List<Contact> contactList;
    private ContactsAdapterListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView model, marka, urunID;
        public ImageView kucukResim;

        public MyViewHolder(View view) {
            super(view);
            model= view.findViewById(R.id.model);
            marka = view.findViewById(R.id.marka);
            kucukResim = view.findViewById(R.id.kucukResim);
            urunID= view.findViewById(R.id.urunIDNo);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected contact in callback
                    listener.onContactSelected(contactList.get(getAdapterPosition()));
                }
            });
        }
    }


    public ContactsAdapter(Context context, List<Contact> contactList, ContactsAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.contactList = contactList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_row_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Contact contact = contactList.get(position);
        holder.model.setText(contact.getModel());
        holder.marka.setText(contact.getMarka());
        holder.urunID.setText(contact.getID());
/*
        Glide.with(context)
                .load(contact.getProfileImage())
                .apply(RequestOptions.circleCropTransform())
                .into(holder.thumbnail);
  */

// changed image viewer ImageView to Picasso
        Picasso.get().load(contact.getKucukResim()).into(holder.kucukResim);

    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public interface ContactsAdapterListener {
        void onContactSelected(Contact contact);
    }
}
