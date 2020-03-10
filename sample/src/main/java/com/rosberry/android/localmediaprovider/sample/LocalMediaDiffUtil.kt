/*
 *
 *  * Copyright (c) 2020 Rosberry. All rights reserved.
 *
 */

package com.rosberry.android.localmediaprovider.sample

import androidx.recyclerview.widget.DiffUtil
import com.rosberry.android.localmediaprovider.LocalMedia

/**
 * @author Alexei Korshun on 10.03.2020.
 */
class LocalMediaDiffUtil(
        private val oldItems: List<LocalMedia>,
        private val newItems: List<LocalMedia>
) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldItems[oldItemPosition]
        val newItem = newItems[newItemPosition]
        return oldItem == newItem
    }

    override fun getOldListSize(): Int = oldItems.size

    override fun getNewListSize(): Int = newItems.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldItems[oldItemPosition]
        val newItem = newItems[newItemPosition]
        return oldItem == newItem
    }
}