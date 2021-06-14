package com.outsystems.imageeditorplugin;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;

import com.ahmedadeltito.photoeditor.GalleryUtils;
import com.ahmedadeltito.photoeditor.PhotoEditorActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.outsystems.imageeditorplugin.Intents.IntentsDefinition;

public class MediaFunctions {

    protected static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_GALLERY = 0x3;
    protected static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_CAMERA = 0x4;


    final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    private static final String PHOTO_PATH = "PhotoEditor";
    private final Activity ParentActivity;

    public MediaFunctions(Activity activity){
        ParentActivity = activity;
    }

    private void showMenu(int caller){
        AlertDialog.Builder builder = new AlertDialog.Builder(ParentActivity);
        builder.setMessage(ParentActivity.getString(com.ahmedadeltito.photoeditor.R.string.access_media_permissions_msg));
        builder.setPositiveButton(ParentActivity.getString(com.ahmedadeltito.photoeditor.R.string.continue_txt), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (caller == 2) {
                    ActivityCompat.requestPermissions(ParentActivity,
                            new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_GALLERY);
                }
            }
        });
        builder.setNegativeButton(ParentActivity.getString(com.ahmedadeltito.photoeditor.R.string.not_now), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(ParentActivity, ParentActivity.getString(com.ahmedadeltito.photoeditor.R.string.media_access_denied_msg), Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }

    public void openGallery() {
        int permissionCheck = PermissionChecker.checkCallingOrSelfPermission(ParentActivity,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            if (!isKitKat) {
                Intent intent = new Intent();
                intent.setType("image/jpeg");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                ParentActivity.startActivityForResult(
                        Intent.createChooser(intent, ParentActivity.getString(com.ahmedadeltito.photoeditor.R.string.upload_picker_title)),
                        IntentsDefinition.GALLERY_INTENT_CALLED);
            } else {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/jpeg");
                ParentActivity.startActivityForResult(intent, IntentsDefinition.GALLERY_KITKAT_INTENT_CALLED);
            }
        } else {
            showMenu(2);
        }
    }

    public void editBase64Image(String base64){
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageName = "IMG_" + timeStamp + ".jpg";
        String selectedOutputPath = com.outsystems.imageeditorplugin.Utils.FileUtils.saveImage("PhotoEditorSDK", imageName, base64);

        onPhotoTaken(selectedOutputPath);
    }

    public void onActivityResultGallery(int requestCode, Intent intent){
        String selectedImagePath = "";
        Uri selectedImageUri;

        if (requestCode == IntentsDefinition.GALLERY_INTENT_CALLED) {
            selectedImageUri = intent.getData();
            selectedImagePath = getPath(selectedImageUri);
        } else if (requestCode == IntentsDefinition.GALLERY_KITKAT_INTENT_CALLED) {
            selectedImageUri = intent.getData();
            final int takeFlags = intent.getFlags()
                    & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            // Check for the freshest data.
            if (selectedImageUri != null) {
                ParentActivity.getContentResolver().takePersistableUriPermission(
                        selectedImageUri, takeFlags);
                selectedImagePath = getPath(selectedImageUri);
            }
        }
        onPhotoTaken(selectedImagePath);
    }

    public String onActivityResultEditor(int resquestCode, Intent intent){
        String imagePath = intent.getStringExtra("imagePath");

        return com.outsystems.imageeditorplugin.Utils.FileUtils.getBase64FromPath(imagePath);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private String getPath(final Uri uri) {
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(ParentActivity, uri)) {
            // ExternalStorageProvider
            if (GalleryUtils.isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/"
                            + split[1];
                }
            }
            // DownloadsProvider
            else if (GalleryUtils.isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));
                return GalleryUtils.getDataColumn(ParentActivity, contentUri, null, null);
            }
            // MediaProvider
            else if (GalleryUtils.isMediaDocument(uri)) {
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
                final String[] selectionArgs = new String[]{split[1]};

                return GalleryUtils.getDataColumn(ParentActivity, contentUri, selection,
                        selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return GalleryUtils.getDataColumn(ParentActivity, uri,null,null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public void onPhotoTaken(String selectedImagePath) {
        Intent intent = new Intent(ParentActivity, PhotoEditorActivity.class);
        intent.putExtra("selectedImagePath", selectedImagePath);
        ParentActivity.startActivityForResult(intent, IntentsDefinition.EDITOR_INTENT_CALLED);
    }
}
