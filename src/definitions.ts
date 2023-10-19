export interface AudioFromVideoRetrieverPlugin {
  extractAudio(options: { path: string, outputPath: string }): Promise<{ path: string }>;
}
