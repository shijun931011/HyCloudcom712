package timechat.fcgz.sj.time.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;

import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.Toast;
import com.tencent.TIMCallBack;
import com.tencent.TIMFriendAllowType;
import com.tencent.TIMUserProfile;
import com.tencent.qcloud.presentation.business.LoginBusiness;
import com.tencent.qcloud.presentation.presenter.FriendshipManagerPresenter;
import com.tencent.qcloud.presentation.viewfeatures.FriendInfoView;
import timechat.fcgz.sj.time.R;
import timechat.fcgz.sj.time.ui.customview.CircleImageView;
import timechat.fcgz.sj.time.ui.customview.LineControllerView;
import timechat.fcgz.sj.time.utils.ImageUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 设置页面
 */
public class SettingFragment extends Fragment implements FriendInfoView {
    private static final String TAG = SettingFragment.class.getSimpleName();
    private View view;
    private FriendshipManagerPresenter friendshipManagerPresenter;
    private TextView id;
    private TextView name;
    private LineControllerView nickName;
    private LineControllerView company;
    private CircleImageView userinfo_img;
    private final int REQ_CHANGE_NICK = 1000;
    private Map<String, TIMFriendAllowType> allowTypeContent;
    private Uri imageUri;
    private static String path = "/sdcard/myHead/";// sd路径

    public SettingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_setting, container, false);
            id = (TextView) view.findViewById(R.id.idtext);
            name = (TextView) view.findViewById(R.id.name);
            userinfo_img = (CircleImageView) view.findViewById(R.id.head_me);
            userinfo_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ImageUtils.showImagePickDialog(getActivity());

                }
            });
        }

        friendshipManagerPresenter = new FriendshipManagerPresenter(this);
        friendshipManagerPresenter.getMyProfile();
        TextView logout = (TextView) view.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginBusiness.logout(new TIMCallBack() {
                    @Override
                    public void onError(int i, String s) {
                        Toast.makeText(getActivity(), getResources().getString(R.string.setting_logout_fail), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess() {
                        ((HomeActivity) getActivity()).logout();
                    }
                });
            }
        });
        nickName = (LineControllerView) view.findViewById(R.id.nickName);
        nickName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditActivity.navToEdit(SettingFragment.this, getResources().getString(R.string.setting_nick_name_change), name.getText().toString(), REQ_CHANGE_NICK, new EditActivity.EditInterface() {
                    @Override
                    public void onEdit(String text, TIMCallBack callBack) {
                        FriendshipManagerPresenter.setMyNick(text, callBack);
                    }
                }, 20);
            }
        });
        allowTypeContent = new HashMap<>();
        allowTypeContent.put(getString(R.string.friend_allow_all), TIMFriendAllowType.TIM_FRIEND_ALLOW_ANY);
        allowTypeContent.put(getString(R.string.friend_need_confirm), TIMFriendAllowType.TIM_FRIEND_NEED_CONFIRM);
        allowTypeContent.put(getString(R.string.friend_refuse_all), TIMFriendAllowType.TIM_FRIEND_DENY_ANY);
        final String[] stringList = allowTypeContent.keySet().toArray(new String[allowTypeContent.size()]);

        LineControllerView modifyPwd = (LineControllerView) view.findViewById(R.id.Modify_login);
        modifyPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ModifyPwdActivity.class);
                getActivity().startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_CHANGE_NICK) {
            if (resultCode == getActivity().RESULT_OK) {
                setNickName(data.getStringExtra(EditActivity.RETURN_EXTRA));
            }
        }

    }

    private void setNickName(String name){
        if (name == null) return;
        this.name.setText(name);
        nickName.setContent(name);
    }

    /**
     * 显示用户信息
     *
     * @param users 资料列表
     */
    @Override
    public void showUserInfo(List<TIMUserProfile> users) {
        id.setText(users.get(0).getIdentifier());
        setNickName(users.get(0).getNickName());
        for (String item : allowTypeContent.keySet()){
            if (allowTypeContent.get(item) == users.get(0).getAllowType()){
//                company.setContent(item);
                break;
            }
        }

    }

}
