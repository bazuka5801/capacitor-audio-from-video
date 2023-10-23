export interface AudioFromVideoRetrieverPlugin {
  extractAudio(options: { path: string, outputPath: string, includeData?: boolean }): Promise<{ path: string, dataUrl?: string }>;
}
