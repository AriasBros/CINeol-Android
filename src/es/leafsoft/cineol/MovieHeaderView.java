package es.leafsoft.cineol;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import es.leafsoft.cineol.R;

public class MovieHeaderView extends RelativeLayout implements OnClickListener {
	
	static private Paint linePaint = new Paint();
	static private Paint backgroundPaint = new Paint();
	static {
        //LinearGradient gradient = new LinearGradient(0, 0, 0, 44, 0xFF93c1e1, 0xFF3E7CBD,
        //											 Shader.TileMode.REPEAT);

        LinearGradient gradient = new LinearGradient(0, 0, 0, 115, 0xFFe8e8e8, 0xFFd0d0d0,
				 									 Shader.TileMode.CLAMP);
		
        backgroundPaint.setShader(gradient);
	}
	
	private final int MARGIN_ERROR = 50;
	private float initialPosX = -1;
	private int previousPosX;
	private float finalPosX = -1;

	private MovieHeaderViewDelegate delegate;
	
	public MovieHeaderView(Context context) {
		super(context);
        //this.setBackgroundColor(0xFF000000);
	}
	
	public MovieHeaderView(Context context, AttributeSet attrs) {
		super(context, attrs);
        //this.setBackgroundColor(0xFF000000);
	}
	
	public MovieHeaderView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
        //this.setBackgroundColor(0xFF000000);
	}
	

	@Override
	protected void onDraw(Canvas canvas) {

		// Dibujamos el fondo de la tabbar.
		linePaint.setColor(0xFFb4b4b4);
		canvas.drawLine(0, this.getHeight() - 1, this.getWidth(), this.getHeight() - 1, linePaint);
		
		linePaint.setColor(0xFFe7e7e7);
		canvas.drawLine(0,  this.getHeight() - 2, this.getWidth(), this.getHeight() - 2, linePaint);		
		
		canvas.drawRect(0, 0, this.getWidth(), this.getHeight() - 2.5f, backgroundPaint);
		
		super.onDraw(canvas);
	}

	
	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			initialPosX = event.getX();
			this.previousPosX = (int) this.initialPosX;
		}
		
		else if(event.getAction() == MotionEvent.ACTION_MOVE) {
			int offset = this.previousPosX - (int)event.getX();
			if (Math.abs(offset) < 10)
				return true;
			
			//this.onTouchMoveEvent(offset);
			this.previousPosX = (int)event.getX();
		}
		else if (event.getAction() == MotionEvent.ACTION_UP) {
			finalPosX = event.getX();

			if (initialPosX - finalPosX > MARGIN_ERROR)
				this.onTouchEventToRightToLeft(event);
			else if (initialPosX - finalPosX < -MARGIN_ERROR)
				this.onTouchEventToLeftToRight(event);
		}
		
		return true;
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
        this.findViewById(R.id.poster_thumb_view).setOnClickListener(this);
	}
	
	private void onTouchMoveEvent(int offset) {
		if (delegate != null)
			delegate.movieHeaderViewDidTouchMove(offset);	
	}

	private void onTouchEventToLeftToRight(MotionEvent event) {
		if (delegate != null)
			delegate.movieHeaderViewDidTouchToLeftToRight();
	}	
	
	private void onTouchEventToRightToLeft(MotionEvent event) {
		if (delegate != null)
			delegate.movieHeaderViewDidTouchToRightToLeft();
	}	
	
	public MovieHeaderViewDelegate getDelegate() {
		return delegate;
	}

	public void setDelegate(MovieHeaderViewDelegate delegate) {
		this.delegate = delegate;
	}

	public void onClick(View v) {
		if (delegate != null && v == this.findViewById(R.id.poster_thumb_view))
			delegate.movieHeaderViewDidTouchOnPosterThumbView();
	}
}