/*
 *
 *  * Copyright (c) 2020 Rosberry. All rights reserved.
 *  
 */

package com.rosberry.android.localmediaprovider

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi

class Query(
        private val uri: Uri? = null,
        private val projection: Array<String>? = null,
        private val selection: String? = null,
        private val selectionArgs: Array<String>? = null,
        private val sortBy: String? = null,
        private val ascending: Boolean = false,
        private val limit: Int? = null
) {

    private val sortOrder: String?
        get() = when {
            sortBy == null && limit == Constant.NO_LIMIT -> null
            else -> StringBuilder().apply {
                // Sorting by Relative Position
                // ORDER BY 1
                // sort by the first column in the PROJECTION
                // otherwise the LIMIT should not work
                append(sortBy ?: 1)

                if (!ascending) append(" DESC")

                limit?.let { append(" LIMIT").append(" ").append(limit) }
            }.toString()
        }

    fun getCursor(contentResolver: ContentResolver): Cursor? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            contentResolver.query(
                uri!!,
                projection,
                createQueryBundle(),
                null
            )
        } else contentResolver.query(uri!!, projection, selection, selectionArgs, sortOrder)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createQueryBundle(): Bundle {
        return Bundle().apply {
            putString(ContentResolver.QUERY_ARG_SQL_SELECTION, selection)
            putStringArray(ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS, selectionArgs)
            limit?.let { putInt(ContentResolver.QUERY_ARG_LIMIT, limit) }
            sortBy?.let { putStringArray(ContentResolver.QUERY_ARG_SORT_COLUMNS, arrayOf(sortBy)) }

            if (!ascending) putInt(
                ContentResolver.QUERY_ARG_SORT_DIRECTION,
                ContentResolver.QUERY_SORT_DIRECTION_DESCENDING
            )
        }
    }
}