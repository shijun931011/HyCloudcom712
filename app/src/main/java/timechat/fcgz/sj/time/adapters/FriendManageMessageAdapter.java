package timechat.fcgz.sj.time.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tencent.TIMFriendResult;
import com.tencent.TIMFutureFriendType;
import com.tencent.TIMValueCallBack;
import com.tencent.qcloud.presentation.presenter.FriendshipManagerPresenter;

import timechat.fcgz.sj.time.model.FriendFuture;
import timechat.fcgz.sj.time.ui.customview.CircleImageView;

import java.util.List;

/**
 * 好友关系链管理消息adapter
 */
public class FriendManageMessageAdapter extends ArrayAdapter<FriendFuture> {
    private int resourceId;
    private View view;
    private ViewHolder viewHolder;

    /**
     * Constructor
     *
     * @param context  The current context.
     * @param resource The resource ID for a layout file containing a TextView to use when
     *                 instantiating views.
     * @param objects  The objects to represent in the ListView.
     */
    public FriendManageMessageAdapter(Context context, int resource, List<FriendFuture> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView != null) {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        } else {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.avatar = (CircleImageView) view.findViewById(timechat.fcgz.sj.time.R.id.avatar);
            viewHolder.name = (TextView) view.findViewById(timechat.fcgz.sj.time.R.id.name);
            viewHolder.des = (TextView) view.findViewById(timechat.fcgz.sj.time.R.id.description);
            viewHolder.status = (TextView) view.findViewById(timechat.fcgz.sj.time.R.id.status);
            view.setTag(viewHolder);
        }
        Resources res = getContext().getResources();
        final FriendFuture data = getItem(position);
        viewHolder.avatar.setImageResource(timechat.fcgz.sj.time.R.drawable.head_other);
        viewHolder.name.setText(data.getName());
        viewHolder.des.setText(data.getMessage());
        viewHolder.status.setTextColor(res.getColor(timechat.fcgz.sj.time.R.color.text_gray1));
        switch (data.getType()){
            case TIM_FUTURE_FRIEND_PENDENCY_IN_TYPE:
                viewHolder.status.setText(res.getString(timechat.fcgz.sj.time.R.string.newfri_agree));
                viewHolder.status.setTextColor(res.getColor(timechat.fcgz.sj.time.R.color.text_blue1));
                viewHolder.status.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FriendshipManagerPresenter.acceptFriendRequest(data.getIdentify(), new TIMValueCallBack<TIMFriendResult>() {
                            @Override
                            public void onError(int i, String s) {

                            }

                            @Override
                            public void onSuccess(TIMFriendResult timFriendResult) {
                                data.setType(TIMFutureFriendType.TIM_FUTURE_FRIEND_DECIDE_TYPE);
                                notifyDataSetChanged();
                            }
                        });
                    }
                });
                break;
            case TIM_FUTURE_FRIEND_PENDENCY_OUT_TYPE:
                viewHolder.status.setText(res.getString(timechat.fcgz.sj.time.R.string.newfri_wait));
                break;
            case TIM_FUTURE_FRIEND_DECIDE_TYPE:
                viewHolder.status.setText(res.getString(timechat.fcgz.sj.time.R.string.newfri_accept));
                break;
        }
        return view;
    }

    public class ViewHolder{
        ImageView avatar;
        TextView name;
        TextView des;
        TextView status;
    }
}