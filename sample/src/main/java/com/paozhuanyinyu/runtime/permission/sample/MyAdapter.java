package com.paozhuanyinyu.runtime.permission.sample;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.paozhuanyinyu.runtime.sample.R;

/**
 * Created by Administrator on 2017/11/6.
 */

public class MyAdapter extends BaseAdapter {
    private String[] permissionDesc;
    private Activity mActivity;
    public MyAdapter(Activity activity,String[] permissions){
        this.permissionDesc = permissions;
        this.mActivity = activity;
    }
    @Override
    public int getCount() {
        return permissionDesc.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if(convertView==null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mActivity).inflate(R.layout.listview_item,null);
            viewHolder.bt_permission = (Button) convertView.findViewById(R.id.bt_permission);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.bt_permission.setText(permissionDesc[position]);
        viewHolder.bt_permission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onClick(position);
            }
        });
        viewHolder.bt_permission.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mOnItemClickListener.onLongClick(position);
                return true;
            }
        });
        return convertView;
    }
    /**
     * 删除按钮的监听接口
     */
    public interface onItemClickListener {
        void onClick(int i);
        void onLongClick(int i);
    }

    private onItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(onItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }
    public static class ViewHolder{
        Button bt_permission;
    }
}
