# Local Media Provider (by Rosberry)

The library is providing an easy way to fetch media files (photo and video) from media storage.

## Usage

### Add a dependency

```groovy
implementation "com.rosberry.android.localmediaprovider:$localprovider_version"
```

### Quary files

```kotlin
val disposable = MediaProvider(context).getLocalMedia(
    folderId = NO_FOLDER_ID,
    limit = NO_LIMIT,
    filterMode = FilterMode.ALL,
    sortingMode = SortingMode.DATE,
    sortingOrder = SortingOrder.DESCENDING
)
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())
    .subscribe { mediaList: List<LocalMedia> -> //work with data }
```

For query files use the `getLocalMedia` method, which return a `Single<List<LocalMedia>>`. By an arguments you can customize your query:

 - folderId - id of the folder in which to query (default is `NO_FOLDER_ID`)
 - limit - limit of the auery (default is `NO_LIMIT`)
 - filterMode - can be `ALL`, `VIDEO` or `IMAGE`
 - sortingMode - can be `NAME`, `DATE`, `SIZE`, `TYPE` or `NUMERIC`
 - sortingOrder - can be `ASCENDING` or `DESCENDING`
 
 ### Listen media updates
 
 ```kotlin
 val disposable = MediaProvider(context).listenMediaUpdates()
     .subscribe { isUpdated: Boolean -> //do what you need when media was updated. }
 ```
⚠️ **Attention** 
`Observable` from `listenMediaUpdates` is publishing the events on the main thread.

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
