package net.antoniy.gidder.popup;

import java.util.ArrayList;
import java.util.List;

import net.antoniy.gidder.R;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class UserActionsPopupWindow extends BetterPopupWindow implements OnClickListener {
	private final static String TAG = UserActionsPopupWindow.class.getSimpleName();
	
	public final static int RESULT_UNDEFINED = 0;
	public final static int RESULT_EDIT = 1;
	public final static int RESULT_DELETE = 2;
	public final static int RESULT_ACTIVATE = 3;
	public final static int RESULT_DEACTIVATE = 4;
	
	private int result = RESULT_UNDEFINED;
	private List<OnActionItemClickListener> listeners;
	private int position;
	private boolean userActive;
	
	public UserActionsPopupWindow(View anchor, int position, boolean userActive) {
		super(anchor);
		
		this.listeners = new ArrayList<OnActionItemClickListener>();
		this.position = position;
		this.userActive = userActive;
		Log.i(TAG, "userActive: " + this.userActive);
		
		onCreate();
	}

	private void onCreate() {
		// inflate layout
		LayoutInflater inflater = (LayoutInflater) this.anchor.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		ViewGroup root = (ViewGroup) inflater.inflate(R.layout.users_actions_popup, null);

		View edit = root.findViewById(R.id.actionPopupEdit);
		edit.setOnClickListener(this);
		
		View delete = root.findViewById(R.id.actionPopupDelete);
		delete.setOnClickListener(this);
		
		View activateDeactivate = root.findViewById(R.id.actionPopupActivateDeactivate);
		activateDeactivate.setOnClickListener(this);
		
		ImageView activateDeactivateImage = (ImageView) activateDeactivate.findViewById(R.id.actionPopupActivateDeactivateImage);
		TextView activateDeactivateText = (TextView) activateDeactivate.findViewById(R.id.actionPopupActivateDeactivateText);
		if(userActive) {
			activateDeactivateImage.setImageResource(R.drawable.ic_action_deactivate);
			activateDeactivateText.setText(R.string.action_popup_deactivate);
		} else {
			activateDeactivateImage.setImageResource(R.drawable.ic_action_activate);
			activateDeactivateText.setText(R.string.action_popup_activate);
		}
		
		// set the inflated view as what we want to display
		this.setContentView(root);
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.actionPopupEdit) {
			result = RESULT_EDIT;
			fireOnActionItemClick();
		} else if(v.getId() == R.id.actionPopupDelete) {
			result = RESULT_DELETE;
			fireOnActionItemClick();
		} else if(v.getId() == R.id.actionPopupActivateDeactivate) {
			if(userActive) {
				result = RESULT_DEACTIVATE;
			} else {
				result = RESULT_ACTIVATE;
			}
			fireOnActionItemClick();
		}
		this.dismiss();
	}
	
	private void fireOnActionItemClick() {
		for (OnActionItemClickListener listener : listeners) {
			listener.onActionItemClick(anchor, position, result);
		}
	}
	
	public int getResult() {
		return result;
	}
	
	public void addOnActionItemClickListener(OnActionItemClickListener listener) {
		listeners.add(listener);
	}
}
