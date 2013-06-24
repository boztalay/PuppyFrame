package com.boztalay.puppyframe.configuration.editalbum;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Environment;
import android.view.Display;
import android.view.WindowManager;
import com.boztalay.puppyframe.persistence.Album;

import java.io.File;
import java.io.FileOutputStream;

public class ImageResizer {
    private static final float MAX_IMAGE_SIZE_SCALE_FACTOR = 0.75f;
    public static final String FILE_PROTOCOL = "file://";

    private Context context;
    private Rect maxImageDimensions;

    public ImageResizer(Context context) {
        this.context = context;

        determineMaxImageDimensions();
    }

    private void determineMaxImageDimensions() {
        maxImageDimensions = new Rect();

        Point displaySize = getDisplaySize();

        maxImageDimensions = new Rect(0, 0, (int)(displaySize.x * MAX_IMAGE_SIZE_SCALE_FACTOR), (int)(displaySize.y * MAX_IMAGE_SIZE_SCALE_FACTOR));
    }

    private Point getDisplaySize() {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();

        Point displaySize = new Point();
        display.getSize(displaySize);

        return displaySize;
    }

    public void resizeAndCacheLargeImagesInAlbum(Album album) {
        for(int i = 0; i < album.getImagePaths().size(); i++) {
            String imagePath = album.getImagePaths().get(i);
            String imagePathWithoutProtocol = imagePath.replace(FILE_PROTOCOL, "");

            if(isImageTooLarge(imagePathWithoutProtocol)) {
                String newImagePath = FILE_PROTOCOL + resizeAndCacheImage(imagePathWithoutProtocol);
                if(newImagePath != null) {
                    album.getImagePaths().add(i, newImagePath);
                    album.getImagePaths().remove(imagePath);
                }
            }
        }
    }

    private boolean isImageTooLarge(String imagePath) {
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(imagePath, bitmapOptions);

        return (bitmapOptions.outWidth > maxImageDimensions.width() || bitmapOptions.outHeight > maxImageDimensions.height());
    }

    private String resizeAndCacheImage(String imagePath) {
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(imagePath, bitmapOptions);

        Rect originalImageDimensions = new Rect(0, 0, bitmapOptions.outWidth, bitmapOptions.outHeight);

        int maxDimensionOfImage = Math.max(originalImageDimensions.width(), originalImageDimensions.height());
        int scaleFactorForDecoding = (int)Math.ceil((double)maxDimensionOfImage / (double)maxImageDimensions.height());

        bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = scaleFactorForDecoding;
        Bitmap slightlyTooLargeBitmap = BitmapFactory.decodeFile(imagePath, bitmapOptions);

        float originalImageAspectRatio = ((float)originalImageDimensions.width() / (float)originalImageDimensions.height());

        Rect newImageDimensions = new Rect();
        if(originalImageAspectRatio > 1.0f) {
            newImageDimensions.bottom = maxImageDimensions.width();
            newImageDimensions.right = (int)((float)maxImageDimensions.width() * originalImageAspectRatio);
        } else {
            newImageDimensions.right = maxImageDimensions.width();
            newImageDimensions.bottom = (int)((float)maxImageDimensions.width() / originalImageAspectRatio);
        }

        Bitmap correctlySizedBitmap = Bitmap.createScaledBitmap(slightlyTooLargeBitmap, newImageDimensions.width(), newImageDimensions.height(), true);
        slightlyTooLargeBitmap.recycle();

        try {
            File imageFile = new File(imagePath);
            File cacheDirectoryFile = new File(Environment.getExternalStorageDirectory() + "/PuppyFrameCache");
            cacheDirectoryFile.mkdirs();
            String cachedImagePath = cacheDirectoryFile.getAbsolutePath() + "/" + imageFile.getName();

            FileOutputStream outputStream = new FileOutputStream(cachedImagePath);
            correctlySizedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
            correctlySizedBitmap.recycle();

            return cachedImagePath;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
