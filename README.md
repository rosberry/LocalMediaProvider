# Local Media Provider (by Rosberry)

The library is providing an easy way to fetch media files (photo and video) from media storage.

## Usage

### Add a dependency

```groovy
implementation "com.rosberry.android.localmediaprovider:$localprovider_version"
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

## License

```
Copyright 2020 Rosberry.

Licensed to the Apache Software Foundation (ASF) under one or more contributor
license agreements. See the NOTICE file distributed with this work for
additional information regarding copyright ownership. The ASF licenses this
file to you under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License. You may obtain a copy of
the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
License for the specific language governing permissions and limitations under
the License.
```
