import { WebPlugin } from '@capacitor/core';

import type { AudioFromVideoRetrieverPlugin } from './definitions';

export class AudioFromVideoRetrieverWeb
  extends WebPlugin
  implements AudioFromVideoRetrieverPlugin
{
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}
