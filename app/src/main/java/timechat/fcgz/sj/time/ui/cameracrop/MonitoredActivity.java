package timechat.fcgz.sj.time.ui.cameracrop;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;



/**
 * @author zyzhang
 * @date 2016-1-28 下午12:16:05
 */
public class MonitoredActivity extends Activity {

	private final ArrayList<LifeCycleListener> mListeners = new ArrayList<LifeCycleListener>();

	public static interface LifeCycleListener {
		public void onActivityCreated(MonitoredActivity activity);

		public void onActivityDestroyed(MonitoredActivity activity);

		public void onActivityStarted(MonitoredActivity activity);

		public void onActivityStopped(MonitoredActivity activity);
	}

	public static class LifeCycleAdapter implements LifeCycleListener {
		public void onActivityCreated(MonitoredActivity activity) {
		}

		public void onActivityDestroyed(MonitoredActivity activity) {
		}

		public void onActivityStarted(MonitoredActivity activity) {
		}

		public void onActivityStopped(MonitoredActivity activity) {
		}
	}

	public void addLifeCycleListener(LifeCycleListener listener) {
		if (mListeners.contains(listener))
			return;
		mListeners.add(listener);
	}

	public void removeLifeCycleListener(LifeCycleListener listener) {
		mListeners.remove(listener);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);


		for (LifeCycleListener listener : mListeners) {
			listener.onActivityCreated(this);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		for (LifeCycleListener listener : mListeners) {
			listener.onActivityDestroyed(this);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		for (LifeCycleListener listener : mListeners) {
			listener.onActivityStarted(this);
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		for (LifeCycleListener listener : mListeners) {
			listener.onActivityStopped(this);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public void finish() {
		super.finish();
	}
	
	
}
