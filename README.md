# capacitor-audio-from-video

Exctract Audio from Video file

## Install

```bash
npm install capacitor-audio-from-video
npx cap sync
```

## API

<docgen-index>

* [`extractAudio(...)`](#extractaudio)
* [`compressVideo(...)`](#compressvideo)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### extractAudio(...)

```typescript
extractAudio(options: { path: string; outputPath: string; includeData?: boolean; }) => Promise<{ path: string; dataUrl?: string; }>
```

| Param         | Type                                                                      |
| ------------- | ------------------------------------------------------------------------- |
| **`options`** | <code>{ path: string; outputPath: string; includeData?: boolean; }</code> |

**Returns:** <code>Promise&lt;{ path: string; dataUrl?: string; }&gt;</code>

--------------------


### compressVideo(...)

```typescript
compressVideo(options: { path: string; outputPath: string; width: number; height: number; bitrate: number; }) => Promise<{ path: string; }>
```

| Param         | Type                                                                                               |
| ------------- | -------------------------------------------------------------------------------------------------- |
| **`options`** | <code>{ path: string; outputPath: string; width: number; height: number; bitrate: number; }</code> |

**Returns:** <code>Promise&lt;{ path: string; }&gt;</code>

--------------------

</docgen-api>
