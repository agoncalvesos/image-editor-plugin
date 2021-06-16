var exec = require('cordova/exec');

var cameraDefaultSettings = {
    quality: 100,
    destinationType: Camera.DestinationType.FILE_URI,
    targetWidth: 1600,
    targetHeight: 1600,
    encodingType: Camera.EncodingType.JPEG,
    mediaType: Camera.MediaType.PICTURE,
    allowEdit: false,
    correctOrientation: true,
    saveToPhotoAlbum: false
};

exports.editImageFromBase64 = function (base64, success, error) {
    exec(success, error, 'ImageEditorPlugin', 'editImage', ["base64", base64]);
};

exports.editImageFromGallery = function (success, error) {
    if(!!navigator.camera){
        //Setting source to Gallery
        cameraDefaultSettings.sourceType = Camera.PictureSourceType.PHOTOLIBRARY;

        navigator.camera.getPicture(function(imagePath) {
            exec(success, error, 'ImageEditorPlugin', 'editImage', ['source_gallery_camera', imagePath]);
        }, function (errorMessage) {
            error(errorMessage)
        }, cameraDefaultSettings);
    } else {
        error("Camera plugin not installed: navigator.camera is undefined");
    }
};

exports.editImageFromCamera = function (success, error) {
    if(!!navigator.camera){
        //Setting source to Camera
        cameraDefaultSettings.sourceType = Camera.PictureSourceType.CAMERA;

        navigator.camera.getPicture(function(imagePath) {
            exec(success, error, 'ImageEditorPlugin', 'editImage', ['source_gallery_camera', imagePath]);
        }, function (errorMessage) {
            error(errorMessage)
        }, cameraDefaultSettings);
    }else {
        error("Camera plugin not installed: navigator.camera is undefined");
    }
};