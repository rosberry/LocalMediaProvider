/*
 *
 *  * Copyright (c) 2019 Rosberry. All rights reserved.
 *
 */

package com.rosberry.android.localmediaprovider.sample

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.tabs.TabLayout
import com.rosberry.android.localmediaprovider.FilterMode
import com.rosberry.android.localmediaprovider.MediaProvider
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.a_main.*

/**
 * @author Alexei Korshun on 10.03.2020.
 */
class SampleActivity : AppCompatActivity(R.layout.a_main) {

    private val tag = "SAMPLE"

    private val readStoragePermission = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)

    private val readStoragePermissionCode = 1987

    private val disposable = CompositeDisposable()

    private val spanCount = 3

    private val cellWidth by lazy { resources.displayMetrics.widthPixels.div(spanCount) }

    private val adapter by lazy { MediaAdapter(cellWidth) }

    private val mediaProvider: MediaProvider by lazy {
        MediaProvider(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initTabs()
        listMedia.layoutManager = GridLayoutManager(this, spanCount, GridLayoutManager.VERTICAL, false)
        listMedia.adapter = adapter
        if (savedInstanceState == null) {
            loadData()
        }
    }

    override fun onDestroy() {
        disposable.dispose()
        super.onDestroy()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == readStoragePermissionCode && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loadData()
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun initTabs() {
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabReselected(tab: TabLayout.Tab) {
                Log.d(tag, "${tab.text} was reselected")
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                Log.d(tag, "${tab.text} was unselected")
            }

            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.text) {
                    "ALL" -> loadData()
                    "IMAGE" -> loadData(filterMode = FilterMode.IMAGES)
                    "VIDEO" -> loadData(filterMode = FilterMode.VIDEO)
                }
            }
        })
    }

    private fun loadData(filterMode: FilterMode = FilterMode.ALL) {
        if (isReadStoragePermissionsGranted()) {
            mediaProvider.getLocalMedia(filterMode = filterMode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { mediaList -> adapter.showItems(mediaList) },
                        { error -> error.printStackTrace() }
                )
                .connect()
        } else {
            ActivityCompat.requestPermissions(this, readStoragePermission, readStoragePermissionCode)
        }
    }

    private fun Disposable.connect() = disposable.add(this)
}