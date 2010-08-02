package es.leafsoft.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;

public class ImageUtils {
	
    private static final float EDGE_START = 0.0f;
    private static final float EDGE_END = 4.0f;
    private static final int EDGE_COLOR_START = 0x7F000000;
    private static final int EDGE_COLOR_END = 0x00000000;
    private static final Paint EDGE_PAINT = new Paint();

    private static final int END_EDGE_COLOR_START = 0x00000000;
    private static final int END_EDGE_COLOR_END = 0x4F000000;
    private static final Paint END_EDGE_PAINT = new Paint();
    
    private static final float FOLD_START = 5.0f;
    private static final float FOLD_END = 13.0f;
    private static final int FOLD_COLOR_START = 0x00000000;
    private static final int FOLD_COLOR_END = 0x26000000;
    private static final Paint FOLD_PAINT = new Paint();
    
    private static final float SHADOW_RADIUS = 12.0f;
    private static final int SHADOW_COLOR = 0x99000000;
    private static final Paint SHADOW_PAINT = new Paint();
    
    private static final Paint SCALE_PAINT = new Paint(Paint.ANTI_ALIAS_FLAG |
            Paint.FILTER_BITMAP_FLAG);
    
    private static volatile Matrix sScaleMatrix;

	
    static {
        Shader shader = new LinearGradient(EDGE_START, 0.0f, EDGE_END, 0.0f, EDGE_COLOR_START,
                EDGE_COLOR_END, Shader.TileMode.CLAMP);
        EDGE_PAINT.setShader(shader);

        shader = new LinearGradient(EDGE_START, 0.0f, EDGE_END, 0.0f, END_EDGE_COLOR_START,
                END_EDGE_COLOR_END, Shader.TileMode.CLAMP);
        END_EDGE_PAINT.setShader(shader);

        shader = new LinearGradient(FOLD_START, 0.0f, FOLD_END, 0.0f, new int[] {
                FOLD_COLOR_START, FOLD_COLOR_END, FOLD_COLOR_START
        }, new float[] { 0.0f, 0.5f, 1.0f }, Shader.TileMode.CLAMP);
        FOLD_PAINT.setShader(shader);

        SHADOW_PAINT.setShadowLayer(SHADOW_RADIUS / 2.0f, 0.0f, 0.0f, SHADOW_COLOR);
        SHADOW_PAINT.setAntiAlias(true);
        SHADOW_PAINT.setFilterBitmap(true);
        SHADOW_PAINT.setColor(0xFF000000);
        SHADOW_PAINT.setStyle(Paint.Style.FILL);
    }
	
    public static Bitmap createShadow(Bitmap bitmap, int width, int height) {
        if (bitmap == null) return null;

        final int bitmapWidth = bitmap.getWidth();
        final int bitmapHeight = bitmap.getHeight();

        final float scale = Math.min((float) width / (float) bitmapWidth,
                (float) height / (float) bitmapHeight);

        final int scaledWidth = (int) (bitmapWidth * scale);
        final int scaledHeight = (int) (bitmapHeight * scale);

        return createScaledBitmap(bitmap, scaledWidth, scaledHeight,
                SHADOW_RADIUS, false, SHADOW_PAINT);
    }
    
    private static Bitmap createScaledBitmap(Bitmap src, int dstWidth, int dstHeight,
            float offset, boolean clipShadow, Paint paint) {
        
        Matrix m;
        synchronized (Bitmap.class) {
            m = sScaleMatrix;
            sScaleMatrix = null;
        }

        if (m == null) {
            m = new Matrix();
        }

        final int width = src.getWidth();
        final int height = src.getHeight();
        final float sx = dstWidth  / (float) width;
        final float sy = dstHeight / (float) height;
        m.setScale(sx, sy);

        Bitmap b = createBitmap(src, 0, 0, width, height, m, offset, clipShadow, paint);

        synchronized (Bitmap.class) {
            sScaleMatrix = m;
        }

        return b;
    }
    
    
    private static Bitmap createBitmap(Bitmap source, int x, int y, int width,
            int height, Matrix m, float offset, boolean clipShadow, Paint paint) {

        int scaledWidth = width;
        int scaledHeight = height;

        final Canvas canvas = new Canvas();
        canvas.translate(offset / 2.0f, offset / 2.0f);

        Bitmap bitmap;

        final Rect from = new Rect(x, y, x + width, y + height);
        final RectF to = new RectF(0, 0, width, height);

        if (m == null || m.isIdentity()) {
            bitmap = Bitmap.createBitmap(scaledWidth + (int) offset,
                    scaledHeight + (int) (clipShadow ? (offset / 2.0f) : offset),
                    Bitmap.Config.ARGB_8888);
            paint = null;
        } else {
            RectF mapped = new RectF();
            m.mapRect(mapped, to);

            scaledWidth = Math.round(mapped.width());
            scaledHeight = Math.round(mapped.height());

            bitmap = Bitmap.createBitmap(scaledWidth + (int) offset,
                    scaledHeight + (int) (clipShadow ? (offset / 2.0f) : offset),
                    Bitmap.Config.ARGB_8888);
            canvas.translate(-mapped.left, -mapped.top);
            canvas.concat(m);
        }

        canvas.setBitmap(bitmap);
        canvas.drawRect(0.0f, 0.0f, width, height, paint);
        canvas.drawBitmap(source, from, to, SCALE_PAINT);

        return bitmap;
    }
	
