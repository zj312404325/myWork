package com.example.administrator.jymall.view;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.administrator.jymall.R;

import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.List;

public class ViewPagerAdapter extends PagerAdapter {

    private Context mContext;
    private List<String> mImageIds;
    private ViewpagerOnitemClicklistener mViewpagerOnitemClicklistener;

    public ViewPagerAdapter(Context mContext, List<String> mImageIds, ViewpagerOnitemClicklistener viewpagerOnitemClicklistener) {
        this.mContext = mContext;
        this.mImageIds = mImageIds;
        this.mViewpagerOnitemClicklistener = viewpagerOnitemClicklistener;
    }

    @Override
    public int getCount() {
        // 无限滑动，设置返回为整数最大数
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        // 初始化图片布局
        ImageView view = new ImageView(mContext);
        view.setBackgroundResource(R.drawable.icon_logo);
        //用Xutils加载图片
        ImageOptions options = new ImageOptions.Builder().setLoadingDrawableId(R.drawable.loading).setFailureDrawableId(R.drawable.noimg).build();
        x.image().bind(view, mImageIds.get(position % mImageIds.size()),options);
        container.addView(view);
        //点击监听的回调，给View层使用，要把position回传
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewpagerOnitemClicklistener.setOnitemClicklistener(position % mImageIds.size());
            }
        });
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    //点击监听的接口
    public interface ViewpagerOnitemClicklistener {
        void setOnitemClicklistener(int position);
    }

}
