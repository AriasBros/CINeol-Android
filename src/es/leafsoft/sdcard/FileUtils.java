package es.leafsoft.sdcard;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;

public class FileUtils {

	private static final int BUFFER_SIZE_NORMAL = 1024;
	
	public static final void copy(String inputPath, String outputPath, int bufferSize) throws FileNotFoundException, IOException {
		
		FileInputStream inputStream = new FileInputStream(inputPath);
		FileOutputStream outputStream = new FileOutputStream(outputPath);
		
		byte[] buffer = new byte[bufferSize];
		int length;
    	while ((length = inputStream.read(buffer))>0){
    		outputStream.write(buffer, 0, length);
    	}
		
    	outputStream.flush();
    	outputStream.close();
		inputStream.close();
	}

	private static final boolean writeBitmapToPath(Bitmap bitmap, String path, CompressFormat format, int quality)
	throws FileNotFoundException, IOException
	{
		// Creamos el fichero.
		File file = new File(path);
		file.createNewFile();
		
		// Y guardamos en el la imagen.
		FileOutputStream outputStream = new FileOutputStream(path);
		boolean result = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
		
    	outputStream.flush();
    	outputStream.close();
    	
    	return result;
	}
	
	public static final boolean writeJPEG(Bitmap bitmap, String path) throws FileNotFoundException, IOException {		
		return writeBitmapToPath(bitmap, path, CompressFormat.JPEG, 100);
	}
}
