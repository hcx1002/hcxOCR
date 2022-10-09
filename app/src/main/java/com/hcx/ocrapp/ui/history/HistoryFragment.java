package com.hcx.ocrapp.ui.history;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.daimajia.swipe.util.Attributes;
import com.hcx.ocrapp.R;
import com.hcx.ocrapp.adapter.RecyclerViewAdapter;
import com.hcx.ocrapp.modle.WordData;
import com.hcx.ocrapp.sqlite.WordDB;
import com.hcx.ocrapp.ui.utils.MyGifHistoryView;

import java.util.List;

public class HistoryFragment extends Fragment {

    private List<WordData> wordDataList;
    private RecyclerView wordRecylerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private MyGifHistoryView myGifHistoryView;
    private TextView textView;
    private View layout;
    private String isLogin;

    @Override
    public void onStart() {
        init();
        super.onStart();
    }

    @SuppressLint("ResourceAsColor")
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_history, container, false);
        wordRecylerView = root.findViewById(R.id.apadter_recycler);
        SharedPreferences loginSP = getActivity().getSharedPreferences("login",Context.MODE_PRIVATE);
        isLogin = loginSP.getString("isLogin","");
        if ("".equals(isLogin)){
            myGifHistoryView = root.findViewById(R.id.history_image);
            textView = root.findViewById(R.id.history_onlogin);
            textView.setVisibility(View.VISIBLE);
            myGifHistoryView.setVisibility(View.VISIBLE);
            layout = root.findViewById(R.id.history_layout);
            layout.setVisibility(View.VISIBLE);
        }else {
            init();
            SharedPreferences preferences =getActivity().getSharedPreferences("setup", Context.MODE_PRIVATE);
            String isHelpList = preferences.getString("list","false");
            if (isHelpList==null){
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("list","false");
                editor.commit();
            }else if (isHelpList.equals("false")){
                    confirm_dialog(root);
            }
        }
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
    }
    public void init(){
        WordDB wordDB = new WordDB(getActivity());
        wordDataList = wordDB.findWord(isLogin);
        recyclerViewAdapter = new RecyclerViewAdapter(getActivity(),wordDataList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(),RecyclerView.VERTICAL,false);
        wordRecylerView.setLayoutManager(layoutManager);
        recyclerViewAdapter.setMode(Attributes.Mode.Single);
        wordRecylerView.setAdapter(recyclerViewAdapter);
        wordRecylerView.setOnScrollListener(onScrollListener);
    }
    RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
        }
    };
    public void confirm_dialog(View view){
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setTitle("帮助");
        builder.setMessage("右滑历史列表可删除和查看\n双击历史列表可直接查看");
        builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences preferences =getActivity().getSharedPreferences("setup", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("list","false");
                editor.commit();
            }
        });
        builder.setPositiveButton("不再提示", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SharedPreferences preferences =getActivity().getSharedPreferences("setup", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("list","true");
                editor.commit();
            }
        });
        builder.show();
    }

}