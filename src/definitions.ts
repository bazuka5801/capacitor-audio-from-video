export interface AudioFromVideoRetrieverPlugin {
  extractAudio(options: { path: string, outputPath: string, includeData?: boolean }): Promise<{ path: string, dataUrl?: string }>;
  compressVideo(options: { path: string, outputPath: string, width: number, height: number,bitrate: number  }): Promise<{ path: string }>;
}
