
package com.mzs.market.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.view.View;
import com.mzs.market.R;
import com.mzs.market.config.AppConfig;

/**
 * @category 公共弹出框
 * @author Administrator
 *
 */
public class CommonDialog extends Dialog implements View.OnClickListener {

    private TextView tvTitle, tvMsg;
    private Button leftBtn, rightBtn;
    private BtnClickListener leftListener, rightListener;

    public CommonDialog(Context context) {
        super(context, R.style.common_dialog);
        setContentView(R.layout.dialog_common);
        initView();
        setProperty();
    }

    private void setProperty() {
        Window window = getWindow();
        window.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams p = window.getAttributes();
        p.height = WindowManager.LayoutParams.WRAP_CONTENT;
        p.width = AppConfig.width * 4 / 5;
        window.setAttributes(p);
        setCanceledOnTouchOutside(false);
    }

    private void initView() {
        tvTitle = (TextView) findViewById(R.id.title_tv);
        tvMsg = (TextView) findViewById(R.id.tv_msg);
        leftBtn = (Button) findViewById(R.id.btn_left);
        rightBtn = (Button) findViewById(R.id.btn_right);
        leftBtn.setOnClickListener(this);
        rightBtn.setOnClickListener(this);
    }

    public CommonDialog setTitle(String title) {
        if (title == null) {
            title = "";
        }
        if (tvTitle != null) {
            tvTitle.setText(title);
        }
        return this;
    }

    public CommonDialog setMsg(String msg) {
        if (msg == null) {
            msg = "";
        }
        if (tvMsg != null) {
            tvMsg.setText(msg);
        }
        return this;
    }

    public CommonDialog setLeftBtn(String leftMsg, BtnClickListener listener) {
        if (leftMsg == null) {
            leftMsg = "";
        }
        if (leftBtn != null) {
            leftBtn.setText(leftMsg);
        }
        this.leftListener = listener;
        return this;
    }

    public CommonDialog setRightBtn(String rightMsg, BtnClickListener listener) {
        if (rightMsg == null) {
            rightMsg = "";
        }
        if (rightBtn != null) {
            rightBtn.setText(rightMsg);
        }
        this.rightListener = listener;
        return this;
    }

    public interface BtnClickListener {
        void onBtnClick(Dialog dialog, int id);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_left:
                if (leftListener != null)
                    leftListener.onBtnClick(this, R.id.btn_left);
                break;
            case R.id.btn_right:
                if (rightListener != null)
                    rightListener.onBtnClick(this, R.id.btn_right);
                break;
        }
    }

}
