package timechat.fcgz.sj.time.ui;

import android.app.Activity;
import android.content.ContentResolver;
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

import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.tencent.TIMCallBack;
import com.tencent.TIMFriendAllowType;
import com.tencent.TIMUserProfile;
import com.tencent.qcloud.presentation.business.LoginBusiness;
import com.tencent.qcloud.presentation.presenter.FriendshipManagerPresenter;
import com.tencent.qcloud.presentation.viewfeatures.FriendInfoView;
import timechat.fcgz.sj.time.R;

import timechat.fcgz.sj.time.ui.cameracrop.CropImage;
import timechat.fcgz.sj.time.ui.customview.CircleImageView;
import timechat.fcgz.sj.time.ui.customview.LineControllerView;
import timechat.fcgz.sj.time.ui.customview.ListPickerDialog;
import timechat.fcgz.sj.time.ui.file.FileHelper;
import timechat.fcgz.sj.time.ui.file.ImageUpload;
import timechat.fcgz.sj.time.ui.file.SharedPreferenceUtils;
import timechat.fcgz.sj.time.ui.file.UserInfoEntity;
import timechat.fcgz.sj.time.utils.Constant;
import timechat.fcgz.sj.time.utils.CropUtil;
import timechat.fcgz.sj.time.utils.DensityUtils;
import timechat.fcgz.sj.time.utils.DialogUtil;
import timechat.fcgz.sj.time.utils.ImageUtils;
import timechat.fcgz.sj.time.utils.SDCardUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


/**
 * 设置页面
 */
public class SettingFragment extends Fragment implements FriendInfoView{
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
    private String path;   //图片路径
    private Bitmap mCropPhoto;
    private Drawable drawablePhoto = null;
//    private ImageView userinfo_img;
    private Drawable userinfodrawable;
    private UserInfoEntity userinfoentity = new UserInfoEntity();



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
        if (view == null){
            view = inflater.inflate(R.layout.fragment_setting, container, false);
            id = (TextView) view.findViewById(R.id.idtext);
            name = (TextView) view.findViewById(R.id.name);
            userinfo_img = (CircleImageView) view.findViewById(R.id.head_me);
            userinfo_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ImageUtils.showImagePickDialog(getActivity());
                    imageUri = ImageUtils.getOutputFileUri();
//                    new DialogUtil(getContext()).builder()
//                            .setCancelable(true)
//                            .setCanceledOnTouchOutside(true)
//                            .addSheetItem("拍照", DialogUtil.SheetItemColor.Blue,
//                                    new DialogUtil.OnSheetItemClickListener() {
//                                        @Override
//                                        public void onClick(int which) {
//                                            if (!SDCardUtils.isSDCardEnable()) {
//                                                Toast.makeText(getContext(), "SD卡不可用状态,此功能不可用!", Toast.LENGTH_SHORT).show();
//                                                return;
//                                            }
//                                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                                            // set the image file name
//                                            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//                                            startActivityForResult(intent, Constant.CAMERA_REQUEST_CODE);
////                                            getPicFromCamera();
//
//                                        }
//                                    })
//                            .addSheetItem("从相册选择", DialogUtil.SheetItemColor.Blue,
//                                    new DialogUtil.OnSheetItemClickListener() {
//                                        @Override
//                                        public void onClick(int which) {
//                                            if (!SDCardUtils.isSDCardEnable()) {
//                                                Toast.makeText(getContext(), "SD卡不可用状态,此功能不可用!", Toast.LENGTH_SHORT).show();
//                                                return;
//                                            }
//                                            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                                            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
//                                            intent.putExtra("return-data", true);
//                                            startActivityForResult(intent, Constant.IMAGE_REQUEST_CODE);
//
//                                        }
//                                    }).show();
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
                    },20);
                }
        });
            allowTypeContent = new HashMap<>();
            allowTypeContent.put(getString(R.string.friend_allow_all), TIMFriendAllowType.TIM_FRIEND_ALLOW_ANY);
            allowTypeContent.put(getString(R.string.friend_need_confirm), TIMFriendAllowType.TIM_FRIEND_NEED_CONFIRM);
            allowTypeContent.put(getString(R.string.friend_refuse_all), TIMFriendAllowType.TIM_FRIEND_DENY_ANY);
            final String[] stringList = allowTypeContent.keySet().toArray(new String[allowTypeContent.size()]);
            company = (LineControllerView) view.findViewById(R.id.company);
            company.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    new ListPickerDialog().show(stringList, getFragmentManager(), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, final int which) {
                            FriendshipManagerPresenter.setFriendAllowType(allowTypeContent.get(stringList[which]), new TIMCallBack() {
                                @Override
                                public void onError(int i, String s) {
                                    Toast.makeText(getActivity(), getString(R.string.setting_friend_confirm_change_err), Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onSuccess() {
                                    company.setContent(stringList[which]);
                                }
                            });
                        }
                    });
                }
            });

            LineControllerView department = (LineControllerView) view.findViewById(R.id.department);
            department.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Intent intent = new Intent(getActivity(), BlackListActivity.class);
