package online.ors.oldraddisold.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 12 May 2017 at 6:28 PM
 */

public class ScrollControlLinearLayoutManager extends LinearLayoutManager {
	private boolean isScrollEnabled = true;
	
	public ScrollControlLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
		super(context, orientation, reverseLayout);
		isScrollEnabled = true;
	}
	
	public ScrollControlLinearLayoutManager(Context context) {
		super(context);
	}
	
	@Override
	public boolean canScrollHorizontally() {
		return isScrollEnabled && super.canScrollHorizontally();
	}
	
	@Override
	public boolean canScrollVertically() {
		return isScrollEnabled && super.canScrollVertically();
	}
}
