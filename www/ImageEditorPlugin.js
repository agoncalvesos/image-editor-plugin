var exec = require('cordova/exec');

exports.editImageFromBase64 = function (base64, success, error) {
    exec(success, error, 'ImageEditorPlugin', 'editImage', ["base64", base64]);
};

exports.editImageFromGallery = function (success, error) {
    exec(success, error, 'ImageEditorPlugin', 'editImage', ["sourcetype", "gallery"]);
};

exports.editImageFromCamera = function (success, error) {
    exec(success, error, 'ImageEditorPlugin', 'editImage', ["sourcetype", "camera"]);
};
