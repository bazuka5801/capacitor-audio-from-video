package com.bazumax.capacitor.audio.from.video;

import android.Manifest;
import android.content.ContentResolver;

import com.getcapacitor.JSObject;
import com.getcapacitor.PermissionState;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;
import com.getcapacitor.annotation.PermissionCallback;

import java.io.File;

@CapacitorPlugin(name = "AudioFromVideoRetriever", permissions = {
    @Permission(
            alias = "storage",
            strings = {
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }
    )
})
public class AudioFromVideoRetrieverPlugin extends Plugin {

    private AudioFromVideoRetriever implementation = new AudioFromVideoRetriever();

    @PermissionCallback
    private void videoPermsCallback(PluginCall call) {
        if (getPermissionState("storage") == PermissionState.GRANTED) {
            extractAudio(call);
        } else {
            call.reject("Permission is required to take a picture");
        }
    }

    @PluginMethod
    public void extractAudio(PluginCall call) {
        if (getPermissionState("storage") != PermissionState.GRANTED) {
            requestPermissionForAlias("storage", call, "videoPermsCallback");
            return;
        }
        String path = call.getString("path");
        String outputPath = call.getString("outputPath");

        ContentResolver resolver = bridge.getContext().getContentResolver();
        File inputFile = implementation.getFileObject(path, resolver);
        File outputFile = implementation.getFileObject(outputPath, resolver);

        implementation.extractAudio(inputFile, outputFile, new AudioFromVideoRetriever.ExtractionCallback() {
            @Override
            public void onExtractionCompleted(File audioFile) {
                JSObject ret = new JSObject();
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
