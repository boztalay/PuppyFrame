package com.boztalay.puppyframeuid.configuration.editalbum;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Environment;
import android.view.Display;
import android.view.WindowManager;
import com.boztalay.puppyframeuid.persistence.Album;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

public class ImageCacher {
    private static final float MAX_IMAGE_SIZE_SCALE_FACTOR = 0.75f;
    public static final String FILE_PROTOCOL = "file://";

    private Context context;
    private Rect maxImageDimensions;

    private Album currentAlbum;
    private ImageResizingListener currentListener;

    public ImageCacher(Context context) {
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

    public void resizeAndCacheLargeImagesInAlbum(Album album, ImageResizingListener listener) {
        currentAlbum = album;
        currentListener = listener;

        new ResizeAndCacheImageTask().execute(album);
    }

    private class ResizeAndCacheImageTask extends AsyncTask<Album, Void, Map<String, Integer>> {
        private Rect originalImageDimensions;

        @Override
        protected Map<String, Integer> doInBackground(Album... albums) {
            Album album = albums[0];

            Map<String, Integer> cachedImagePaths = new HashMap<String, Integer>();

            for(int i = 0; i < album.getImagePaths().size(); i++) {
                String imagePath = album.getImagePaths().get(i);
                String imagePathWithoutProtocol = imagePath.replace(FILE_PROTOCOL, "");

                originalImageDimensions = getImageDimensions(imagePathWithoutProtocol);

                if(isOriginalImageTooLarge(imagePathWithoutProtocol)) {
                    String cachedImagePath = FILE_PROTOCOL + resizeAndCacheImage(imagePathWithoutProtocol);

                    if(cachedImagePath != null) {
                        cachedImagePaths.put(cachedImagePath, new Integer(i));
                    }
                }
            }

            return cachedImagePaths;
        }

        private Rect getImageDimensions(String imagePath) {
            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
            bitmapOptions.inJustDecodeBounds = true;

            BitmapFactory.decodeFile(imagePath, bitmapOptions);

            return new Rect(0, 0, bitmapOptions.outWidth, bitmapOptions.outHeight);
        }

        private boolean isOriginalImageTooLarge(String imagePath) {
            return (originalImageDimensions.width() > maxImageDimensions.width() || originalImageDimensions.height() > maxImageDimensions.height());
        }

        private String resizeAndCacheImage(String imagePath) {
            Rect newImageDimensions = calculateNewImageDimensions();

            Bitmap slightlyTooLargeBitmap = getSlightlyTooLargeBitmapForImage(imagePath);
            Bitmap correctlySizedBitmap = Bitmap.createScaledBitmap(slightlyTooLargeBitmap, newImageDimensions.width(), newImageDimensions.height(), true);
            slightlyTooLargeBitmap.recycle();

            String cachedImagePath = cacheBitmapWithPath(correctlySizedBitmap, imagePath);
            return cachedImagePath;
        }

        private Bitmap getSlightlyTooLargeBitmapForImage(String imagePath) {
            int maxDimensionOfImage = Math.max(originalImageDimensions.width(), originalImageDimensions.height());
            int scaleFactorForDecoding = (int)Math.ceil((double)maxDimensionOfImage / (double)maxImageDimensions.height());

            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
            bitmapOptions.inSampleSize = scaleFactorForDecoding;

            return BitmapFactory.decodeFile(imagePath, bitmapOptions);
        }

        private Rect calculateNewImageDimensions() {
            float originalImageAspectRatio = ((float)originalImageDimensions.width() / (float)originalImageDimensions.height());

            Rect newImageDimensions = new Rect();
            if(originalImageAspectRatio > 1.0f) {
                newImageDimensions.bottom = maxImageDimensions.width();
                newImageDimensions.right = (int)((float)maxImageDimensions.width() * originalImageAspectRatio);
            } else {
                newImageDimensions.right = maxImageDimensions.width();
                newImageDimensions.bottom = (int)((float)maxImageDimensions.width() / originalImageAspectRatio);
            }

            return newImageDimensions;
        }

        private String cacheBitmapWithPath(Bitmap bitmapToSave, String imagePath) {
            try {
                File cacheDirectoryFile = new File(Environment.getExternalStorageDirectory() + "/.PuppyFrameCache");
                cacheDirectoryFile.mkdirs();

                File imageFile = new File(imagePath);
                String cachedImagePath = cacheDirectoryFile.getAbsolutePath() + "/" + imageFile.getName();

                FileOutputStream outputStream = new FileOutputStream(cachedImagePath);
                bitmapToSave.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
                bitmapToSave.recycle();

                return cachedImagePath;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(Map<String, Integer> cachedImagePaths) {
            for(String cachedImagePath : cachedImagePaths.keySet()) {
                int originalImagePathIndex = cachedImagePaths.get(cachedImagePath).intValue();
                String imagePath = currentAlbum.getImagePaths().remove(originalImagePathIndex);
                currentAlbum.getImagePaths().add(originalImagePathIndex, cachedImagePath);
                currentAlbum.cacheImagePath(imagePath, cachedImagePath);
            }

            currentListener.imageResizingCompleted();
        }
    }

    public interface ImageResizingListener {
        public void imageResizingCompleted();
    }
}
