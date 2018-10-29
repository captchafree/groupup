package groupup.com.groupup;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    private static final String TAG = "RecyclerViewAdapter";

    private ArrayList<String> mGroupNames = new ArrayList<>();
    private ArrayList<String> mGroupIDs = new ArrayList<>();
    private ArrayList<String> mImages = new ArrayList<>();
    private Context mContext;

    public RecyclerViewAdapter(Context context, ArrayList<String> groupNames, ArrayList<String> groupIDs, ArrayList<String> images){
        mContext = context;
        mGroupNames = groupNames;
        mGroupIDs = new ArrayList<>();
        mImages = images;
    }

    //Responsible for inflating the view, recycles the viewholders and putting them into po itions
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listitem, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    //Will change based on what layouts are and what want them to look like
    //Called every time a new item is added to the list
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");

        //Get the image
        Glide.with(mContext)
                .asBitmap()
                .load(mImages.get(position))
                .into(holder.image);
        holder.groupName.setText(mGroupNames.get(position));

        //On click listener for an item in the list
        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "OnClick: clicked on " + mGroupNames.get(position));

                //Print the group's name on the user's screen
                //Toast.makeText(mContext, mGroupNames.get(position), Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(mContext, OutsiderGroupPage.class);
                intent.putExtra("image_url", mImages.get(position));
                intent.putExtra("group_name", mGroupNames.get(position));
                mContext.startActivity(intent);
            }
        });
    }

    //Tells the adapter how many items are in the list
    // if 0, nothing would happen
    @Override
    public int getItemCount() {
        return mGroupNames.size();
    }

    //Holds each entry in memory and recycles, holds the view
    public class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView image;
        TextView groupName;
        RelativeLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            groupName = itemView.findViewById(R.id.group_name);
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }
    }
}
