# Local Media Provider
The library is providing an easy way to fetch media files (photo and video) from media storage.

## Requirements
Android API 21+

## Usage

Add a dependency:
```groovy
implementation 'com.rosberry.android:LocalMediaProvider:0.1.0'
```

### Query files
```kotlin
val mediaList = MediaProvider(context).getLocalMedia(
    folderId = NO_FOLDER_ID,
    limit = NO_LIMIT,
    filterMode = FilterMode.ALL,
    sortingMode = SortingMode.DATE,
    sortingOrder = SortingOrder.DESCENDING
)
```

Use `getLocalMedia` method to query files, which return a list of `LocalMedia`. You can customize your query with arguments:
 - folderId - id of the folder in which to query (default is `NO_FOLDER_ID`)
 - limit - limit of the query (default is `NO_LIMIT`)
 - filterMode - can be `ALL`, `VIDEO` or `IMAGE` (default is `ALL`)
 - sortingMode - can be `NAME`, `DATE`, `SIZE`, `TYPE` or `NUMERIC` (default is `DATE`)
 - sortingOrder - can be `ASCENDING` or `DESCENDING` (default is `DESCENDING`)
 
### Listen media updates
 ```kotlin
 interface MediaUpdatesCallback {
     fun onChange(selfChange: Boolean)
 }
 ```
Register callback with `MediaProvider.registerMediaUpdatesCallback`.
`MediaUpdatesCallback.onChange` method will be invoked whenever content change occurs.
Don't forget to unregister callback with `MediaProvider.unregisterMediaCallback` when it isn't using.


⚠️ **Attention** 
`MediaUpdateCallback` is publishing the events on the main thread.

## About

<img src="https://github.com/rosberry/Foundation/blob/master/Assets/full_logo.png?raw=true" height="100" />

This project is owned and maintained by [Rosberry](http://rosberry.com). We build mobile apps for users worldwide 🌏.

Check out our [open source projects](https://github.com/rosberry), read [our blog](https://medium.com/@Rosberry) or give us a high-five on 🐦 [@rosberryapps](http://twitter.com/RosberryApps).

## License

Image Cropper is available under the Apache License 2.0. See the LICENSE file for more info.