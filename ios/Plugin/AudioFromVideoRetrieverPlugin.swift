import Foundation
import Capacitor

/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitorjs.com/docs/plugins/ios
 */
@objc(AudioFromVideoRetrieverPlugin)
public class AudioFromVideoRetrieverPlugin: CAPPlugin {
    private let implementation = AudioFromVideoRetriever()

    @objc func extractAudio(_ call: CAPPluginCall) {
        let path = call.getString("path") ?? ""
        let outputPath = call.getString("outputPath") ?? ""
        
        let url = URL(string: path)
        let outputUrl = URL(string: outputPath)
        
        implementation.extractAudio(videoURL: url!, outputURL: outputUrl!) { resultUrl, error in
            guard error == nil else {
                call.reject(error!.localizedDescription)
                return
            }
            
            guard let resultUrl = resultUrl else {
                call.reject("Unexpected error occurred during audio extraction.")
                return
            }
            
            call.resolve([
                "path": resultUrl.absoluteString
            ])
        }
    }
}
