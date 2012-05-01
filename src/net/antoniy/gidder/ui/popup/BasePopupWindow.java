package net.antoniy.gidder.ui.popup;

import net.antoniy.gidder.R;
import net.antoniy.gidder.ui.util.GidderCommons;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.PopupWindow;

/**
 * This class does most of the work of wrapping the {@link PopupWindow} so it's
 * simpler to use.
 * 
 * @author Antoniy Chonkov <antoniy@gmail.com>
 * 
 */
public abstract class BasePopupWindow {
	protected final View anchor;
	private final PopupWindow window;
	private View root;
	private final WindowManager windowManager;
	private final float heightInDp;

	public BasePopupWindow(View anchor) {
		this(anchor, 0);
	}
	
	/**
	 * Create a BetterPopupWindow
	 * 
	 * @param anchor
	 *            the view that the BetterPopupWindow will be displaying 'from'
	 */
	public BasePopupWindow(View anchor, float heightInDp) {
		this.anchor = anchor;
		this.heightInDp = heightInDp;
		this.window = new PopupWindow(anchor.getContext());

		// when a touch even happens outside of the window make the window go away
		this.window.setTouchInterceptor(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
					BasePopupWindow.this.window.dismiss();
					return true;
				}
				return false;
			}
		});

		this.windowManager = (WindowManager) this.anchor.getContext().getSystemService(Context.WINDOW_SERVICE);
	}

	private void preShow() {
		if (this.root == null) {
			throw new IllegalStateException(
					"setContentView was not called with a view to display.");
		}

		this.window.setBackgroundDrawable(new BitmapDrawable());

		this.window.setWidth(WindowManager.LayoutParams.FILL_PARENT);
		if(heightInDp == 0) {
			this.window.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);	
		} else {
			this.window.setHeight(GidderCommons.convertDpToPixels(windowManager, 300));
		}
		this.window.setTouchable(true);
		this.window.setFocusable(true);
		this.window.setOutsideTouchable(true);

		this.window.setContentView(this.root);
	}

	/**
	 * Sets the content view. Probably should be called from {@link onCreate}
	 * 
	 * @param root
	 *            the view the popup will display
	 */
	public void setContentView(View root) {
		this.root = root;
		this.window.setContentView(root);
	}

	/**
	 * Will inflate and set the view from a resource id
	 * 
	 * @param layoutResID
	 */
	public void setContentView(int layoutResID) {
		LayoutInflater inflator = (LayoutInflater) this.anchor.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.setContentView(inflator.inflate(layoutResID, null));
	}

	/**
	 * If you want to do anything when {@link dismiss} is called
	 * 
	 * @param listener
	 */
	public void setOnDismissListener(PopupWindow.OnDismissListener listener) {
		this.window.setOnDismissListener(listener);
	}


	/**
	 * Displays like a QuickAction from the anchor view.
	 */
	public void showLikeQuickAction() {
		this.preShow();

		this.window.setAnimationStyle(R.style.Animations_GrowFromBottom);
		this.window.showAtLocation(this.anchor, Gravity.CENTER, 0, 0);
	}

	public void dismiss() {
		this.window.dismiss();
	}
}