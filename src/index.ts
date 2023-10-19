import { registerPlugin } from '@capacitor/core';

import type { AudioFromVideoRetrieverPlugin } from './definitions';

const AudioFromVideoRetriever = registerPlugin<AudioFromVideoRetrieverPlugin>(
  'AudioFromVideoRetriever',
  {
    web: () => import('./web').then(m => new m.AudioFromVideoRetrieverWeb()),
  },
);

export * from './definitions';
export { AudioFromVideoRetriever };
