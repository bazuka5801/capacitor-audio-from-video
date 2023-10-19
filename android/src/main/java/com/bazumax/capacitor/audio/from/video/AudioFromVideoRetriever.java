package com.bazumax.capacitor.audio.from.video;

import android.content.ContentResolver;
import android.database.Cursor;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.net.Uri;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;


public class AudioFromVideoRetriever {


    public interface ExtractionCallback {
        void onExtractionCompleted(File audioFile);
        void onExtractionFailed(String errorMessage);
    }

    public File getFileObject(String path, ContentResolver resolver) {
        Uri u = Uri.parse(path);
        if (u != null && "content".equals(u.getScheme())) {
            Cursor cursor = resolver.query(u, new String[]{android.provider.MediaStore.Images.ImageColumns.DATA}, null, null, null);
            cursor.moveToFirst();
            String filePath = cursor.getString(0);
            cursor.close();
            return new File(filePath);
        }
        if (u.getScheme() == null || u.getScheme().equals("file")) {
            return new File(u.getPath());
        }
        return null;
    }

    public void extractAudio(File videoFile, File outputAudioFile, ExtractionCallback callback) {
        MediaExtractor extractor = new MediaExtractor();

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(videoFile);
            FileDescriptor fd = fis.getFD();
            extractor.setDataSource(fd);
        } catch (IOException e) {
            callback.onExtractionFailed("Failed to set data source: " + e.getMessage());
            return;
        }
        catch (Exception e) {
            e.printStackTrace();
        } finally {
            //Release stuff
            try {
                if(fis != null) {
                    fis.close();
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        int audioTrackIndex = findAudioTrack(extractor);
        if (audioTrackIndex == -1) {
            callback.onExtractionFailed("No audio track found in the video");
            return;
        }

        extractor.selectTrack(audioTrackIndex);
        MediaFormat audioFormat = extractor.getTrackFormat(audioTrackIndex);
        String mimeType = audioFormat.getString(MediaFormat.KEY_MIME);

        MediaCodec codec = null;
        FileOutputStream outputStream = null;

        try {
            codec = MediaCodec.createDecoderByType(mimeType);
            codec.configure(audioFormat, null, null, 0);
            codec.start();

            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
            boolean isExtractorEOS = false;
            boolean isDecoderEOS = false;

            outputStream = new FileOutputStream(outputAudioFile);

            while (!isDecoderEOS) {
                if (!isExtractorEOS) {
                    int inputBufferIndex = codec.dequeueInputBuffer(10000);
                    if (inputBufferIndex >= 0) {
                        ByteBuffer inputBuffer = codec.getInputBuffer(inputBufferIndex);
                        int sampleSize = extractor.readSampleData(inputBuffer, 0);
                        if (sampleSize < 0) {
                            codec.queueInputBuffer(inputBufferIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                            isExtractorEOS = true;
                        } else {
                            codec.queueInputBuffer(inputBufferIndex, 0, sampleSize, extractor.getSampleTime(), 0);
                            extractor.advance();
                        }
                    }
                }

                int outputBufferIndex = codec.dequeueOutputBuffer(bufferInfo, 10000);
                if (outputBufferIndex >= 0) {
                    ByteBuffer outputBuffer = codec.getOutputBuffer(outputBufferIndex);
                    byte[] chunk = new byte[bufferInfo.size];
                    outputBuffer.get(chunk);
                    outputBuffer.clear();
                    outputStream.write(chunk);
                    codec.releaseOutputBuffer(outputBufferIndex, false);
                    if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                        isDecoderEOS = true;
                    }
                } else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                    // Ignore format change
                }
            }

            callback.onExtractionCompleted(outputAudioFile);
        } catch (IOException e) {
            callback.onExtractionFailed("Audio extraction failed: " + e.getMessage());
        } finally {
            if (codec != null) {
                codec.stop();
                codec.release();
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            extractor.release();
        }
    }

    private static int findAudioTrack(MediaExtractor extractor) {
        int numTracks = extractor.getTrackCount();
        for (int i = 0; i < numTracks; i++) {
            MediaFormat format = extractor.getTrackFormat(i);
            String mime = format.getString(MediaFormat.KEY_MIME);
            if (mime.startsWith("audio/")) {
                return i;
            }
        }
        return -1;
    }
}