package com.outsystems.imageeditorplugin;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import org.apache.cordova.CallbackContext;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

import com.ahmedadeltito.photoeditor.PhotoEditorActivity;
import com.outsystems.imageeditorplugin.Intents.IntentsDefinition;


public class ImageEditorPlugin extends CordovaPlugin {

    CallbackContext callbackContext;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        this.callbackContext = callbackContext;

        if (action.equals("editImage")) {
            String sourceTypeString = args.getString(0);
            cordova.setActivityResultCallback(this); //makes this class able to handle the startActivityForResult result

            if (sourceTypeString.equals("source_gallery_camera")){
                String path = args.getString(1);
                onPhotoTaken(path);

            } else if (sourceTypeString.equals("base64")) {
                String strBase64 = args.getString(1);

                if (strBase64.equals("")) {
                    Log.d("ERROR", "You must set the second argument of the editImage function as the base64 string");
                    return false;
                }
                String selectedOutputPath = com.outsystems.imageeditorplugin.Utils.FileUtils.saveImage(this.cordova.getActivity().getCacheDir(), strBase64);
                onPhotoTaken(selectedOutputPath);
            } else {
                Log.d("ERROR", "You must set the first argument of the editImage function as sourcetype or base64");
            }
            return true;
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (resultCode) {
            case android.app.Activity.RESULT_CANCELED:
                break;
            case android.app.Activity.RESULT_OK:
                if (requestCode == IntentsDefinition.EDITOR_INTENT_CALLED){
                    String imagePath = intent.getStringExtra("imagePath");

                    String base64 = com.outsystems.imageeditorplugin.Utils.FileUtils.getBase64FromPath(imagePath);

                    if (base64 != null && base64.length() > 0) {
                        callbackContext.success(base64);
                    } else {
                        callbackContext.error("Expected one non-empty string argument.");
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }

    public void onPhotoTaken(String selectedImagePath) {
        Intent intent = new Intent(this.cordova.getActivity(), PhotoEditorActivity.class);
        intent.putExtra("selectedImagePath", selectedImagePath);
        this.cordova.getActivity().startActivityForResult(intent, IntentsDefinition.EDITOR_INTENT_CALLED);
    }
}