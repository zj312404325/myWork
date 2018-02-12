package com.example.administrator.jymall.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.jymall.R;
import com.example.administrator.jymall.util.FormatUtil;

/**
 * 自定义组件：购买数量，带减少增加按钮
 * Created by hiwhitley on 2016/7/4.
 */
public class AmountView extends LinearLayout {

    public double getAmount() {
		return FormatUtil.toDouble(etAmount.getText());
	}

	public void setAmount(double amount) {
		 etAmount.setText(FormatUtil.toString1(amount));
		this.amount = amount;
	}


	private static final String TAG = "AmountView";
    private double amount = 1; //购买数量
    private double goods_storage = 1; //商品库存
    private double goods_min = 1; //商品起订量
    
    private OnAmountChangeListener mListener;

    private EditText etAmount;
    private Button btnDecrease;
    private Button btnIncrease;
    
    private boolean f=false;
    
    

    public boolean isF() {
		return f;
	}

	public void setF(boolean f) {
		this.f = f;
	}

	public AmountView(Context context) {
        this(context, null);
    }

    public AmountView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.view_amount, this);
        etAmount = (EditText) findViewById(R.id.etAmount);
        btnDecrease = (Button) findViewById(R.id.btnDecrease);
        btnIncrease = (Button) findViewById(R.id.btnIncrease);
        btnDecrease.setOnClickListener(new btnonClick());
        btnIncrease.setOnClickListener(new btnonClick());
       // etAmount.addTextChangedListener(new txtChanged());
        
       etAmount.setOnEditorActionListener(new TextView.OnEditorActionListener() {

			@SuppressLint("NewApi")
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event)  {				
				if(KeyEvent.KEYCODE_ENTER == actionId || actionId == EditorInfo.IME_ACTION_DONE){
					if (etAmount.getText().toString().isEmpty())
						return false;
			        amount = Double.valueOf(etAmount.getText().toString());
			        if (amount > goods_storage) {
			            etAmount.setText(FormatUtil.toString1(goods_storage));
			            amount = goods_storage;
			            return false;
			        }
			        if(amount < goods_min){
			        	etAmount.setText(FormatUtil.toString1(goods_min));
			        	amount = goods_min;
			        	return false;
			        }
			        if (mListener != null ) {
			            mListener.onAmountChange1(v, amount);
			        }
				}
				return false;
			}
		
		
       });
       

        TypedArray obtainStyledAttributes = getContext().obtainStyledAttributes(attrs, R.styleable.AmountView);
        int btnWidth = obtainStyledAttributes.getDimensionPixelSize(R.styleable.AmountView_btnWidth, LayoutParams.WRAP_CONTENT);
        int tvWidth = obtainStyledAttributes.getDimensionPixelSize(R.styleable.AmountView_tvWidth, 80);
        int tvTextSize = obtainStyledAttributes.getDimensionPixelSize(R.styleable.AmountView_tvTextSize, 0);
        int btnTextSize = obtainStyledAttributes.getDimensionPixelSize(R.styleable.AmountView_btnTextSize, 0);
        obtainStyledAttributes.recycle();

        /*LayoutParams btnParams = new LayoutParams(btnWidth, LayoutParams.MATCH_PARENT);
        btnDecrease.setLayoutParams(btnParams);
        btnIncrease.setLayoutParams(btnParams);*/
        if (btnTextSize != 0) {
            btnDecrease.setTextSize(TypedValue.COMPLEX_UNIT_PX, btnTextSize);
            btnIncrease.setTextSize(TypedValue.COMPLEX_UNIT_PX, btnTextSize);
        }

       /* LayoutParams textParams = new LayoutParams(tvWidth, LayoutParams.MATCH_PARENT);
        etAmount.setLayoutParams(textParams);*/
        if (tvTextSize != 0) {
            etAmount.setTextSize(tvTextSize);
        }
    }

    public void setOnAmountChangeListener(OnAmountChangeListener onAmountChangeListener) {
        this.mListener = onAmountChangeListener;
    }

    public void setGoods_storage(double goods_storage) {
        this.goods_storage = goods_storage;
    }
    
    public void setGoods_min(double goods_min){
    	this.goods_min = goods_min;
    }
    
    public class btnonClick implements OnClickListener{

		@Override
		public void onClick(View v) {
			int i = v.getId();
	        if (i == R.id.btnDecrease) {
	            if (amount > goods_min) {
	                amount--;
	                etAmount.setText(FormatUtil.toString1(amount));
	            }
	        } else if (i == R.id.btnIncrease) {
	            if (amount < goods_storage) {
	                amount++;
	                etAmount.setText(FormatUtil.toString1(amount));
	            }
	        }

	        etAmount.clearFocus();

	        if (mListener != null ) {
	            mListener.onAmountChange(v, amount);
	        }
			
		}
    	
    }
    
    public class txtChanged implements TextWatcher{

		@SuppressLint("NewApi")
		@Override
		public void afterTextChanged(Editable s) {
			if (s.toString().isEmpty())
	            return;
	        amount = Double.valueOf(s.toString());
	        if (amount > goods_storage) {
	            etAmount.setText(FormatUtil.toString1(goods_storage ));
	            return;
	        }
	        if(amount < goods_min){
	        	etAmount.setText(FormatUtil.toString1(goods_min));
	            return;
	        }

	        if (mListener != null && f) {
	        	f= false;
	            mListener.onAmountChange1(null, amount);
	        }
			
		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			// TODO Auto-generated method stub
			f = true;
			
		}

		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			// TODO Auto-generated method stub
			
		}
    	
    }



    public interface OnAmountChangeListener {
        void onAmountChange(View view, double amount);
        
        void onAmountChange1(View view, double amount);
    }

}
