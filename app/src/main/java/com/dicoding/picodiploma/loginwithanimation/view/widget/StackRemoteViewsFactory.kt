import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import com.dicoding.picodiploma.loginwithanimation.data.response.ListStoryItem
import kotlinx.coroutines.runBlocking
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

internal class StackRemoteViewsFactory(
    private val mContext: Context,
    private val repository: UserRepository
) : RemoteViewsService.RemoteViewsFactory {

    private val mWidgetItems = ArrayList<Bitmap>()
    private var storiesList: List<ListStoryItem> = listOf()

    override fun onCreate() {
        // Tidak ada
    }

    override fun onDataSetChanged() {
        mWidgetItems.clear()

        runBlocking {
            val result = repository.getStories()
            result.onSuccess {
                storiesList = it
            }.onFailure {
                storiesList = emptyList()
            }
        }

        storiesList.forEach { story ->
            val bitmap = getBitmapFromURL(story.photoUrl) ?:
            BitmapFactory.decodeResource(mContext.resources, R.drawable.baseline_image_24)
            mWidgetItems.add(bitmap)
        }
    }

    override fun onDestroy() {
        mWidgetItems.clear()
    }

    override fun getCount(): Int = mWidgetItems.size

    override fun getViewAt(position: Int): RemoteViews {
        val rv = RemoteViews(mContext.packageName, R.layout.widget_item)
        rv.setImageViewBitmap(R.id.imageView, mWidgetItems[position])

        val fillInIntent = Intent()
        rv.setOnClickFillInIntent(R.id.imageView, fillInIntent)

        return rv
    }

    private fun getBitmapFromURL(src: String): Bitmap? {
        return try {
            val url = URL(src)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input: InputStream = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(position: Int): Long = position.toLong()

    override fun hasStableIds(): Boolean = true
}
