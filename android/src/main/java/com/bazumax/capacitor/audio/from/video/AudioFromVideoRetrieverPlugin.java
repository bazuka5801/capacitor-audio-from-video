package com.bazumax.capacitor.audio.from.video;

import android.Manifest;
import android.content.ContentResolver;
import android.os.Build;

import com.getcapacitor.JSObject;
import com.getcapacitor.PermissionState;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;
import com.getcapacitor.annotation.PermissionCallback;

import java.io.File;
import java.io.IOException;

@CapacitorPlugin(name = "AudioFromVideoRetriever", permissions = {
    @Permission(
            alias = "storage-new",
            strings = {
                    Manifest.permission.READ_MEDIA_VIDEO
            }
    ),
    @Permission(
            alias = "storage-old",
            strings = {
                    Manifest.permission.READ_EXTERNAL_STORAGE
            }
    )
})
public class AudioFromVideoRetrieverPlugin extends Plugin {

    private AudioFromVideoRetriever implementation = new AudioFromVideoRetriever();

    public String getStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return "storage-new";
        } else {
            return "storage-old";
        }
    }

    @PermissionCallback
    private void videoPermsCallback(PluginCall call) {
        if (getPermissionState(getStoragePermission()) == PermissionState.GRANTED) {
            extractAudio(call);
        } else {
            call.reject("Permission is required to take a picture");
        }
    }

    @PluginMethod
    public void extractAudio(PluginCall call) {
        if (getPermissionState(getStoragePermission()) != PermissionState.GRANTED) {
            requestPermissionForAlias(getStoragePermission(), call, "videoPermsCallback");
            return;
        }
        String path = call.getString("path");
        String outputPath = call.getString("outputPath");
        Boolean includeData = call.getBoolean("includeData", false);

        ContentResolver resolver = bridge.getContext().getContentResolver();
        File inputFile = implementation.getFileObject(path, resolver);
        File outputFile = implementation.getFileObject(outputPath, resolver);

        implementation.extractAudio(inputFile, outputFile, new AudioFromVideoRetriever.ExtractionCallback() {
            @Override
            public void onExtractionCompleted(File audioFile, String mimeType) throws IOException {
                JSObject ret = new JSObject();
                if (includeData) {
                    ret.put("dataUrl", implementation.getDataUrlFromAudioFile(audioFile, mimeType));
                }
                ret.put("path", outputPath);
                call.resolve(ret);
            }

            @Override
            public void onExtractionFailed(String errorMessage) {
                call.reject(errorMessage);
            }
        });
    }
}
