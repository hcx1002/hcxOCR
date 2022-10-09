package com.hcx.ocrapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.daimajia.swipe.implments.SwipeItemRecyclerMangerImpl;
import com.hcx.ocrapp.R;
import com.hcx.ocrapp.modle.WordData;
import com.hcx.ocrapp.sqlite.WordDB;
import com.hcx.ocrapp.ui.camera.pictureResult;

import java.util.List;

import static android.content.ContentValues.TAG;

public class RecyclerViewAdapter extends RecyclerSwipeAdapter<RecyclerViewAdapter.SimpleViewHolder> {

    private Context context;
    private List<WordData> wordDataList;
    protected SwipeItemRecyclerMangerImpl mItemManger = new SwipeItemRecyclerMangerImpl(this);

    public RecyclerViewAdapter(Context context,List<WordData> wordDataList){
        this.context = context;
        this.wordDataList = wordDataList;
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.swipelayout,parent,false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SimpleViewHolder simpleViewHolder, int i) {
        WordData wordData = wordDataList.get(i);
        simpleViewHolder.swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
        simpleViewHolder.swipeLayout.addSwipeListener(new SimpleSwipeListener(){
            @Override
            public void onOpen(SwipeLayout layout) {
                //YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(layout.findViewById(R.id.trash));
            }
        });
        simpleViewHolder.swipeLayout.setOnDoubleClickListener(new SwipeLayout.DoubleClickListener() {
            @Override
            public void onDoubleClick(SwipeLayout layout, boolean surface) {
                Intent intent = new Intent(context, pictureResult.class);
                intent.putExtra("word",wordData.getWord());
                intent.putExtra("isT","0");
                intent.putExtra("time",wordData.getTime());
                context.startActivity(intent);
            }
        });
        simpleViewHolder.btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemManger.removeShownLayouts(simpleViewHolder.swipeLayout);
                wordDataList.remove(i);
                Log.d(TAG, "onClick: i： "+i+"  id："+wordData.getId());
                WordDB wordDB = new WordDB(context);
                wordDB.deleteWord(wordData.getId());
                //notifyItemChanged(i);
                notifyDataSetChanged();
                notifyItemRangeChanged(i,wordDataList.size());
                mItemManger.closeAllItems();
                //导航到history页面
                //Navigation.findNavController(v).navigate(R.id.navigation_dashboard);
                Toast.makeText(context, "删除成功!", Toast.LENGTH_SHORT).show();
            }
        });
        simpleViewHolder.btnLook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, pictureResult.class);
                intent.putExtra("word",wordData.getWord());
                intent.putExtra("isT","0");
                intent.putExtra("time",wordData.getTime());
                context.startActivity(intent);
            }
        });
        simpleViewHolder.word.setText(wordData.getWord());
        mItemManger.bindView(simpleViewHolder.itemView,i);
    }

    @Override
    public int getItemCount() {
        return wordDataList.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    public static class SimpleViewHolder extends RecyclerView.ViewHolder{
        SwipeLayout swipeLayout;
        TextView word;
        Button btnDel;
        Button btnLook;
        public SimpleViewHolder(@NonNull View itemView) {
            super(itemView);
            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
            word = (TextView) itemView.findViewById(R.id.text_data);
            btnDel = (Button) itemView.findViewById(R.id.delete);
            btnLook = itemView.findViewById(R.id.look);
        }
    }
}
