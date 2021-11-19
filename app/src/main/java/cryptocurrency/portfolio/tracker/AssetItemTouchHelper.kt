package cryptocurrency.portfolio.tracker

import android.graphics.Canvas
import android.util.Log
import androidx.recyclerview.widget.AdapterListUpdateCallback
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import cryptocurrency.portfolio.tracker.adapters.AssetListAdapter
import cryptocurrency.portfolio.tracker.callbacks.DeleteItemCallback

private const val LOG_TAG = "AssetItemTouchHelper"

class AssetItemTouchHelper(private val callback: DeleteItemCallback): ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

    private var flagSwiped = false

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false


    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        callback.deleteItem(viewHolder.absoluteAdapterPosition)
        Log.v(LOG_TAG, "swiped")
    }

//    override fun onChildDraw(
//        c: Canvas,
//        recyclerView: RecyclerView,
//        viewHolder: RecyclerView.ViewHolder,
//        dX: Float,
//        dY: Float,
//        actionState: Int,
//        isCurrentlyActive: Boolean
//    ) {
//
//            val foreground = (viewHolder as AssetListAdapter.ViewHolder).binding.item
//            getDefaultUIUtil().onDraw(c, recyclerView, foreground,
//                dX/4, dY, actionState, isCurrentlyActive)
//
//
////        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
//    }

}