package timechat.fcgz.sj.time.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.TIMCallBack;
import com.tencent.TIMConversationType;
import com.tencent.TIMFriendResult;
import com.tencent.TIMFriendStatus;

import com.tencent.TIMValueCallBack;

import com.tencent.qcloud.presentation.event.FriendshipEvent;
import com.tencent.qcloud.presentation.presenter.FriendshipManagerPresenter;
import com.tencent.qcloud.presentation.viewfeatures.FriendshipManageView;

import timechat.fcgz.sj.time.model.FriendProfile;
import timechat.fcgz.sj.time.model.FriendshipInfo;
import timechat.fcgz.sj.time.ui.customview.LineControllerView;
import timechat.fcgz.sj.time.ui.customview.ListPickerDialog;

import java.util.Collections;
import java.util.List;

public class ProfileActivity extends FragmentActivity implements FriendshipManageView,  View.OnClickListener {

    private static final String TAG = ProfileActivity.class.getSimpleName();

    private final int CHANGE_CATEGORY_CODE = 100;
    private final int CHANGE_REMARK_CODE = 200;

    private FriendshipManagerPresenter friendshipManagerPresenter;
    private String identify, categoryStr;


    public static void navToProfile(Context context, String identify){
        Intent intent = new Intent(context, ProfileActivity.class);
        intent.putExtra("identify", identify);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(timechat.fcgz.sj.time.R.layout.activity_profile);
        identify = getIntent().getStringExtra("identify");
        friendshipManagerPresenter = new FriendshipManagerPresenter(this);
        showProfile(identify);
    }

    /**
     * 显示用户信息
     *
     * @param identify
     */
    public void showProfile(final String identify) {
        final FriendProfile profile = FriendshipInfo.getInstance().getProfile(identify);
        Log.d(TAG, "show profile isFriend " + (profile!=null));
        if (profile == null) return;
        TextView name = (TextView) findViewById(timechat.fcgz.sj.time.R.id.name);
        name.setText(profile.getName());
        LineControllerView id = (LineControllerView) findViewById(timechat.fcgz.sj.time.R.id.id);
        id.setContent(profile.getIdentify());
        final LineControllerView remark = (LineControllerView) findViewById(timechat.fcgz.sj.time.R.id.remark);
        remark.setContent(profile.getRemark());
        remark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditActivity.navToEdit(ProfileActivity.this, getString(timechat.fcgz.sj.time.R.string.profile_remark_edit), remark.getContent(), CHANGE_REMARK_CODE, new EditActivity.EditInterface() {
                    @Override
                    public void onEdit(String text, TIMCallBack callBack) {
                        FriendshipManagerPresenter.setRemarkName(profile.getIdentify(), text, callBack);
                    }
                },20);

            }
        });
        LineControllerView category = (LineControllerView) findViewById(timechat.fcgz.sj.time.R.id.group);
        //一个用户可以在多个分组内，客户端逻辑保证一个人只存在于一个分组
        category.setContent(categoryStr = profile.getGroupName());
        LineControllerView black = (LineControllerView) findViewById(timechat.fcgz.sj.time.R.id.blackList);
        black.setCheckListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    FriendshipManagerPresenter.addBlackList(Collections.singletonList(identify), new TIMValueCallBack<List<TIMFriendResult>>() {
                        @Override
                        public void onError(int i, String s) {
                            Log.e(TAG, "add black list error " + s);
                        }

                        @Override
                        public void onSuccess(List<TIMFriendResult> timFriendResults) {
                            if (timFriendResults.get(0).getStatus() == TIMFriendStatus.TIM_FRIEND_STATUS_SUCC){
                                Toast.makeText(ProfileActivity.this, getString(timechat.fcgz.sj.time.R.string.profile_black_succ), Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    });
                }
            }
        });

    }




    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case timechat.fcgz.sj.time.R.id.btnChat:
                Intent intent = new Intent(this, ChatActivity.class);
                intent.putExtra("identify", identify);
                intent.putExtra("type", TIMConversationType.C2C);
                startActivity(intent);
                finish();
                break;
            case timechat.fcgz.sj.time.R.id.btnDel:
                friendshipManagerPresenter.delFriend(identify);
                break;
            case timechat.fcgz.sj.time.R.id.group:
                final String[] groups = FriendshipInfo.getInstance().getGroupsArray();
                for (int i = 0; i < groups.length; ++i) {
                    if (groups[i].equals("")) {
                        groups[i] = getString(timechat.fcgz.sj.time.R.string.default_group_name);
                        break;
                    }
                }
                new ListPickerDialog().show(groups, getSupportFragmentManager(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (groups[which].equals(categoryStr)) return;
                        friendshipManagerPresenter.changeFriendGroup(identify,
                                categoryStr.equals(getString(timechat.fcgz.sj.time.R.string.default_group_name))?null:categoryStr,
                                groups[which].equals(getString(timechat.fcgz.sj.time.R.string.default_group_name))?null:groups[which]);
                    }
                });
                break;
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CHANGE_CATEGORY_CODE) {
            if (resultCode == RESULT_OK) {
                LineControllerView category = (LineControllerView) findViewById(timechat.fcgz.sj.time.R.id.group);
                category.setContent(categoryStr = data.getStringExtra("category"));
            }
        }else if (requestCode == CHANGE_REMARK_CODE){
            if (resultCode == RESULT_OK) {
                LineControllerView remark = (LineControllerView) findViewById(timechat.fcgz.sj.time.R.id.remark);
                remark.setContent(data.getStringExtra(EditActivity.RETURN_EXTRA));

            }
        }

    }

    /**
     * 添加好友结果回调
     *
     * @param status 返回状态
     */
    @Override
    public void onAddFriend(TIMFriendStatus status) {

    }

    /**
     * 删除好友结果回调
     *
     * @param status 返回状态
     */
    @Override
    public void onDelFriend(TIMFriendStatus status) {
        switch (status){
            case TIM_FRIEND_STATUS_SUCC:
                Toast.makeText(this, getResources().getString(timechat.fcgz.sj.time.R.string.profile_del_succeed), Toast.LENGTH_SHORT).show();
                finish();
                break;
            case TIM_FRIEND_STATUS_UNKNOWN:
                Toast.makeText(this, getResources().getString(timechat.fcgz.sj.time.R.string.profile_del_fail), Toast.LENGTH_SHORT).show();
                break;
        }

    }

    /**
     * 修改好友分组回调
     *
     * @param status    返回状态
     * @param groupName 分组名
     */
    @Override
    public void onChangeGroup(TIMFriendStatus status, String groupName) {
        LineControllerView category = (LineControllerView) findViewById(timechat.fcgz.sj.time.R.id.group);
        if (groupName == null){
            groupName = getString(timechat.fcgz.sj.time.R.string.default_group_name);
        }
        switch (status){
            case TIM_FRIEND_STATUS_UNKNOWN:
                Toast.makeText(this, getString(timechat.fcgz.sj.time.R.string.change_group_error),Toast.LENGTH_SHORT).show();
            case TIM_FRIEND_STATUS_SUCC:
                category.setContent(groupName);
                FriendshipEvent.getInstance().OnFriendGroupChange();
                break;
            default:
                Toast.makeText(this, getString(timechat.fcgz.sj.time.R.string.change_group_error),Toast.LENGTH_SHORT).show();
                category.setContent(getString(timechat.fcgz.sj.time.R.string.default_group_name));
                break;
        }
    }
}
