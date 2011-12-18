package net.antoniy.gidder.popup;

import java.util.ArrayList;
import java.util.List;

import net.antoniy.gidder.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class RepositoryActionsPopupWindow extends BetterPopupWindow implements OnClickListener {

	public final static int RESULT_UNDEFINED = 0;
	public final static int RESULT_EDIT = 1;
	public final static int RESULT_DELETE = 2;
	
	private int result = RESULT_UNDEFINED;
	private List<OnActionItemClickListener> listeners;
	private int position;
	
	public RepositoryActionsPopupWindow(View anchor, int position) {
		super(anchor);
		
		this.listeners = new ArrayList<OnActionItemClickListener>();
		this.position = position;
		
		onCreate();
	}

	private void onCreate() {
		// inflate layout
		LayoutInflater inflater = (LayoutInflater) this.anchor.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		ViewGroup root = (ViewGroup) inflater.inflate(R.layout.repositories_actions_popup, null);

		View edit = root.findViewById(R.id.actionPopupEdit);
		edit.setOnClickListener(this);
		
		View delete = root.findViewById(R.id.actionPopupDelete);
		delete.setOnClickListener(this);
		
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
