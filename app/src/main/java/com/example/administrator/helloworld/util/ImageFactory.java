package com.example.administrator.helloworld.util;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

/**
 * Image compress factory class
 * 
 * @author 
 *
 */
public class ImageFactory {

	private static String TAG = "ImageFactory";
	/**
	 * Get bitmap from specified image path
	 * 
	 * @param imgPath
	 * @return
	 */
	public Bitmap getBitmap(String imgPath) {
		// Get bitmap through image path
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		newOpts.inJustDecodeBounds = false;
		newOpts.inPurgeable = true;
		newOpts.inInputShareable = true;
		// Do not compress
		newOpts.inSampleSize = 1;
		newOpts.inPreferredConfig = Config.RGB_565;
		return BitmapFactory.decodeFile(imgPath, newOpts);
	}
	
	/**
	 * Store bitmap into specified image path
	 * 
	 * @param bitmap
	 * @param outPath
	 * @throws FileNotFoundException 
	 */
	public void storeImage(Bitmap bitmap, String outPath) throws FileNotFoundException {
		FileOutputStream os = new FileOutputStream(outPath);
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
	}
	
	/**
	 * Compress image by pixel, this will modify image width/height. 
	 * Used to get thumbnail
	 * 
	 * @param imgPath image path
	 * @param pixelW target pixel of width
	 * @param pixelH target pixel of height
	 * @return
	 */
	public Bitmap ratio(String imgPath, float pixelW, float pixelH) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();  
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true，即只读边不读内容
        newOpts.inJustDecodeBounds = true;
        newOpts.inPreferredConfig = Config.RGB_565;
        // Get bitmap info, but notice that bitmap is null now  
        Bitmap bitmap = BitmapFactory.decodeFile(imgPath,newOpts);
          
        newOpts.inJustDecodeBounds = false;  
        int w = newOpts.outWidth;  
        int h = newOpts.outHeight;  
        // 想要缩放的目标尺寸
        float hh = pixelH;// 设置高度为240f时，可以明显看到图片缩小了
	    float ww = pixelW;// 设置宽度为120f，可以明显看到图片缩小了
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可  
        int be = 1;//be=1表示不缩放  
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放  
            be = (int) (newOpts.outWidth / ww);  
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放  
            be = (int) (newOpts.outHeight / hh);  
        }  
        if (be <= 0) be = 1;  
        newOpts.inSampleSize = be;//设置缩放比例
        // 开始压缩图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(imgPath, newOpts);
        // 压缩好比例大小后再进行质量压缩
//        return compress(bitmap, maxSize); // 这里再进行质量压缩的意义不大，反而耗资源，删除
        return bitmap;
	}
	
	/**
	 * Compress image by size, this will modify image width/height. 
	 * Used to get thumbnail
	 * 
	 * @param image
	 * @param pixelW target pixel of width
	 * @param pixelH target pixel of height
	 * @return
	 */
	public Bitmap ratio(Bitmap image, float pixelW, float pixelH) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
	    image.compress(Bitmap.CompressFormat.JPEG, 100, os);
	    if( os.toByteArray().length / 1024>1024) {//判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出    
	        os.reset();//重置baos即清空baos  
	        image.compress(Bitmap.CompressFormat.JPEG, 50, os);//这里压缩50%，把压缩后的数据存放到baos中  
	    }  
	    ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());  
	    BitmapFactory.Options newOpts = new BitmapFactory.Options();  
	    //开始读入图片，此时把options.inJustDecodeBounds 设回true了  
	    newOpts.inJustDecodeBounds = true;
	    newOpts.inPreferredConfig = Config.RGB_565;
	    Bitmap bitmap = BitmapFactory.decodeStream(is, null, newOpts);  
	    newOpts.inJustDecodeBounds = false;  
	    int w = newOpts.outWidth;  
	    int h = newOpts.outHeight;  
	    float hh = pixelH;// 设置高度为240f时，可以明显看到图片缩小了
	    float ww = pixelW;// 设置宽度为120f，可以明显看到图片缩小了
	    //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可  
	    int be = 1;//be=1表示不缩放  
	    if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放  
	        be = (int) (newOpts.outWidth / ww);  
	    } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放  
	        be = (int) (newOpts.outHeight / hh);  
	    }  
	    if (be <= 0) be = 1;  
	    newOpts.inSampleSize = be;//设置缩放比例  
	    //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了  
	    is = new ByteArrayInputStream(os.toByteArray());  
	    bitmap = BitmapFactory.decodeStream(is, null, newOpts);
	    //压缩好比例大小后再进行质量压缩