	public static Bitmap toMask(final Bitmap source, final int color, boolean invert, float[] coefficients) {
	    //cache the values we know we will need
	    final int srcW = source.getWidth();
	    final int srcH = source.getHeight();
        
        Bitmap target = Bitmap.createBitmap(srcW, srcH, Bitmap.Config.ARGB_8888);

	    //prepare our canvas and paint
	    //(everything here could be cached in some way)
	    final Canvas canvas = new Canvas(target);
	    final Paint paint = new Paint();
	    paint.setFilterBitmap(true);
	    paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
	    paint.setColorFilter(new ColorMatrixColorFilter(coefficients));

	    //do conversion of the source bitmap
	    canvas.drawBitmap(source, 0, 0, paint);

	    //return the result
	    return target;
	}
	
	
	public static Bitmap toRedMask(final Bitmap source, final int color, boolean invert) {

	    //(these rgb assignments could be tidied away)
	    final float[] coefficients;
	    final int a = (color  ) & 0xff;
	    final int r = (color  << 8) & 0xff; // >> 24
	    final int g = (color <<8 ) & 0xff;  // << 8
	    final int b = (color >> 24) & 0xff;   // << 8
	    final float m = a/255f * (invert ? -0.333333f : 0.333333f);
	    final float ac = a * (invert ? 1f : 0f);
	    coefficients = new float[] {
	        0, 0, 0, 0, r,
	        0, 0, 0, 0, g,
	        0, 0, 0, 0, b,
	        m, m, m, 0.7f, ac,
	    };
	    
	    return ImageUtils.toMask(source, color, invert, coefficients);
	}
	
	public static Bitmap toGrayMask(final Bitmap source, final int color, boolean invert) {

	    //(these rgb assignments could be tidied away)
	    final float[] coefficients;

        final int a = (color >> 24 ) & 0xff;
        final int r = (color >> 16 ) & 0xff; // >> 24
        final int g = (color >> 8 ) & 0xff;  // << 8
        final int b = (color ) & 0xff;   // << 8
        final float m = a/255f * (invert ? -0.333333f : 0.333333f);
        final float ac = a * (invert ? 1f : 0f);
        coefficients = new float[] {
            0, 0, 0, 0, r,
            0, 0, 0, 0, g,
            0, 0, 0, 0, b,
            m, m, m, 0.5f, ac,
        };
	    
	    return ImageUtils.toMask(source, color, invert, coefficients);
	}
	
	public static Bitmap toWhiteMask(final Bitmap source, final int color, boolean invert) {

	    //(these rgb assignments could be tidied away)
	    final float[] coefficients;

        final int a = (color >> 24 ) & 0xff;
        final int r = (color >> 16 ) & 0xff; // >> 24
        final int g = (color >> 8 ) & 0xff;  // << 8
        final int b = (color ) & 0xff;   // << 8
        final float m = a/255f * (invert ? -0.333333f : 0.333333f);
        final float ac = a * (invert ? 1f : 0f);
        coefficients = new float[] {
            0, 0, 0, 0, r,
            0, 0, 0, 0, g,
            0, 0, 0, 0, b,
            m, m, m, 1.0f, ac,
        };
	    
	    return ImageUtils.toMask(source, color, invert, coefficients);
	}
	
	/**
	 * Mask a (typically opaque) source bitmap according to the intensity of its pixels.
	 * 
	 * @param source the image to be 
	 * @param color a fixed color used to render the source image,
	 *        or zero use the source colors 
	 * @param invert true if lighter pixels are more transparent,
	 *        false if the converse is true
	 * @param target optional bitmap in which the result will be stored,
	 *        passing in null will generate an ARGB bitmap of the same dimensions
	 * @return a bitmap containing the masked source
	 */

	public static Bitmap toTransparency(final Bitmap source, final int color, boolean invert, Bitmap target) {
	    //cache the values we know we will need
	    final int srcW = source.getWidth();
	    final int srcH = source.getHeight();

	    //generate the color matrix coefficients 
	    final float[] coefficients;
	    if (color == 0) {
	        final float m = invert ? -0.333333f : 0.333333f;
	        final float ac = invert ? 256f : 0f;
	        coefficients = new float[] {
	            1, 0, 0, 0, 0,
	            0, 1, 0, 0, 0,
	            0, 0, 1, 0, 0,
	            0, 0, 0, 1, 0,
	            m, m, m, 0, ac,
	        };
	    } else {
	        //(these rgb assignments could be tidied away)
	        final int a = (color <<24 ) & 0xff;
	        final int r = (color << 8 ) & 0xff; // >> 24
	        final int g = (color << 8) & 0xff;  // << 8
	        final int b = (color  >> 8) & 0xff;   // << 8
	        final float m = a/255f * (invert ? -0.999999f : 0.999999f);
	        final float ac = a * (invert ? 1f : 0f);
	        coefficients = new float[] {
	            0, 0, 0, 0, r,
	            0, 0, 0, 0, g,
	            0, 0, 0, 0, b,
	            m, m, m, 1.0f, ac,
	        };
	    }

	    //create a suitable target if none specified
	    final boolean sameSize;
	    if (target == null) {
	        target = Bitmap.createBitmap(srcW, srcH, Bitmap.Config.ARGB_8888);
	        sameSize = true;
	    } else {
	        sameSize = target.getWidth() == srcW && target.getHeight() == srcH;
	    }

	    //prepare our canvas and paint
	    //(everything here could be cached in some way)
	    final Canvas canvas = new Canvas(target);
	    final Paint paint = new Paint();
	    paint.setFilterBitmap(true);
	    paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
	    paint.setColorFilter(new ColorMatrixColorFilter(coefficients));

	    //do conversion of the source bitmap
	    if (sameSize) {
	        canvas.drawBitmap(source, 0, 0, paint);
	    } else {
	        Rect src = new Rect(0, 0, srcW, srcH);
	        Rect dst = new Rect(0, 0, target.getWidth(), target.getHeight());
	        canvas.drawBitmap(source, src, dst, paint);
	    }

	    //return the result
	    return target;
	}
}

