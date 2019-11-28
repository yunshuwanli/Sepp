package com.priv.sepp;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.List;

public class SwitchVideoTypeDialog extends Dialog {
    private ArrayAdapter<SwitchVideoModel> adapter = null;
    private List<SwitchVideoModel> data;
    private ListView listView = null;
    private Context mContext;
    private OnListItemClickListener onItemClickListener;

    private class OnItemClickListener implements AdapterView.OnItemClickListener {
        private OnItemClickListener() {
        }

        public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
            SwitchVideoTypeDialog.this.dismiss();
            SwitchVideoTypeDialog.this.onItemClickListener.onItemClick(i);
        }
    }

    public interface OnListItemClickListener {
        void onItemClick(int i);
    }

    public SwitchVideoTypeDialog(Context context) {
        super(context, R.style.dialog_style);
        this.mContext = context;
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    public void initList(List<SwitchVideoModel> list, OnListItemClickListener onListItemClickListener) {
        this.onItemClickListener = onListItemClickListener;
        this.data = list;
        View inflate = LayoutInflater.from(this.mContext).inflate(R.layout.switch_video_dialog, null);
        this.listView = (ListView) inflate.findViewById(R.id.switch_dialog_list);
        setContentView(inflate);
        this.adapter = new ArrayAdapter(this.mContext, R.layout.switch_video_dialog_item, list);
        this.listView.setAdapter(this.adapter);
        this.listView.setOnItemClickListener(new OnItemClickListener());
        Window window = getWindow();
        LayoutParams attributes = window.getAttributes();
        attributes.width = (int) (((double) this.mContext.getResources().getDisplayMetrics().widthPixels) * 0.5d);
        window.setAttributes(attributes);
    }
}
