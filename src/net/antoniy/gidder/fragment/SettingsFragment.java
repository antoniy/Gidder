package net.antoniy.gidder.fragment;

import net.antoniy.gidder.R;
import net.antoniy.gidder.service.SSHDaemonService;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

public class SettingsFragment extends BaseFragment implements OnClickListener {
	
	private Button startSshdButton;
	private Button stopSshdButton;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		LinearLayout mainContainer = (LinearLayout) inflater.inflate(R.layout.settings, null);

		startSshdButton = (Button) mainContainer.findViewById(R.id.startSshdButton);
		startSshdButton.setOnClickListener(this);
		
		stopSshdButton = (Button) mainContainer.findViewById(R.id.stopSshdButton);
		stopSshdButton.setOnClickListener(this);
		
		return mainContainer;
	}

	@Override
	public void onClick(View v) {
		int viewId = v.getId();
		
		Intent intent = new Intent(getActivity(), SSHDaemonService.class);
		if(viewId == R.id.startSshdButton) {
			getActivity().startService(intent);
		} else if(viewId == R.id.stopSshdButton) {
			getActivity().stopService(intent);
		}
	}
}
