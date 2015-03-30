package org.bitnp.netcheckin2.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

/**
 * Created by langley on 3/27/15.
 */
public class CustomEditText extends EditText{

    private Drawable clearDrawable;
    private boolean isHasFocus;
    Drawable [] drawables;

    public CustomEditText(Context context) {
        super(context);
        init();
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){

        // get left, top, right, bottom
        drawables = getCompoundDrawables();
        clearDrawable = drawables[2];

        this.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                isHasFocus = hasFocus;
                if(isHasFocus){
                    setClearDrawableVisibility(getText().toString().length() >= 1);
                } else {
                    setClearDrawableVisibility(false);
                }
            }
        });

        this.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                boolean isVisable = getText().toString().length() >= 1;
                setClearDrawableVisibility(isVisable);
            }
        });

        setClearDrawableVisibility(false);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_UP:
                float x = event.getX();
                if(x > (getWidth() - getTotalPaddingRight())
                        && x < (getWidth() - getPaddingRight()))
                    setText("");
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    private void setClearDrawableVisibility(boolean visibility){
        // why not work?
        //clearDrawable.setVisible(visibility, false);
        Drawable temp;
        if(visibility)
            temp = clearDrawable;
        else
            temp = null;
        setCompoundDrawables(drawables[0], drawables[1], temp, drawables[3]);
    }
}
