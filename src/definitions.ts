export interface AudioFromVideoRetrieverPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
