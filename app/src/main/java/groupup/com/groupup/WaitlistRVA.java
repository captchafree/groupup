package groupup.com.groupup;

import android.content.Context;
import android.content.Intent;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;

import groupup.com.groupup.Database.DatabaseManager;
import groupup.com.groupup.Database.GetDataListener;
import groupup.com.groupup.Database.UserKeys;

public class WaitlistRVA extends RecyclerView.Adapter<WaitlistRVA.ViewHolder> {

    private static final String TAG = "MemberListRVA";

    private ArrayList<String> mMemberNames;
    private List<String> mMemberIDs;
    private Group mGroup;
    private Context mContext;

    public WaitlistRVA(Context context, ArrayList<String> members, List<String> memberIDs, Group group){

        mMemberNames = members;
        mMemberIDs = memberIDs;
        mGroup = group;

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

                //Listener for the popup menu
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        final String clickedID = mMemberIDs.get(position);
                        final DatabaseManager manager = DatabaseManager.getInstance();

                        //Determine which button in the popup menu was pressed
                        switch (item.getItemId()) {
                            case R.id.profile:
                                //View user's profile
                                Log.d(TAG, "Viewing user's profile: " + mMemberNames.get(position));
                                //Query for the user's information
                                manager.getUserWithIdentifier(UserKeys.ID, clickedID, new GetDataListener() {
                                    @Override
                                    public void onSuccess(DataSnapshot data) {
                                        //Start the activity to view the user's information
                                        User user = data.getValue(User.class);
                                        Log.d(TAG, "member found: " + user.getName());
                                        Intent intent = new Intent(mContext, ViewUserProfile.class);
                                        intent.putExtra("user", user);
                                        mContext.startActivity(intent);
                                    }

                                    @Override
                                    public void onFailure(DatabaseError error) {

                                    }
                                });
                                return true;
                            case R.id.add:
                                //Add user to the group
                                Log.d(TAG, "Add user to group");
                                //Query for the user's information
                                manager.getUserWithIdentifier(UserKeys.ID, clickedID, new GetDataListener() {
                                    @Override
                                    public void onSuccess(DataSnapshot data) {
                                        User user = data.getValue(User.class);
                                        Log.d(TAG, "member found: " + user.getName());

                                        //Remove the user from the waitlist, add them to the group
                                        user.addGroup(mGroup.getID());
                                        mGroup.removeWaitlistUser(clickedID);
                                        mGroup.addMember(clickedID);

                                        //Update the user and group in Firebase
                                        manager.updateUserWithID(clickedID, user);
                                        manager.updateGroupWithID(mGroup.getID(), mGroup);

                                        mMemberIDs.remove(clickedID);
                                        mMemberNames.remove(user.getName());
                                    }

                                    @Override
                                    public void onFailure(DatabaseError error) {

                                    }
                                });

                                return true;
                            case R.id.remove:
                                //Remove user from the waitlist
                                Log.d(TAG, "Remove from waitlist clicked");
                                manager.getUserWithIdentifier(UserKeys.ID, clickedID, new GetDataListener() {
                                    @Override
                                    public void onSuccess(DataSnapshot data) {
                                        User user = data.getValue(User.class);
                                        Log.d(TAG, "member found: " + user.getName());

                                        //Remove the uer from the waitlist and update the group
                                        mGroup.removeWaitlistUser(clickedID);

                                        manager.updateGroupWithID(mGroup.getID(), mGroup);

                                        mMemberIDs.remove(clickedID);
                                        mMemberNames.remove(user.getName());
                                    }

                                    @Override
                                    public void onFailure(DatabaseError error) {

                                    }
                                });
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
