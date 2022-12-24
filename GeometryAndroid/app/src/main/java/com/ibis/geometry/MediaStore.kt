package com.ibis.geometry

import android.content.ContentResolver
import android.content.ContentValues
import android.graphics.Bitmap
import android.os.Build
import android.provider.MediaStore
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import java.io.OutputStream

@JvmInline
value class MediaStore(val resolver: ContentResolver) : com.ibis.geometry.common.MediaStore {
    override fun writeImage(image: ImageBitmap, stream: OutputStream) {
        image.asAndroidBitmap().compress(Bitmap.CompressFormat.PNG, 90, stream)
    }

    override fun init(ext: String): OutputStream {
        val collection = MediaStore.Files.getContentUri("external")
        val values = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, "geometry.$ext")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                put(MediaStore.Downloads.IS_PENDING, 1)
        }
        val uri = resolver.insert(collection, values)!!
        values.clear()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            values.put(MediaStore.Downloads.IS_PENDING, 0)
        val stream = resolver.openOutputStream(uri)!!
        return object : OutputStream() {
            override fun write(value: Int) {
                stream.write(value)
            }

            override fun write(b: ByteArray?, off: Int, len: Int) {
                stream.write(b, off, len)
            }

            override fun write(b: ByteArray?) {
                stream.write(b)
            }

            override fun flush() {
                stream.flush()
            }

            override fun close() {
                stream.close()
                resolver.update(uri, values, null, null)
            }
        }
    }
}
