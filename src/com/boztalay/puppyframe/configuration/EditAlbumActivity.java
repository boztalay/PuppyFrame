package com.boztalay.puppyframe.configuration;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.GridView;

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
	
	private StoredImagesAdapter storedImagesAdapter;

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
        ActionBar actionBar = getActionBar();
        if(actionBar != null) {
            if(editingMode == EditMode.EDITING) {
                actionBar.setTitle(getString(R.string.edit_album_title));
            } else if(editingMode == EditMode.ADDING) {
                actionBar.setTitle(getString(R.string.add_album_title));
            }
        }

        try {
		    storedImagesAdapter = new StoredImagesAdapter(EditAlbumActivity.this);
        } catch (StoredImagesAdapter.PuppyFrameImageLoadingException e) {
            showProblemLoadingDialog();
        }

		GridView gridView = (GridView) findViewById(R.id.pictures_grid);
		gridView.setAdapter(storedImagesAdapter);
		gridView.setFastScrollEnabled(true);
	}

    private void showProblemLoadingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Problem Loading Images");
        builder.setMessage("There was a problem loading the images on your device.");
        builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
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
				//TODO check for length
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
