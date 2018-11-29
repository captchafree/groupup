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

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupRVA extends RecyclerView.Adapter<GroupRVA.ViewHolder> {

    private static final String TAG = "GroupRVA";

    private ArrayList<String> mGroupNames;
    private ArrayList<String> mImages;
    private ArrayList<Group> mGroups;
    private Context mContext;
    private Class nextPage;

    public GroupRVA(Context context, ArrayList<Group> groups, Class to){

        mGroupNames = new ArrayList<String>();
        mImages = new ArrayList<String>();

        for(Group g : groups) {
            mGroupNames.add(g.getName());
            mImages.add(g.getPicture());
        }

        mGroups = groups;
        mContext = context;
        nextPage = to;
    }

    //Responsible for inflating the view, recycles the viewholders and putting them into positions
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


                //Open the group's page
                Intent intent = new Intent(mContext, nextPage);
                intent.putExtra("group", mGroups.get(position));
                mContext.startActivity(intent);
            }
        });
    }

    //Tells the adapter how many items are in the list
    //if 0, nothing would happen
    @Override
    public int getItemCount() {
        return mGroupNames.size();
    }

    //Holds each entry in memory and recycles, holds the view
    public class ViewHolder extends RecyclerView.ViewHolder {

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
