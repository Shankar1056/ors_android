package online.ors.oldraddisold.view;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 13 Apr 2017 at 5:48 PM
 */

public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {
	
	int spanCount;
	int spacing;
	private boolean includeEdge;
	
	public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
		this.spanCount = spanCount;
		this.spacing = spacing;
		this.includeEdge = includeEdge;
	}
	
	@Override
	public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
		int position = parent.getChildAdapterPosition(view); // item position
		int column = position % spanCount; // item column
		
		if (includeEdge) {
			outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
			outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)
			
			if (position < spanCount) { // top edge
				outRect.top = spacing;
			}
			outRect.bottom = spacing; // item bottom
		} else {
			outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
			outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
			if (position >= spanCount) {
				outRect.top = spacing; // item top
			}
		}
	}
}