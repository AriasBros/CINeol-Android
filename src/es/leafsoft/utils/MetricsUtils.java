package es.leafsoft.utils;

import android.content.Context;

public class MetricsUtils {

	public static int dipsToPixels(float dips, float scale) {
		return (int) (dips * scale + 0.5f);
	}
	
	public static int dipsToPixels(Context context, float dips) {
		return MetricsUtils.dipsToPixels(context.getResources().getDisplayMetrics().density, dips);
	}
}
