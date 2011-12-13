package net.antoniy.gidder.fragment;

import net.antoniy.gidder.activity.SlideActivity;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;

public abstract class ContextMenuFragment extends BaseFragment {
	
	protected final static int CONTEXT_MENU_ITEM_EDIT = 1;
	protected final static int CONTEXT_MENU_ITEM_REMOVE = 2;
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle("Actions");
		menu.add(ContextMenu.NONE, CONTEXT_MENU_ITEM_EDIT, ContextMenu.NONE, "Edit");
		menu.add(ContextMenu.NONE, CONTEXT_MENU_ITEM_REMOVE, ContextMenu.NONE, "Delete");
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if(((SlideActivity)getActivity()).getCurrentFragment() != FragmentType.USERS) {
			return false;
		}
		
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		
		switch (item.getItemId()) {
		case CONTEXT_MENU_ITEM_EDIT:
			onContextMenuEditItemSelected(info.position);
			break;
		case CONTEXT_MENU_ITEM_REMOVE:
			onContextMenuRemoveItemSelected(info.position);
			break;
		}
		
		return super.onContextItemSelected(item);
	}
	
	protected abstract void onContextMenuEditItemSelected(int position);
	
	protected abstract void onContextMenuRemoveItemSelected(int position);
}
