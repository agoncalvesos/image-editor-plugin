var exec = require('cordova/exec');

exports.editImageFromBase64 = function (base64, success, error) {
    exec(success, error, 'ImageEditorPlugin', 'editImage', ["base64", base64]);
};

exports.editImageFromGallery = function (success, error) {
    exec(success, error, 'ImageEditorPlugin', 'editImage', ["sourcetype", "gallery"]);
};

exports.editImageFromCamera = function (success, error) {
    if(!!navigator.camera){
        navigator.camera.getPicture(function(imagePath) {
            exec(success, error, 'ImageEditorPlugin', 'editImage', ["sourcetype", "camera",imagePath]);
        }, function (errorMessage) {
            error(errorMessage)
        }, {
            quality: 100,  
            destinationType: Camera.DestinationType.FILE_URI, 
            sourceType : Camera.PictureSourceType.CAMERA, 
            targetWidth: 1600,
            targetHeight: 1600, 
            encodingType: Camera.EncodingType.JPEG,
            mediaType: Camera.MediaType.PICTURE,
            allowEdit: false,
            correctOrientation: true,
            saveToPhotoAlbum: false
        });
    }else{
        exec(success, error, 'ImageEditorPlugin', 'editImage', ["sourcetype", "camera"]);
    }
};
