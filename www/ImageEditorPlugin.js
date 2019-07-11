var exec = require('cordova/exec');

exports.editImageFromBase64 = function (base64, success, error) {
    let reg = /base64,(.*)$/
    let result = reg.exec(base64);
    exec(success, error, 'ImageEditorPlugin', 'editImage', ["base64", result[1]]);
};

exports.editImageFromGallery = function (success, error) {
    exec(success, error, 'ImageEditorPlugin', 'editImage', ["sourcetype", "gallery"]);
};

exports.editImageFromCamera = function (success, error) {
    exec(success, error, 'ImageEditorPlugin', 'editImage', ["sourcetype", "camera"]);
};