//                    startActivity(intent);
                }
            });
            LineControllerView phone = (LineControllerView) view.findViewById(R.id.setting_phone);
            phone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Intent intent = new Intent(getActivity(), BlackListActivity.class);
//                    startActivity(intent);
                }
            });
            LineControllerView area = (LineControllerView) view.findViewById(R.id.area);
            area.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Intent intent = new Intent(getActivity(), BlackListActivity.class);
//                    startActivity(intent);
                }
            });
            LineControllerView modifyPwd = (LineControllerView) view.findViewById(R.id.Modify_login);
//            Bundle bundle = getArguments();
//            modifyPwd.setContent(bundle.getString(ModifyPwdActivity.ARGUMENT_NAME));
            modifyPwd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), ModifyPwdActivity.class);
                    getActivity().startActivity(intent);
                }
            });
        return view ;
        }


        @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
            super.onActivityResult(requestCode, resultCode, data);
        // 防止空指针异常
        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case Constant.CAMERA_REQUEST_CODE: {

                dealImageByUri(getContext(), imageUri);
                Log.d("1234", "onActivityResult: " + "11111111111");
                break;
            }
            case Constant.RESULT_REQUEST_CODE: {// 调用图片剪切程序返回数据

                File picFile = new File(Constant.EXTERNAL_HEADICO_DIR + "/"
                        + Constant.USER_PHOTO_NAME);

                path = picFile.getAbsolutePath();
                if (data == null) {
                    mCropPhoto = BitmapFactory.decodeFile(path);
                } else {
                    mCropPhoto = data.getParcelableExtra("data");
                }
                drawablePhoto = new BitmapDrawable(mCropPhoto);
                Bitmap bitmap = ImageUtils.DrawableToBitmap(drawablePhoto);
                try {
                    ImageUtils.saveBitmapToFile(mCropPhoto, path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                recycleCropPhoto();
                //   保存图片成功后从本地获取图片
                uploadphoto(drawablePhoto);
                break;
            }
            case Constant.IMAGE_REQUEST_CODE: {

                Uri uriImage = data.getData();
                dealImageByUri(getContext(), uriImage);
                break;
            }

        }
            if (requestCode == REQ_CHANGE_NICK) {
                if (resultCode == getActivity().RESULT_OK) {
                    setNickName(data.getStringExtra(EditActivity.RETURN_EXTRA));
                }
            }
        }


    private void uploadphoto(Drawable drawablePhoto){
        if (ImageUtils.filepathImagetoDrawable(Constant.photo3) != null) {
            drawablePhoto = ImageUtils.filepathImagetoDrawable(Constant.photo3);
        } else {
            drawablePhoto = ContextCompat.getDrawable(getContext(), R.drawable.user_center);
        }
        userinfo_img.setImageDrawable(drawablePhoto);
    }

    private void dealImageByUri(Context context, Uri uriCamera) {
//        Logg.d("dealImageByUri");
        Bitmap tempPhoto = null;
        try {
            ContentResolver cr = context.getContentResolver();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            options.inPurgeable = true;   //允许可清除
            options.inInputShareable = true;// 以上options的两个属性必须联合使用才会有效果
            InputStream is = cr.openInputStream(uriCamera);
            tempPhoto = BitmapFactory.decodeStream(is, null, options);
            is.close();
        } catch (Exception e) {
            if (tempPhoto != null)
                tempPhoto.recycle();
            tempPhoto = null;
            e.printStackTrace();
        }
        if (tempPhoto != null) {
            // 调用剪切程序
            // 这里如果是调用媒体库返回的图片数据，则调用剪切程序进行剪切
            // 如果是调用剪切程序返回的数据，则走case PHOTO_CROP_DATA:这条分支，然后直接return
            int height = (int) (DensityUtils.getDisplayMetrics(context) * 160);
            cropPhoto(tempPhoto, height, height);
        }
        if (tempPhoto != null) {
            tempPhoto.recycle();// 即时回收
        }
        tempPhoto = null;
    }

    /**
     * 压缩图片并缓存到存储卡，startActivityForResult方式调用剪切程序
     *
     * @param photo  待裁剪的图片
     * @param width  裁剪后的图片宽度
     * @param height 裁剪后的图片高度
     */

    private void cropPhoto(Bitmap photo, int width, int height) {


        // 将选择的图片等比例压缩后缓存到存储卡根目录，并返回图片文件
        File f = CropUtil.makeTempFile(getContext(), photo, "TEMPIMG.png");
        if (f == null) {
            Toast.makeText(getContext(), "内存空间不足,创建图像失败", Toast.LENGTH_SHORT).show();
            return;
        }
        // 调用CropImage类对图片进行剪切
        Intent intent = new Intent(getContext(), CropImage.class);
        Bundle extras = new Bundle();
        extras.putString("circleCrop", "true");
        extras.putInt("aspectX", 200);
        extras.putInt("aspectY", 200);
        extras.putInt("outputX", (int) width);
        extras.putInt("outputY", (int) height);
        intent.putExtra("scale", true);// 黑边
        intent.putExtra("scaleUpIfNeeded", true);// 黑边
        intent.setDataAndType(Uri.fromFile(f), "image/*");
        intent.putExtras(extras);
        startActivityForResult(intent, Constant.RESULT_REQUEST_CODE);
//        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_left);
    }

    /**
     * 回收头像png 删除原始照片
     */
    private void recycleCropPhoto() {
        FileHelper filehelper = new FileHelper(getContext());
        String fileName = Constant.photo3;
        filehelper.delSDFile(fileName);
    }

