package com.paozhuanyinyu.runtime.permission.dialog;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

import com.paozhuanyinyu.rxpermissions.R;

/**
 * @Description:
 * @author:cuiwei get from Tienfook Chang
 * @see:
 * @since:
 * @copyright © 35.com
 * @Date:2012-9-7
 */
public class MyDialog extends Dialog {

    public MyDialog(Context context, int theme) {
        super(context, theme);
    }

    public MyDialog(Context context) {
        super(context);
    }

    public static class Builder {

        private Context context;
        private String title;
        private CharSequence message;
        private int messageColor;
        private CharSequence content;
        private int contentColor;
        private String positiveButtonText;
        private String negativeButtonText;
        private OnClickListener positiveButtonClickListener, negativeButtonClickListener;

        public Builder(Context context) {
            this.context = context;

        }

        public Builder setMessage(CharSequence message) {
            this.message = message;

            return this;

        }

        public Builder setMessage(int message) {
            this.message = (String) context.getText(message);

            return this;

        }

        public Builder setMessageColor(String color) {
            this.messageColor = Color.parseColor(color);

            return this;

        }

        public Builder setMessageColor(int color) {
            this.messageColor = context.getResources().getColor(color);

            return this;

        }

        public Builder setContent(CharSequence content) {
            this.content = content;

            return this;

        }

        public Builder setContent(int content) {
            this.content = (String) context.getText(content);

            return this;

        }

        public Builder setContentColor(String color) {
            this.contentColor = Color.parseColor(color);

            return this;

        }

        public Builder setContentColor(int color) {
            this.contentColor = context.getResources().getColor(color);

            return this;

        }

        public Builder setTitle(int title) {
            this.title = (String) context.getText(title);

            return this;

        }

        public Builder setTitle(String title) {
            this.title = title;

            return this;

        }

        public Builder setPositiveButton(int positiveButtonText,

                                         OnClickListener listener) {

            this.positiveButtonText = (String) context

                    .getText(positiveButtonText);

            this.positiveButtonClickListener = listener;

            return this;

        }

        public Builder setPositiveButton(String positiveButtonText,

                                         OnClickListener listener) {

            this.positiveButtonText = positiveButtonText;

            this.positiveButtonClickListener = listener;

            return this;

        }

        /**
         * @param negativeButtonText
         * @param listener
         * @return
         * @Description:初始化dialog中第二个按钮的文字和点击事件
         * @see:
         * @since:
         * @author: hanlx
         * @date:2013-2-21
         */
        public Builder setNegativeButton(int negativeButtonText,

                                         OnClickListener listener) {
            this.negativeButtonText = (String) context.getText(negativeButtonText);
            this.negativeButtonClickListener = listener;

            return this;

        }

        public Builder setNegativeButton(String negativeButtonText,

                                         OnClickListener listener) {
            this.negativeButtonText = negativeButtonText;
            this.negativeButtonClickListener = listener;

            return this;

        }

        public MyDialog build() {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            final MyDialog dialog = new MyDialog(context, R.style.verifyDialog);

            View layout = inflater.inflate(R.layout.xpermission_dialog_mail, null);
            dialog.addContentView(layout, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

            if (!TextUtils.isEmpty(title)) {
                ((TextView) layout.findViewById(R.id.title)).setText(title);
            }
            if (!TextUtils.isEmpty(message)) {
                ((TextView) layout.findViewById(R.id.tv_message)).setText(message);
                if (messageColor != 0) {
                    ((TextView) layout.findViewById(R.id.tv_message)).setTextColor(messageColor);
                }
            }
            if (!TextUtils.isEmpty(content)) {
                ((TextView) layout.findViewById(R.id.tv_content)).setText(content);
                if (contentColor != 0) {
                    ((TextView) layout.findViewById(R.id.tv_content)).setTextColor(contentColor);
                }
            } else {
                ((TextView) layout.findViewById(R.id.tv_content)).setVisibility(View.GONE);
            }

            if (positiveButtonText != null) {
                ((TextView) layout.findViewById(R.id.positiveButton)).setText(positiveButtonText);
                if (negativeButtonText == null) {
                    ((TextView) layout.findViewById(R.id.positiveButton)).setBackgroundResource(R.drawable.xpermission_selector_btn_dialog_bottom_radius_day);
                    layout.findViewById(R.id.view_line).setVisibility(View.GONE);
                }

                if (positiveButtonClickListener != null) {
                    ((TextView) layout.findViewById(R.id.positiveButton)).setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            positiveButtonClickListener.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
                        }
                    });
                }
            } else {
                layout.findViewById(R.id.positiveButton).setVisibility(View.GONE);

            }

            if (negativeButtonText != null) {
                ((TextView) layout.findViewById(R.id.negativeButton)).setText(negativeButtonText);

                if (negativeButtonClickListener != null) {
                    ((TextView) layout.findViewById(R.id.negativeButton)).setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            negativeButtonClickListener.onClick(dialog, DialogInterface.BUTTON_NEGATIVE);

                        }
                    });
                }
            } else {
                layout.findViewById(R.id.negativeButton).setVisibility(View.GONE);
            }

            if (positiveButtonText == null && negativeButtonText == null) {
                layout.findViewById(R.id.positiveButtonLayout).setVisibility(View.GONE);
            }
            dialog.setContentView(layout);
            return dialog;

        }

    }

}
