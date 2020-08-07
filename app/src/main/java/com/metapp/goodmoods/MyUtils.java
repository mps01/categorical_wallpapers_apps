package com.metapp.goodmoods;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static android.app.WallpaperManager.FLAG_LOCK;
import static android.app.WallpaperManager.FLAG_SYSTEM;

public class MyUtils {

    public static int counter = 0;

    public static void setBgOrDownload(final String urlString, final Activity activity, final boolean download) {

        Picasso.get().load(urlString).into(new Target() {
            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                if (download) {
                   saveImageBitmap(activity, urlString,bitmap);

                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            WallpaperManager wallpaperManager = WallpaperManager.getInstance(activity);
                            try {
                                wallpaperManager.setBitmap(bitmap);
                                ((PreviewActivity) activity).wallpaperSet(true);
                            } catch (IOException e) {
                                e.printStackTrace();
                                ((PreviewActivity) activity).wallpaperSet(true);

                            }
                        }
                    }).start();

                }


            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });
    }
    public static void setHomeOrLock(String urlString, final Activity activity, final boolean lockScreen) {

        Picasso.get().load(urlString).into(new Target() {
            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            WallpaperManager wallpaperManager = WallpaperManager.getInstance(activity);
                            try {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    if (lockScreen) {
                                        wallpaperManager.setBitmap(bitmap, null, true, FLAG_LOCK);

                                    } else {
                                        wallpaperManager.setBitmap(bitmap, null, true, FLAG_SYSTEM);


                                    }
                                    ((PreviewActivity) activity).homeOrLockSet(lockScreen);

                                } else {
                                    wallpaperManager.setBitmap(bitmap);
                                    ((PreviewActivity) activity).wallpaperSet(true);

                                }


                            } catch (IOException e) {
                                e.printStackTrace();
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(activity, "Something went wrong", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        }
                    }).start();

            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });
    }

    public static void saveImageBitmap(Activity context, String url, Bitmap bitmap) {
        final AtomicBoolean isFilePresent = new AtomicBoolean(false);
        String myDir = Environment.getExternalStorageDirectory().toString() + "/" + context.getResources().getString(R.string.app_name);
        File fileDir = new File(myDir);

        if (!fileDir.exists()) {
            fileDir.mkdir();
        }
        url = url.substring(url.lastIndexOf("/") + 1);
        final File newFile = new File(myDir, "Wallpaper_" + url);
        File[] filesList = fileDir.listFiles();
        if (filesList != null) {

            ArrayList<File> files = new ArrayList<>(Arrays.asList(filesList));
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                Optional<File> result = files.stream().filter(new Predicate<File>() {
                    @Override
                    public boolean test(File file) {
                        return file.getAbsolutePath().equals(newFile.getAbsolutePath());
                    }
                }).findFirst();
                result.ifPresent(new Consumer<File>() {
                    @Override
                    public void accept(File file) {
                        isFilePresent.set(true);
                    }
                });
            } else {
                for (File file : files) {
                    if (file.getAbsolutePath().equals(newFile.getAbsolutePath())) {
                        isFilePresent.set(true);
                        break;
                    }
                }
            }

            if (!isFilePresent.get()) {
                saveImageIntoMemory(context,bitmap, newFile,true);
            } else {
                Toast.makeText(context, "Alread Downloaded", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private static void saveImageIntoMemory(Activity context, Bitmap bitmap, File newFile, boolean download) {
        FileOutputStream ostream = null;
        try {
            newFile.createNewFile();
            ostream = new FileOutputStream(newFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
            ostream.flush();
            ostream.close();
            if(download)
            {
                ((PreviewActivity) context).downloadComplete(true);

            }else{
                Toast.makeText(context, "Image is saved", Toast.LENGTH_SHORT).show();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            ((PreviewActivity) context).downloadComplete(false);


        } catch (IOException e) {
            e.printStackTrace();
            ((PreviewActivity) context).downloadComplete(false);

        }
    }

    public static void getInternalFile(final Activity context, String url) throws IOException {
        File myDir = context.getFilesDir();
        final File file = new File(myDir, "tempFile.jpg");
        file.createNewFile();

        Picasso.get().load(url).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                FileOutputStream ostream = null;
                try {
                    file.createNewFile();
                    ostream = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
                    ostream.flush();
                    ostream.close();
                    CropImage.activity(Uri.fromFile(file)).start(context);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Log.d("lksjdf",e.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("lksjdf",e.toString());

                }
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });
    }

    public static Bitmap getContactBitmapFromURI(Context context, Uri uri) {
        try {

            InputStream input = context.getContentResolver().openInputStream(uri);
            if (input == null) {
                return null;
            }
            return BitmapFactory.decodeStream(input);
        }
        catch (FileNotFoundException e)
        {

        }
        return null;

    }

    public static void saveCropedImage(Activity activity, String url, Bitmap bitmap)
    {
        String myDir = Environment.getExternalStorageDirectory().toString() + "/" + activity.getResources().getString(R.string.app_name);
        File fileDir = new File(myDir);

        if (!fileDir.exists()) {
            fileDir.mkdir();
        }
        url = url.substring(url.lastIndexOf("/") + 1);
        File newFile = new File(myDir, "Wallpaper_Crop_" + url);
        saveImageIntoMemory(activity,bitmap,newFile,false);
    }

    public static void shareApp(String content, Context context) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, content);
        context.startActivity(Intent.createChooser(sharingIntent, "Share Text"));
    }

    public static boolean isOnline(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //should check null because in airplane mode it will be null
        return (netInfo != null && netInfo.isConnected());
    }
}