//	    return compress(bitmap, maxSize); // 这里再进行质量压缩的意义不大，反而耗资源，删除
	    return bitmap;
	}
	
	/**
	 * Compress by quality,  and generate image to the path specified
	 * 
	 * @param image
	 * @param outPath
	 * @param maxSize target will be compressed to be smaller than this size.(kb)
	 * @throws IOException 
	 */
	public void compressAndGenImage(Bitmap image, String outPath, int maxSize) throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		// scale
		int options = 100;
		// Store the bitmap into output stream(no compress)
        image.compress(Bitmap.CompressFormat.PNG, options, os);  
        // Compress by loop
        while ( os.toByteArray().length / 1024 > maxSize) {
            // Clean up os
        	os.reset();
        	// interval 10
            options -= 10;
            image.compress(Bitmap.CompressFormat.PNG, options, os);
        }
        
        // Generate compressed image file
        FileOutputStream fos = new FileOutputStream(outPath);  
        fos.write(os.toByteArray());  
        fos.flush();  
        fos.close();  
	}
	
	/**
	 * Compress by quality,  and generate image to the path specified
	 * 
	 * @param imgPath
	 * @param outPath
	 * @param maxSize target will be compressed to be smaller than this size.(kb)
	 * @param needsDelete Whether delete original file after compress
	 * @throws IOException 
	 */
	public void compressAndGenImage(String imgPath, String outPath, int maxSize, boolean needsDelete) throws IOException {
		compressAndGenImage(getBitmap(imgPath), outPath, maxSize);
		
		// Delete original file
		if (needsDelete) {
			File file = new File (imgPath);
			if (file.exists()) {
				file.delete();
			}
		}
	}
	
	/**
	 * Ratio and generate thumb to the path specified
	 * 
	 * @param image
	 * @param outPath
	 * @param pixelW target pixel of width
	 * @param pixelH target pixel of height
	 * @throws FileNotFoundException
	 */
	public void ratioAndGenThumb(Bitmap image, String outPath, float pixelW, float pixelH) throws FileNotFoundException {
		Bitmap bitmap = ratio(image, pixelW, pixelH);
		storeImage( bitmap, outPath);
	}
	
	/**
	 * Ratio and generate thumb to the path specified
	 * 
	 * @param imgPath
	 * @param outPath
	 * @param pixelW target pixel of width
	 * @param pixelH target pixel of height
	 * @param needsDelete Whether delete original file after compress
	 * @throws FileNotFoundException
	 */
	public void ratioAndGenThumb(String imgPath, String outPath, float pixelW, float pixelH, boolean needsDelete) throws FileNotFoundException {
		Bitmap bitmap = ratio(imgPath, pixelW, pixelH);
		storeImage( bitmap, outPath);
		
		// Delete original file
				if (needsDelete) {
					File file = new File (imgPath);
					if (file.exists()) {
						file.delete();
					}
				}
	}
	
	
	public static void compressPicture(String srcPath, String desPath) {
		FileOutputStream fos = null;
		BitmapFactory.Options op = new BitmapFactory.Options();

		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		op.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath, op);
		op.inJustDecodeBounds = false;

		// 缩放图片的尺寸
		float w = op.outWidth;
		float h = op.outHeight;
		float hh = 800f;//
		float ww = 800f;//
		// 最长宽度或高度1024
		float be = 1.0f;
		if (w > h && w > ww) {
			be = (float) (w / ww);
		} else if (w < h && h > hh) {
			be = (float) (h / hh);
		}
		if (be <= 0) {
			be = 1.0f;
		}
		op.inSampleSize = (int) be;// 设置缩放比例,这个数字越大,图片大小越小.
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		bitmap = BitmapFactory.decodeFile(srcPath, op);
		int desWidth = (int) (w / be);
		int desHeight = (int) (h / be);
		bitmap = Bitmap.createScaledBitmap(bitmap, desWidth, desHeight, true);
		try {
			fos = new FileOutputStream(desPath);
			if (bitmap != null) {
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static String selectImage(Context context,Intent data){
        Uri selectedImage = data.getData();
//      Log.e(TAG, selectedImage.toString());
        if(selectedImage!=null){          
            String uriStr=selectedImage.toString();
            String path=uriStr.substring(10,uriStr.length());
            if(path.startsWith("com.sec.android.gallery3d")){
                Log.e(TAG, "It's auto backup pic path:"+selectedImage.toString());
                return null;
            }
        }     
        String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(selectedImage,filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();
        return picturePath;   
    }
	
	
	@SuppressLint("NewApi")
	public static String getPath(final Context context, final Uri uri) {
	    final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
	    // DocumentProvider
	    if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
	        // ExternalStorageProvider
	        if (isExternalStorageDocument(uri)) {
	            final String docId = DocumentsContract.getDocumentId(uri);
	            final String[] split = docId.split(":");
	            final String type = split[0];
	            if ("primary".equalsIgnoreCase(type)) {
	                return Environment.getExternalStorageDirectory() + "/" + split[1];
	            }
	            // TODO handle non-primary volumes
	        }
	        // DownloadsProvider
	        else if (isDownloadsDocument(uri)) {
	            final String id = DocumentsContract.getDocumentId(uri);
	            final Uri contentUri = ContentUris.withAppendedId(
	                    Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
	            return getDataColumn(context, contentUri, null, null);
	        }
	        // MediaProvider
	        else if (isMediaDocument(uri)) {
	            final String docId = DocumentsContract.getDocumentId(uri);
	            final String[] split = docId.split(":");
	            final String type = split[0];
	            Uri contentUri = null;
	            if ("image".equals(type)) {
	                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
	            } else if ("video".equals(type)) {
	                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
	            } else if ("audio".equals(type)) {
	                contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
	            }
	            final String selection = "_id=?";
	            final String[] selectionArgs = new String[] {
	                    split[1]
	            };
	            return getDataColumn(context, contentUri, selection, selectionArgs);
	        }
	    }
	    // MediaStore (and general)
	    else if ("content".equalsIgnoreCase(uri.getScheme())) {
	        // Return the remote address
	        if (isGooglePhotosUri(uri))
	            return uri.getLastPathSegment();
	        return getDataColumn(context, uri, null, null);
	    }
	    // File
	    else if ("file".equalsIgnoreCase(uri.getScheme())) {
	        return uri.getPath();
	    }
	    return null;
	}
	/**
	 * Get the value of the data column for this Uri. This is useful for
	 * MediaStore Uris, and other file-based ContentProviders.
	 *
	 * @param context The context.
	 * @param uri The Uri to query.
	 * @param selection (Optional) Filter used in the query.
	 * @param selectionArgs (Optional) Selection arguments used in the query.
	 * @return The value of the _data column, which is typically a file path.
	 */
	public static String getDataColumn(Context context, Uri uri, String selection,
	        String[] selectionArgs) {
	    Cursor cursor = null;
	    final String column = "_data";
	    final String[] projection = {
	            column
	    };
	    try {
	        cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
	                null);
	        if (cursor != null && cursor.moveToFirst()) {
	            final int index = cursor.getColumnIndexOrThrow(column);
	            return cursor.getString(index);
	        }
	    } finally {
	        if (cursor != null)
	            cursor.close();
	    }
	    return null;
	}
	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is ExternalStorageProvider.
	 */
	public static boolean isExternalStorageDocument(Uri uri) {
	    return "com.android.externalstorage.documents".equals(uri.getAuthority());
	}
	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 */
	public static boolean isDownloadsDocument(Uri uri) {
	    return "com.android.providers.downloads.documents".equals(uri.getAuthority());
	}
	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 */
	public static boolean isMediaDocument(Uri uri) {
	    return "com.android.providers.media.documents".equals(uri.getAuthority());
	}
	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is Google Photos.
	 */
	public static boolean isGooglePhotosUri(Uri uri) {
	    return "com.google.android.apps.photos.content".equals(uri.getAuthority());
	}
}