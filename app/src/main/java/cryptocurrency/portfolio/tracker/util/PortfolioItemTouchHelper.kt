package cryptocurrency.portfolio.tracker.util

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

abstract class PortfolioItemTouchHelperCallback (
    val trashIcon: Drawable
        ): ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

    private val swipeBackground = ColorDrawable(Color.RED)

    abstract fun onAssetSwiped(pos: Int): Unit

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        onAssetSwiped(viewHolder.bindingAdapterPosition)
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

        if (dX < 0) {
            val itemView = viewHolder.itemView
            val iconMargin = (itemView.height - trashIcon.intrinsicHeight) / 2

            trashIcon.setBounds(itemView.right - iconMargin - trashIcon.intrinsicWidth,
                itemView.top + iconMargin, itemView.right - iconMargin,
                itemView.top + trashIcon.intrinsicHeight + iconMargin)

            swipeBackground.setBounds((itemView.right + dX - 20).toInt(), itemView.top,
                itemView.right, itemView.bottom)

            swipeBackground.draw(c)
            trashIcon.draw(c)
        }
    }
}