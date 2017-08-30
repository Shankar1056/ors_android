package online.ors.oldraddisold.view;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 22 May 2017 at 7:34 PM
 */

public class GridSpacingHeaderItemDecoration extends GridSpacingItemDecoration {
	public GridSpacingHeaderItemDecoration(int spanCount, int spacing, boolean includeEdge) {
		super(spanCount, spacing, includeEdge);
	}
	
	@Override
	public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
		int position = parent.getChildAdapterPosition(view); // item position
		int spanCount = position == 0 ? 1 : this.spanCount;
		int column = position == 0 ? 0 : (position - 1) % spanCount; // item column
		
		outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
		outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)
		
		if (position + 1 < spanCount || position == 0) { // top edge
			outRect.top = spacing;
		}
		outRect.bottom = spacing; // item bottom
	}
}
