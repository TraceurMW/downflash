package com.itheima.myapplication;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.os.Build;

import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import static android.R.attr.button;
import static android.R.attr.paddingTop;
import static com.itheima.myapplication.R.id.listview;

/**
 * Created by TraceurMW on 2017/1/16.
 */

public class RefreashListView extends ListView {

    private View mHeaderView;
    private float downY;
    private float moveY;
    private int mHeaderViewHeight;
    public static final int PULL_to_REFRESH=0;
    public static final int RELEASE_REFRESH=1;
    public static final int REFRESHING=2;
    private int currentState=PULL_to_REFRESH;
    private RotateAnimation RotateUpAnim;
    private View mHeadArrow;
    private TextView mTitle;
    private TextView mStateDesc;
    private ProgressBar mPb;
    private ProgressBar Pb;
    private RotateAnimation RotateDownAnim;
    private OnRefreshListener mListener;


    public RefreashListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }



    public RefreashListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RefreashListView(Context context) {
        super(context);
        init();
    }
    private void init() {
        initHeaderView();
        initAnimation();

    }

    private void initAnimation() {
        RotateUpAnim = new RotateAnimation(0f, -180f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        RotateUpAnim.setDuration(300);
        RotateUpAnim.setFillAfter(true);

        RotateDownAnim = new RotateAnimation(-180f, -360f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        RotateDownAnim.setDuration(300);
        RotateDownAnim.setFillAfter(true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                downY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                moveY=ev.getY();
                if(currentState==REFRESHING){
                    return super.onTouchEvent(ev);
                }
                float offset=moveY-downY;
                if(offset>0&&getFirstVisiblePosition()==0){
                int paddingTop=(int)(-mHeaderViewHeight+offset);
                mHeaderView.setPadding(0,paddingTop,0,0);

                if(paddingTop>=0&&currentState!=RELEASE_REFRESH){
                        currentState=RELEASE_REFRESH;
                        updateHeader();
                }else if(paddingTop<0&&currentState!=PULL_to_REFRESH){
                        currentState=PULL_to_REFRESH;
                        updateHeader();
                }
                return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if(currentState==PULL_to_REFRESH){
                    mHeaderView.setPadding(0,-mHeaderViewHeight,0,0);
               } else if(currentState==RELEASE_REFRESH){
                    mHeaderView.setPadding(0,0,0,0);
                    currentState=REFRESHING;
                    updateHeader();
                }
                break;
            default:
                break;


        }
        return super.onTouchEvent(ev);
    }

    private void updateHeader() {
        switch (currentState){
            case  PULL_to_REFRESH:
                mHeadArrow.startAnimation(RotateDownAnim);
                mTitle.setText("下拉刷新");
                break;
            case RELEASE_REFRESH:
                mHeadArrow.startAnimation(RotateUpAnim);
                mTitle.setText("释放刷新");
                break;
            case REFRESHING:
                mHeadArrow.clearAnimation();
                mHeadArrow.setVisibility(View.INVISIBLE);
                Pb.setVisibility(View.VISIBLE);
                mTitle.setText("正在刷新");
                if(mListener!=null){
                    mListener.onRefresh();
                }
                break;
        }
    }

    private void initHeaderView() {
        mHeaderView = View.inflate(getContext(), R.layout.layout_header_list, null);
        mHeadArrow = mHeaderView.findViewById(R.id.iv_arrow);
        mTitle = (TextView) mHeaderView.findViewById(R.id.tv_title);
        Pb = (ProgressBar) mHeaderView.findViewById(R.id.pb);
        mStateDesc = (TextView) mHeaderView.findViewById(R.id.tv_desc_last_refresh);
        mHeaderView.measure(0,0);
        mHeaderViewHeight = mHeaderView.getMeasuredHeight();
        mHeaderView.setPadding(0,-mHeaderViewHeight,0,0);


       addHeaderView(mHeaderView);
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public void onRefreshComplete() {
        currentState=PULL_to_REFRESH;
        mTitle.setText("下拉刷新");
        mHeaderView.setPadding(0,-mHeaderViewHeight,0,0);
        Pb.setVisibility(View.INVISIBLE);
        mHeadArrow.setVisibility(View.INVISIBLE);
        String time=getTime();
        mStateDesc.setText("最后刷新时间"+time);
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public String getTime() {
        long currentTimeMillis=System.currentTimeMillis();
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        return format.format(currentTimeMillis);
    }

    public interface OnRefreshListener{
        void onRefresh();
    }
    public void setRefreshListener(OnRefreshListener mListener){
        this.mListener =mListener;

    }
}
