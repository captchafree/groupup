package groupup.com.groupup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import groupup.com.groupup.Database.DatabaseManager;
import groupup.com.groupup.Database.GetDataListener;
import groupup.com.groupup.Database.UserKeys;

import static java.security.AccessController.getContext;

public class MemberListRVA extends RecyclerView.Adapter<MemberListRVA.ViewHolder> {

    private static final String TAG = "MemberListRVA";

    private ArrayList<String> mMemberNames;
    private List<String> mMemberIDs;
    private Group mGroup;
    private Context mContext;

    public MemberListRVA(Context context, ArrayList<String> members, List<String> memberIDs, Group group){

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
                final String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                Log.d(TAG, "OnClick: clicked on " + mMemberNames.get(position));

                final PopupMenu popup = new PopupMenu(mContext, v);

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        final String clickedID = mMemberIDs.get(position);
                        final DatabaseManager manager = DatabaseManager.getInstance();

                        switch (item.getItemId()) {
                            case R.id.remove:
                                //Removing a member
                                Log.d(TAG, "Removing member: " + mMemberNames.get(position));

                                //Don't allow owner to remove self
                                if(!clickedID.equals(userID)) {
                                    //Query for the user's information
                                    manager.getUserWithIdentifier(UserKeys.ID, clickedID, new GetDataListener() {
                                        @Override
                                        public void onSuccess(DataSnapshot data) {
                                            User user = data.getValue(User.class);
                                            Log.d(TAG, "member found: " + user.getName());
                                            //Remove the user and update the group/user information in Firebase
                                            user.removeGroup(mGroup.getID());
                                            mGroup.removeMember(clickedID);

                                            manager.updateUserWithID(clickedID, user);
                                            manager.updateGroupWithID(mGroup.getID(), mGroup);

                                            mMemberIDs.remove(clickedID);
                                            mMemberNames.remove(user.getName());
                                        }

                                        @Override
                                        public void onFailure(DatabaseError error) {

                                        }
                                    });
                                }
                                return true;
                            case R.id.promote:
                                //Promote a member to owner
                                Log.d(TAG, "Promote to Owner clicked");
                                //Query for the user's information
                                manager.getUserWithIdentifier(UserKeys.ID, clickedID, new GetDataListener() {
                                    @Override
                                    public void onSuccess(DataSnapshot data) {
                                        User user = data.getValue(User.class);
                                        Log.d(TAG, "member found: " + user.getName());

                                        //Change the owner and update the group in firebase
                                        mGroup.setOwner(clickedID);
                                        manager.updateGroupWithID(mGroup.getID(), mGroup);
                                        ((Activity)mContext).finish();
                                    }

                                    @Override
                                    public void onFailure(DatabaseError error) {

                                    }
                                });

                                return true;
                            case R.id.viewprofile:
                                //View a member's profile
                                Log.d(TAG, "View Profile clicked");
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
                            default:
                                return false;
                        }
                    }
                });

                Menu menuOptions = popup.getMenu();
                popup.inflate(R.menu.member_popup);

                //Hide the remove member and promote to owner options if the user isnt an owner
                if(!mGroup.getOwner().equals(userID)){
                    menuOptions.getItem(0).setVisible(false);
                    menuOptions.getItem(1).setVisible(false);
                }

                //Display the popup menu
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
