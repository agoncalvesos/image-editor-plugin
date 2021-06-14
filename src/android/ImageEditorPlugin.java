package com.outsystems.imageeditorplugin;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import org.apache.cordova.CallbackContext;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import com.outsystems.imageeditorplugin.Intents.IntentsDefinition;
import com.outsystems.imageeditorplugin.MediaFunctions;


public class ImageEditorPlugin extends CordovaPlugin {

    MediaFunctions mediaFunctions;
    CallbackContext callbackContext;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        this.callbackContext = callbackContext;
        mediaFunctions = new MediaFunctions(this.cordova.getActivity());

        if (action.equals("editImage")) {
            String sourceTypeString = args.getString(0);
            cordova.setActivityResultCallback(this); //makes this class able to handle the startActivityForResult result

            if (sourceTypeString.equals("sourcetype")){
                String sourceType = args.getString(1);

                if (sourceType.equals("camera")){
                    String path = args.getString(2);
                    mediaFunctions.onPhotoTaken(path);
                } else {
                    mediaFunctions.openGallery();
                }
            } else if (sourceTypeString.equals("base64")) {
                String strBase64 = args.getString(1);

                if (strBase64.equals("")) {
                    Log.d("ERROR", "You must set the second argument of the editImage function as the base64 string");
                    return false;
                }
                mediaFunctions.editBase64Image(strBase64);
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
                if (requestCode == IntentsDefinition.GALLERY_INTENT_CALLED || requestCode == IntentsDefinition.GALLERY_KITKAT_INTENT_CALLED) {
                    cordova.setActivityResultCallback(this); //makes this class able to handle the startActivityForResult result
                    mediaFunctions.onActivityResultGallery(requestCode, intent);
                } else if (requestCode == IntentsDefinition.EDITOR_INTENT_CALLED){
                    String base64 = mediaFunctions.onActivityResultEditor(requestCode, intent);

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
}