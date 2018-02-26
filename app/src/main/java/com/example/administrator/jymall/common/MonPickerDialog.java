package com.example.administrator.jymall.common;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

/**
 * Created by Administrator on 2018-02-23.
 */

public class MonPickerDialog extends DatePickerDialog {
    @SuppressLint("NewApi")
    public MonPickerDialog(Context context, OnDateSetListener callBack,
                           int year, int monthOfYear, int dayOfMonth) {
        super(context, callBack, year, monthOfYear, dayOfMonth);
        this.setTitle(year + "年" + (monthOfYear + 1) + "月");


        ((ViewGroup) ((ViewGroup) this.getDatePicker().getChildAt(0))
                .getChildAt(0)).getChildAt(2).setVisibility(View.GONE);

    }

    @Override
    public void onDateChanged(DatePicker view, int year, int month, int day) {
        super.onDateChanged(view, year, month, day);
        this.setTitle(year + "年" + (month + 1) + "月");
    }


}
