package groupup.com.groupup;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class WaitlistRVA extends RecyclerView.Adapter<WaitlistRVA.ViewHolder> {

    private static final String TAG = "MemberListRVA";

    private ArrayList<String> mMemberNames;
    private Context mContext;

    public WaitlistRVA(Context context, ArrayList<String> members){

        mMemberNames = new ArrayList<String>();

        for(String s : members)
            mMemberNames.add(s);

        mContext = context;
    }

    //Responsible for inflating the view, recycles the viewholders and putting them into positions
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.member_listitem, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    //Will change based on what layouts are and what want them to look like
    //Called every time a new item is added to the list
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");

        holder.memberName.setText(mMemberNames.get(position));

        //On click listener for an item in the list
        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "OnClick: clicked on " + mMemberNames.get(position));

                PopupMenu popup = new PopupMenu(mContext, v);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.profile:
                                //View user's profile
                                Log.d(TAG, "Removing member: " + mMemberNames.get(position));
                                return true;
                            case R.id.add:
                                //Add user to the group
                                Log.d(TAG, "Placeholder 1 clicked");
                                return true;
                            case R.id.remove:
                                //Remove user from the waitlist
                                Log.d(TAG, "Placeholder 2 clicked");
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popup.inflate(R.menu.waitlist_popup);
                popup.show();
            }
        });
    }

    //Tells the adapter how many items are in the list
    //if 0, nothing would happen
    @Override
    public int getItemCount() {
        return mMemberNames.size();
    }

    //Holds each entry in memory and recycles, holds the view
    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView memberName;
        RelativeLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            memberName = itemView.findViewById(R.id.member_name);
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }
    }
}
