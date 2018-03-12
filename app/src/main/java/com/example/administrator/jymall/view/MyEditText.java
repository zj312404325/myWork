package com.example.administrator.jymall.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.InputFilter;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.administrator.jymall.R;
import com.example.administrator.jymall.util.CommonUtil;

public class MyEditText extends RelativeLayout {

	private ImageView input_img;
	private EditText input_txt;
	private LinearLayout input_text_b;
	
	
	public MyEditText(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public MyEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs,  
                R.styleable.MyView);
        int imgBg =  a.getResourceId(R.styleable.MyView_imgBg, 0);
        int isfouce =  a.getResourceId(R.styleable.MyView_isfouce, 0);
        View view;
        if(imgBg>0){
        	view = View.inflate(getContext(), R.layout.my_edittext, this);
        	input_img = view.findViewById(R.id.input_img);
        	input_img.setImageResource(imgBg);
        }
        else{
        	view = View.inflate(getContext(), R.layout.my_edittext1, this);
        }
        
       
        input_txt =view.findViewById(R.id.input_txt);
        
          
        
        
        input_text_b = view.findViewById(R.id.input_text_b);
        if(isfouce == 0){
        	input_text_b.setBackgroundResource(R.drawable.input_g);
        }
        
        int textColor = a.getColor(R.styleable.MyView_textColor,  
                0XFF000000);  
        float textSize = a.getFloat(R.styleable.MyView_textSize, 14);  
        
        int maxLength = a.getInt(R.styleable.MyView_maxLength, 200);
        
      
        String text = a.getString(R.styleable.MyView_text);
        String hint = a.getString(R.styleable.MyView_hint);
        int ispwd = a.getInt(R.styleable.MyView_ispwd,0);
        
        input_txt.setTextColor(textColor);
        input_txt.setText(text);
        input_txt.setHint(hint);
		input_txt.setSingleLine(true);
        input_txt.setTextSize(TypedValue.COMPLEX_UNIT_DIP,textSize);
        if(ispwd==1){
        	input_txt.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
        InputFilter[] filters = {new InputFilter.LengthFilter(maxLength)};  
        input_txt.setFilters(filters);
       
        
        input_txt.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@SuppressLint("NewApi")
			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				if(arg1){
					input_text_b.setBackground(CommonUtil.getDrawable(R.drawable.input_b));
				}
				else{
					input_text_b.setBackground(CommonUtil.getDrawable(R.drawable.input_g));
				}
				
			}
		});
	}
	
	public void setText(String v){
		input_txt.setText(v);
	}
	
	public String getText(){
		return input_txt.getText().toString();
	}

	public EditText getEditeText(){
		return input_txt;
	}
}
