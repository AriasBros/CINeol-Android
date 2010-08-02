package es.leafsoft.cineol;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.RelativeLayout;


public class DARelativeLayout extends RelativeLayout {

	private int index = -1;
	private DARelativeLayoutDelegate delegate = null;
	
	
	public DARelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public DARelativeLayoutDelegate getDelegate() {
		return delegate;
	}

	public void setDelegate(DARelativeLayoutDelegate delegate) {
		this.delegate = delegate;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int tag) {
		this.index = tag;
	}


	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		
		if (ev.getAction() == MotionEvent.ACTION_UP)
			delegate.didTouchEvent(this);
		
		return true;
	}
}
