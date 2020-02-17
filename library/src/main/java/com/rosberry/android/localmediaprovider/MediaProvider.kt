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
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject

/**
 * @author mmikhailov on 2019-10-31.
 */
class MediaProvider(private val context: Context) {

    private val mediaUpdateSubject = BehaviorSubject.createDefault(true)

    private var mediaContentObserver: ContentObserver? = null

    fun getLocalMedia(
            folderId: Long = NO_FOLDER_ID,
            limit: Int = NO_LIMIT,
            filterMode: FilterMode = FilterMode.ALL,
            sortingMode: SortingMode = SortingMode.DATE,
            sortingOrder: SortingOrder = SortingOrder.DESCENDING
    ): Single<List<LocalMedia>> {

        val finalFolderId = if (folderId.isValid()) folderId else NO_FOLDER_ID
        val finalLimit = if (limit.isValid()) limit else NO_LIMIT

        return queryFromMediaStore(finalFolderId, finalLimit, sortingMode, sortingOrder, filterMode)
    }

    fun listenMediaUpdates(): Observable<Boolean> = mediaUpdateSubject
        .doOnSubscribe {
            if (mediaContentObserver == null) {
                registerListener()
            }
        }
        .doOnDispose { unregisterListener() }

    /**
     * Returns Single of media list refined by query params
     *
     * @param folderId should be {@link MediaProvider.noFolderId} to query all media
     * @param limit should be {@link MediaProvider.noLimit} to query all media
     * @param sortingMode
     * @param sortingOrder
     * @param filterMode
     * */
    private fun queryFromMediaStore(
            folderId: Long,
            limit: Int,
            sortingMode: SortingMode,
            sortingOrder: SortingOrder,
            filterMode: FilterMode
    ): Single<List<LocalMedia>> {

        val query = Query.Builder()
            .uri(MediaStore.Files.getContentUri("external"))
            .selection(filterMode.selection(folderId))
            .args(*filterMode.args(folderId))
            .projection(LocalMedia.projection)
            .sort(sortingMode.mediaColumn)
            .ascending(sortingOrder.isAscending)
            .limit(limit)
            .build()

        return query.queryResults(context.contentResolver,
                CursorHandler { LocalMedia(it) })
    }

    private fun FilterMode.selection(folderId: Long): String =
            when (this) {
                FilterMode.ALL -> {
                    if (folderId.isValid()) {
                        String.format("(%s=? or %s=?) and %s=?",
                                MediaStore.Files.FileColumns.MEDIA_TYPE,
                                MediaStore.Files.FileColumns.MEDIA_TYPE,
                                MediaStore.Files.FileColumns.PARENT)
                    } else {
                        String.format("%s=? or %s=?",
                                MediaStore.Files.FileColumns.MEDIA_TYPE,
                                MediaStore.Files.FileColumns.MEDIA_TYPE)
                    }
                }
                else -> {
                    if (folderId.isValid()) {
                        String.format("%s=? and %s=?",
                                MediaStore.Files.FileColumns.MEDIA_TYPE,
                                MediaStore.Files.FileColumns.PARENT)
                    } else {
                        String.format("%s=?", MediaStore.Files.FileColumns.MEDIA_TYPE)
                    }
                }
            }

    private fun FilterMode.args(folderId: Long): Array<Any> =
            when (this) {
                FilterMode.ALL -> {
                    if (folderId.isValid()) {
                        arrayOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE,
                                MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO,
                                folderId)
                    } else {
                        arrayOf<Any>(
                                MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE,
                                MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO
                        )
                    }
                }
                FilterMode.IMAGES -> {
                    if (folderId.isValid()) {
                        arrayOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE, folderId)
                    } else {
                        arrayOf<Any>(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE)
                    }
                }
                FilterMode.VIDEO -> {
                    if (folderId.isValid()) {
                        arrayOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO, folderId)
                    } else {
                        arrayOf<Any>(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO)
                    }
                }
            }

    private fun Long.isValid(): Boolean = this > NO_FOLDER_ID

    private fun Int.isValid(): Boolean = this > NO_LIMIT

    private fun <T> Query.queryResults(cr: ContentResolver, ch: CursorHandler<T>): Single<List<T>> =
            Single.create { emitter ->
                try {
                    this.getCursor(cr)
                        .use { cursor ->
                            val result = mutableListOf<T>()
                            if (cursor != null && cursor.count > 0) {
                                while (cursor.moveToNext()) {
                                    result.add(ch.handle(cursor))
                                }
                            }

                            emitter.onSuccess(result)
                        }
                } catch (err: Exception) {
                    emitter.onError(err)
                }
            }

    private fun registerListener() {
        mediaContentObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                mediaUpdateSubject.onNext(true)
            }
        }.also { observer ->
            context.contentResolver.registerContentObserver(MediaStore.Images.Media.INTERNAL_CONTENT_URI, true,
                    observer)

            context.contentResolver.registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true,
                    observer)
        }
    }

    private fun unregisterListener() {
        mediaContentObserver?.let { observer ->
            context.contentResolver.unregisterContentObserver(observer)
            mediaContentObserver = null
        }
    }
}