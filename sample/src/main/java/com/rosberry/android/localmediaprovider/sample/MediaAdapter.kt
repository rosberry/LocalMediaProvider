/*
 *
 *  * Copyright (c) 2019 Rosberry. All rights reserved.
 *
 */

package com.rosberry.android.localmediaprovider.sample

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rosberry.android.localmediaprovider.LocalMedia
import kotlinx.android.synthetic.main.i_media.view.*

/**
 * @author Alexei Korshun on 10.03.2020.
 */
class MediaAdapter(private val maxWidth: Int) : RecyclerView.Adapter<MediaViewHolder>() {

    private var items: List<LocalMedia> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        return LayoutInflater.from(parent.context)
            .inflate(R.layout.i_media, parent, false)
            .apply { updateLayoutParams { this.width = maxWidth; this.height = this.width } }
            .let { view -> MediaViewHolder(view) }
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun showItems(newItems: List<LocalMedia>) {
        LocalMediaDiffUtil(items, newItems)
            .run { DiffUtil.calculateDiff(this) }
            .also { items = newItems }
            .dispatchUpdatesTo(this)
    }
}

class MediaViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    fun bind(item: LocalMedia) {
        itemView.run {
            itemView.textType.text = item.mimeType
            if (item.mimeType.startsWith("image/")) {
                Glide.with(imageMedia)
                    .load(item.uri)
                    .into(imageMedia)
            } else {
                imageMedia.setImageDrawable(null)
            }
        }
    }
}