package net.antoniy.gidder.popup;

import net.antoniy.gidder.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.Toast;

public class ActionsPopupWindow extends BetterPopupWindow implements
		OnClickListener {

	public ActionsPopupWindow(View anchor) {
		super(anchor);
	}

	@Override
	protected void onCreate() {
		// inflate layout
		LayoutInflater inflater = (LayoutInflater) this.anchor.getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		ViewGroup root = (ViewGroup) inflater.inflate(R.layout.actions_popup, null);

		// setup button events
		for (int i = 0, icount = root.getChildCount(); i < icount; i++) {
			View v = root.getChildAt(i);

			if (v instanceof TableRow) {
				TableRow row = (TableRow) v;

				for (int j = 0, jcount = row.getChildCount(); j < jcount; j++) {
					View item = row.getChildAt(j);
					if (item instanceof Button) {
						Button b = (Button) item;
						b.setOnClickListener(this);
					}
				}
			}
		}

		// set the inflated view as what we want to display
		this.setContentView(root);
	}

	@Override
	public void onClick(View v) {
		// we'll just display a simple toast on a button click
		Button b = (Button) v;
		Toast.makeText(this.anchor.getContext(), b.getText(), Toast.LENGTH_SHORT).show();
		this.dismiss();
	}
}
