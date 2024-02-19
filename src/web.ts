import { WebPlugin } from '@capacitor/core';

import type { AudioFromVideoRetrieverPlugin } from './definitions';

export class AudioFromVideoRetrieverWeb
  extends WebPlugin
  implements AudioFromVideoRetrieverPlugin
{
async compressVideo(options: { path: string; outputPath: string; width: number; height: number; bitrate: number; }): Promise<{ path: string; }> {
  console.log('ECHO', options);
  return  { path: options.path };
}
  async extractAudio(options: { path: string, outputPath: string, includeData?: boolean | undefined }): Promise<{ path: string, dataUrl?: string }> {
    console.log('ECHO', options);
    return options;
  }
}
