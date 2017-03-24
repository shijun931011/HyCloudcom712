package timechat.fcgz.sj.time.ui;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;

import com.tencent.TIMUserProfile;
import com.tencent.qcloud.presentation.presenter.FriendshipManagerPresenter;
import com.tencent.qcloud.presentation.viewfeatures.FriendInfoView;

import timechat.fcgz.sj.time.R;
import timechat.fcgz.sj.time.adapters.ExpandGroupListAdapter;
import timechat.fcgz.sj.time.adapters.ProfileSummaryAdapter;
import timechat.fcgz.sj.time.model.FriendProfile;
import timechat.fcgz.sj.time.model.FriendshipInfo;

import timechat.fcgz.sj.time.model.ProfileSummary;
import timechat.fcgz.sj.time.ui.customview.TemplateTitle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 联系人界面
 */
public class ContactFragment extends Fragment{
    private View view;
    private ExpandGroupListAdapter mGroupListAdapter;
    private ExpandableListView mGroupListView;
    private EditText mSearchinput;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (view == null){
            view = inflater.inflate(R.layout.fragment_contact, container, false);
            mSearchinput = (EditText) view.findViewById(R.id.inputSearch);
            mSearchinput.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), SearchFriendActivity.class);
                    getActivity().startActivity(intent);
                }
            });
            mGroupListView = (ExpandableListView) view.findViewById(R.id.groupList);
            TemplateTitle title = (TemplateTitle) view.findViewById(R.id.contact_antionbar);
            title.setMoreImgAction(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showMoveDialog();
                }
            });
            final Map<String, List<FriendProfile>> friends = FriendshipInfo.getInstance().getFriends();
            mGroupListAdapter = new ExpandGroupListAdapter(getActivity(), FriendshipInfo.getInstance().getGroups(), friends);
            mGroupListView.setAdapter(mGroupListAdapter);
            mGroupListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long l) {
                    friends.get(FriendshipInfo.getInstance().getGroups().get(groupPosition)).get(childPosition).onClick(getActivity());
                    return false;
                }
            });
            mGroupListAdapter.notifyDataSetChanged();
        }
        return view;
    }


    @Override
    public void onResume(){
        super.onResume();
        mGroupListAdapter.notifyDataSetChanged();
    }

    private Dialog inviteDialog;
    private TextView addFriend;
//    private TextView addGroup;
    private TextView managerGroup;
    private void showMoveDialog() {
        inviteDialog = new Dialog(getActivity(), R.style.dialog);
        inviteDialog.setContentView(R.layout.contact_more);
        addFriend = (TextView) inviteDialog.findViewById(R.id.add_friend);
        managerGroup = (TextView) inviteDialog.findViewById(R.id.manager_group);
//        addGroup = (TextView) inviteDialog.findViewById(R.id.add_group);
        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SearchFriendActivity.class);
                getActivity().startActivity(intent);
                inviteDialog.dismiss();
            }
        });
//        addGroup.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), SearchGroupActivity.class);
//                getActivity().startActivity(intent);
//                inviteDialog.dismiss();
//            }
//        });
        managerGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ManageFriendGroupActivity.class);
                getActivity().startActivity(intent);
                inviteDialog.dismiss();
            }
        });
        Window window = inviteDialog.getWindow();
        window.setGravity(Gravity.TOP | Gravity.RIGHT);
        WindowManager.LayoutParams lp = window.getAttributes();
        window.setAttributes(lp);
        inviteDialog.show();
    }
//    private void showGroups(String type){
//        Intent intent = new Intent(getActivity(), GroupListActivity.class);
//        intent.putExtra("type", type);
//        getActivity().startActivity(intent);
//    }
}
