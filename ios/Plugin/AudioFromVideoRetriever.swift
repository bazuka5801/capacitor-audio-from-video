import AVFoundation

@objc public class AudioFromVideoRetriever: NSObject {
    @objc public func extractAudio(videoURL: URL, outputURL: URL, completion: @escaping (URL?, Error?) -> Void) {
        let asset = AVURLAsset(url: videoURL)
        let audioTrack = asset.tracks(withMediaType: .audio).first
        
        guard let audioTrack = audioTrack else {
            let error = NSError(domain: "AudioExtractionError", code: 0, userInfo: [NSLocalizedDescriptionKey: "No audio track found in the video."])
            completion(nil, error)
            return
        }
        
        let composition = AVMutableComposition()
        let audioCompositionTrack = composition.addMutableTrack(withMediaType: .audio, preferredTrackID: kCMPersistentTrackID_Invalid)
        
        do {
            try audioCompositionTrack?.insertTimeRange(audioTrack.timeRange, of: audioTrack, at: .zero)
        } catch {
            completion(nil, error)
            return
        }
        
        let exportSession = AVAssetExportSession(asset: composition, presetName: AVAssetExportPresetPassthrough)
        exportSession?.outputFileType = AVFileType.m4a
        exportSession?.outputURL = outputURL
        
        exportSession?.exportAsynchronously {
            if exportSession?.status == .completed {
                completion(outputURL, nil)
            } else if let error = exportSession?.error {
                completion(nil, error)
            }
        }
    }
}