//    private void uploadphoto(Drawable drawablePhoto) {
//        String url = Constant.photo3;
//        Map<String, String> textMap = new HashMap<String, String>();
////
//        // TODO
//        textMap.put("username", (String) SharedPreferenceUtils.get(
//                getContext(), "username", ""));
//
//        Map<String, String> fileMap = new HashMap<String, String>();
//        fileMap.put("userphoto", path);
//        ImageUpload uploadPhotoHttpRequest = new ImageUpload(url, textMap,
//                fileMap);
//        uploadPhotoHttpRequest.setRequest(url, textMap, fileMap);
//        uploadPhotoHttpRequest.startRequest(uploadPhotoRequestListener);
//    }

//    ImageUpload.ImageUploadListener uploadPhotoRequestListener = new ImageUpload.ImageUploadListener() {
//
//        @Override
//        public void onResult(ImageUpload request, int tag) {
//            String resultString = request.getResultString();
//            userinfoentity.setuPhoto(drawablePhoto);
//            userinfodrawable = userinfoentity.getuPhoto();
//            if (userinfodrawable != null) {
//                userinfo_img.setImageDrawable(userinfodrawable);
//            }
//            Toast.makeText(getContext(), "上传成功", Toast.LENGTH_SHORT).show();
//        }
//
//        @Override
//        public void onError(int errorCode, int tag) {
//            Log.d(TAG, "onError: " + errorCode + "   " + tag);
//            Toast.makeText(getContext(), "上传图像失败", Toast.LENGTH_SHORT).show();
//        }

//    };

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
