package com.example.emoji;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class CustomTodayMemoAdapter extends BaseAdapter {

    ArrayList<CustomTodayMemo> listViewItemList = new ArrayList<>();
    String pre_list = "";

    @Override
    public int getCount() {
        return listViewItemList.size();
    }

    @Override
    public Object getItem(int i) {
        return listViewItemList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        Context mcontext = viewGroup.getContext();
        ViewHolder holder;

        if(view == null){
            holder = new ViewHolder();

            LayoutInflater inflater = (LayoutInflater) mcontext.getSystemService(mcontext.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.customdatashow, null);

            holder.tv_mydate = view.findViewById(R.id.tv_mydate);
            holder.tv_hcmemo = view.findViewById(R.id.tv_hcmemo);

            view.setTag(holder);
        }
        else
            holder = (ViewHolder) view.getTag();

        CustomTodayMemo custommemo = (CustomTodayMemo) getItem(i);

        holder.tv_mydate.setText(custommemo.getMydate());
        holder.tv_hcmemo.setText(custommemo.getHcmemo());

        return view;
    }

    class ViewHolder{
        public TextView tv_mydate;
        public TextView tv_hcmemo;
    }

    public void addData(String _ficon, String _mydate, String _hcmemo){
        //parse로 전달받은 데이터들을 customTodayMemo 객체로 만들어주고
        CustomTodayMemo ctm = new CustomTodayMemo(_ficon, _mydate, _hcmemo);
        //위에서 만든 ArrayList에 추가해준다.
;

        if(pre_list.equals(_ficon)){
            listViewItemList.add(ctm);
        }
        else {
            listViewItemList.clear();
            listViewItemList.add(ctm);
        }
        pre_list = _ficon;


    }


}
