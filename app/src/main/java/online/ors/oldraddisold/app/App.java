package online.ors.oldraddisold.app;

import android.app.Application;

import online.ors.oldraddisold.R;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 08 May 2017 at 5:50 PM
 */

public class App extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		
		CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
		    .setDefaultFontPath("fonts/lato-bold.ttf")
		    .setFontAttrId(R.attr.fontPath)
		    .build()
		);
	}
}
