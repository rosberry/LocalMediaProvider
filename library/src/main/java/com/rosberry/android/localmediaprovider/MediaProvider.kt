/*
 *
 *  * Copyright (c) 2019 Rosberry. All rights reserved.
 *
 */

package com.rosberry.android.localmediaprovider

import android.content.ContentResolver
import android.content.Context
import android.database.ContentObserver
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import com.rosberry.android.localmediaprovider.Constant.NO_FOLDER_ID
import com.rosberry.android.localmediaprovider.Constant.NO_LIMIT
import com.rosberry.android.localmediaprovider.sort.SortingMode
import com.rosberry.android.localmediaprovider.sort.SortingOrder

/**
 * @author mmikhailov on 2019-10-31.
 */
class MediaProvider(private val context: Context) {

    private var mediaUpdatesCallback: MediaUpdatesCallback? = null

    private var mediaContentObserver: ContentObserver? = null

    fun getLocalMedia(
            folderId: Long = NO_FOLDER_ID,
            limit: Int? = null,
            filterMode: FilterMode = FilterMode.ALL,
            sortingMode: SortingMode = SortingMode.DATE,
            sortingOrder: SortingOrder = SortingOrder.DESCENDING
    ): List<LocalMedia> {
        return queryFromMediaStore(
            if (folderId > NO_FOLDER_ID) folderId else NO_FOLDER_ID,
            limit,
            filterMode,
            sortingMode,
            sortingOrder
        )
    }

    fun registerMediaUpdatesCallback(callback: MediaUpdatesCallback) {
        mediaUpdatesCallback = callback
        registerListener()
    }

    fun unregisterMediaUpdatesCallback() {
        unregisterListener()
    }

    /**
     * Returns list of media refined by query params
     *
     * @param folderId should be {@link MediaProvider.noFolderId} to query all media
     * @param limit should be {@link MediaProvider.noLimit} to query all media
     * @param sortingMode
     * @param sortingOrder
     * @param filterMode
     * */
    private fun queryFromMediaStore(
            folderId: Long,
            limit: Int?,
            filterMode: FilterMode,
            sortingMode: SortingMode,
            sortingOrder: SortingOrder
    ): List<LocalMedia> {
        val query = Query(
            MediaStore.Files.getContentUri("external"),
            LocalMedia.projection,
            filterMode.selection(folderId),
            filterMode.args(folderId).map { arg -> arg.toString() }.toTypedArray(),
            sortingMode.mediaColumn,
            sortingOrder.isAscending,
            limit
        )

        return query.queryResults(context.contentResolver) { cursor -> LocalMedia(cursor) }
    }

    private fun registerListener() {
        mediaContentObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                mediaUpdatesCallback?.onChange(selfChange)
            }
        }.also { observer ->
            context.contentResolver.run {
                registerContentObserver(MediaStore.Images.Media.INTERNAL_CONTENT_URI, true, observer)
                registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, observer)
            }
        }
    }

    private fun unregisterListener() {
        mediaContentObserver?.let { observer ->
            context.contentResolver.unregisterContentObserver(observer)
            mediaContentObserver = null
            mediaUpdatesCallback = null
        }
    }

    private fun <T> Query.queryResults(contentResolver: ContentResolver, cursorHandler: CursorHandler<T>): List<T> {
        return getCursor(contentResolver).use { cursor ->
            val result = mutableListOf<T>()
            if (cursor != null && cursor.count > 0) {
                while (cursor.moveToNext()) {
                    result.add(cursorHandler.handle(cursor))
                }
            }
            result
        }
    }
}