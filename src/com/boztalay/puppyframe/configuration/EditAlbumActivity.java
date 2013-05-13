package com.boztalay.puppyframe.configuration;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import com.boztalay.puppyframe.R;
import com.boztalay.puppyframe.persistence.Album;
import com.boztalay.puppyframe.persistence.PuppyFramePersistenceManager;

public class EditAlbumActivity extends Activity {
	public static final String ALBUM_ID_KEY = "albumId";

	public enum EditMode {
		EDITING, ADDING;
	}

	private PuppyFramePersistenceManager persistenceManager;
	private EditMode editingMode;
	private Album album;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_album);

		determineEditModeAndLoadAlbum();

		setUpViewsAndTitle();
	}

	private void determineEditModeAndLoadAlbum() {
		persistenceManager = new PuppyFramePersistenceManager(this);

		String editingAlbumId = getIntent().getStringExtra(ALBUM_ID_KEY);
		if(editingAlbumId != null) {
			editingMode = EditMode.EDITING;
			album = persistenceManager.getAlbumWithId(editingAlbumId);
		} else {
			editingMode = EditMode.ADDING;
			album = persistenceManager.createNewAlbum("Untitled Album");
		}
	}

	private void setUpViewsAndTitle() {
		if(editingMode == EditMode.EDITING) {
			getActionBar().setTitle(getString(R.string.edit_album_title));
		} else if(editingMode == EditMode.ADDING) {
			getActionBar().setTitle(getString(R.string.add_album_title));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.edit_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.done_editing_menu_action:
			createNamingDialog();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void createNamingDialog() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Name Your Album");

		final EditText input = new EditText(this);
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				album.setTitle(input.getText().toString());
				persistenceManager.saveAlbum(album);
				setResult(RESULT_OK);
				finish();
			}
		});
		alert.setNegativeButton("Cancel", null);
		
		alert.show();
	}
}
