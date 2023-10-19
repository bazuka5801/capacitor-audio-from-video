import { WebPlugin } from '@capacitor/core';

import type { AudioFromVideoRetrieverPlugin } from './definitions';

export class AudioFromVideoRetrieverWeb
  extends WebPlugin
  implements AudioFromVideoRetrieverPlugin
{
  async extractAudio(options: { path: string, outputPath: string }): Promise<{ path: string }> {
    console.log('ECHO', options);
    return options;
  }
}
