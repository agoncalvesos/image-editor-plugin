# image-editor-plugin

This plugin defines a window.imageeditorplugin object, which supplies an interface to take pictures with the camera, get a picture from the gallery or use a base64 string, allowing the user to crop that picture, add text, emojis, stickets and share that picture. The edited image is then exported as base64, ready to be saved to a database.

## Available features
- Crop an image
- Add emojis
- Draw lines with a wide range of colors
- Add stickers (android only)
- Add text with a wide range of colors
- Share that image with anyone

## Installation

```sh
cordova plugin add https://github.com/agoncalvesos/image-editor-plugin.git
```

## editImageFromBase64
Opens the image editor main screen, allowing the user to use all the features, on the base64 passed as input parameter

```javascript
window.imageeditorplugin.editImageFromBase64(base64, onSuccess, onError);
```

## editImageFromCamera

Opens the phone camera allowing the user to take a photo and edit it right away

```javascript
window.imageeditorplugin.editImageFromCamera(onSuccess, onError);
```

## editImageFromGallery

Opens the phone gallery allowing the user to choose a photo and edit it right away

```javascript
window.imageeditorplugin.editImageFromGallery(onSuccess, onError);
```

## Description

This cordova plugin was created to be used inside an OutSystems plugin, and allows the user to edit an image. Functions editImageFromBase64, editImageFromCamera and editImageFromGallery return a base64 string that can be saved in a database or shown to the user

## Supported Platforms

- Android 4.0 +
- iOS 9.0 +

## Example

Create a button on your page

```html
<button id="testeditorbase64">edit base64 image</button>
<button id="testeditorcamera">edit from camera</button>
<button id="testeditorgallery">edit from gallery</button>
<img id="base64image" height="400" width="600" src="data:image/jpeg,/base64string..."/>
```

Then add click event

```javascript
document.getElementById(“testeditorbase64”).addEventListener(“click”, function(){
   imageeditorplugin.editImageFromBase64(document.getElementById(“base64image”).src, function(base64){
       document.getElementById(“base64image”).src = “data:image;base64,” + base64;
   },onFail);
});
document.getElementById(“testeditorcamera”).addEventListener(“click”, function(){
   imageeditorplugin.editImageFromCamera(function(base64){
       document.getElementById(“base64image”).src = “data:image;base64,” + base64;
   },onFail)
});
document.getElementById(“testeditorgallery”).addEventListener(“click”, function(){
   imageeditorplugin.editImageFromGallery(function(base64){
       document.getElementById(“base64image”).src = “data:image;base64,” + base64;
   },onFail)
});

function onFail(message) {
      console.log('plugin message: ' + message);
}
```

# Credits
Thanks to eventtus for the native source code for ios and android, available here:
https://github.com/eventtus/photo-editor
https://github.com/eventtus/photo-editor-android
