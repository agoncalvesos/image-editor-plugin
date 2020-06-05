import UIKit
import OSPhotoEditor

@objc(ImageEditorPlugin) class ImageEditorPlugin : CDVPlugin {
    
    @IBOutlet weak var imageView: UIImageView!
    @IBOutlet var callbackId: String!
    
    
    @objc(editImage:)
    
    func editImage(_ command: CDVInvokedUrlCommand) {
        self.callbackId = command.callbackId;
        
        var sourceType = UIImagePickerController.SourceType.photoLibrary;
        let sourceTypeString = command.arguments[0] as? String;
        if ( sourceTypeString == "sourcetype" ){
            if (command.arguments[1] as? String == "camera"){
                sourceType = .camera;
            }
            
            let picker = UIImagePickerController()
            picker.delegate = self
            picker.sourceType = sourceType
            
            self.viewController.present(picker, animated: true,  completion: {
                //                pluginResult = CDVPluginResult(
                //                    status: CDVCommandStatus_OK,
                //                    messageAs: ""
                //                )
            })
            
        } else if (sourceTypeString == "base64" ) {
            let strBase64 = command.arguments[1] as? String ?? "";
            
            if (strBase64 != ""){
                
                let dataDecoded : Data = Data(base64Encoded: strBase64, options: .ignoreUnknownCharacters)!
                
                presentImageEditorViewController(image: UIImage(data: dataDecoded)!);
            }
            
            /*self.commandDelegate!.send(
             pluginResult,
             callbackId: command.callbackId
             )*/
        } else {
            print("You must set the first argument of the editImage function as sourcetype or base64")
            
            let pluginResult = CDVPluginResult(
                status: CDVCommandStatus_ERROR
            )
            self.commandDelegate!.send(
                pluginResult,
                callbackId: command.callbackId
            )
        }
    }
    
    func presentImageEditorViewController (image: UIImage){
        let photoEditor = PhotoEditorViewController(nibName:"PhotoEditorViewController",bundle: Bundle(for: PhotoEditorViewController.self))
        photoEditor.modalPresentationStyle = .fullScreen
        photoEditor.photoEditorDelegate = self
        photoEditor.image = image
        //Colors for drawing and Text, If not set default values will be used
        //photoEditor.colors = [.red, .blue, .green]
        
        //Stickers that the user will choose from to add on the image
        /*for i in 0...10 {
            photoEditor.stickers.append(UIImage(named: i.description )!)
        }*/
        
        //To hide controls - array of enum control
        //photoEditor.hiddenControls = [.crop, .draw, .share]
        
        self.viewController.present(photoEditor, animated: true, completion: nil)
    }
}

extension ImageEditorPlugin: PhotoEditorDelegate {
    
    func doneEditing(image: UIImage) {
        
        let pluginResult = CDVPluginResult(
            status: CDVCommandStatus_OK,
            messageAs: image.toBase64()
        )
        
        self.commandDelegate!.send(pluginResult, callbackId: self.callbackId)
    }
    
    func canceledEditing() {
        let pluginResult = CDVPluginResult(
            status: CDVCommandStatus_ERROR
        )
        self.commandDelegate!.send(
            pluginResult,
            callbackId: self.callbackId
        )
    }
}

extension ImageEditorPlugin: UIImagePickerControllerDelegate, UINavigationControllerDelegate {
    
     func imagePickerController(_ picker: UIImagePickerController,
                                       didFinishPickingMediaWithInfo info: [UIImagePickerController.InfoKey : Any]) {
        
        guard let image = info[UIImagePickerController.InfoKey.originalImage] as? UIImage else {
            picker.dismiss(animated: true, completion: nil)
            return
        }
        picker.dismiss(animated: true, completion: nil)
        
        
        self.presentImageEditorViewController(image: image);
    }
    
    func imagePickerControllerDidCancel(_ picker: UIImagePickerController) {
        self.viewController.dismiss(animated: true, completion: nil)
    }
}

extension UIImage {
    
    public func toBase64() -> String? {
        let imageData: Data?
        imageData = self.jpegData(compressionQuality: 1)//compression
        return imageData?.base64EncodedString()
    }
}
