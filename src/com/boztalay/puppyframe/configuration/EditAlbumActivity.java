package com.boztalay.puppyframe.configuration;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;
import com.boztalay.puppyframe.R;
import com.boztalay.puppyframe.persistence.Album;
import com.boztalay.puppyframe.persistence.PuppyFramePersistenceManager;

public class EditAlbumActivity extends Activity implements AdapterView.OnItemClickListener {
	public static final String ALBUM_ID_KEY = "albumId";

    public enum EditMode {
		EDITING, ADDING
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
		    storedImagesAdapter = new StoredImagesAdapter(EditAlbumActivity.this, album);
        } catch (StoredImagesAdapter.PuppyFrameImageLoadingException e) {
            showProblemLoadingDialog();
        }

		GridView gridView = (GridView) findViewById(R.id.pictures_grid);
		gridView.setAdapter(storedImagesAdapter);
		gridView.setFastScrollEnabled(true);
        gridView.setOnItemClickListener(this);
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
        getMenuInflater().inflate(R.menu.edit_menu, menu);
		return true;
	}

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String pathOfImageClicked = (String) parent.getAdapter().getItem(position);
        if(album.getImagePaths().contains(pathOfImageClicked)) {
            album.removeImagePath(pathOfImageClicked);
            ((SelectableImageView)view).setChecked(false);
        } else {
            album.addImagePath(pathOfImageClicked);
            ((SelectableImageView)view).setChecked(true);
        }
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case R.id.done_editing_menu_action:
                if(album.getImagePaths().size() == 0) {
                    Toast.makeText(EditAlbumActivity.this, "Please add at least one picture to this album", Toast.LENGTH_LONG).show();
                } else {
				    createNamingDialog();
                }
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void createNamingDialog() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Name Your Album");

		final EditText input = new EditText(this);
        input.setText(album.getTitle());
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
                Editable editable = input.getText();
				if(editable == null || editable.length() == 0) {
                    Toast.makeText(EditAlbumActivity.this, "You gotta name it something!", Toast.LENGTH_LONG).show();
                    return;
                }

				album.setTitle(editable.toString());
                album.setThumbnailPath(album.getImagePaths().get(0));
				persistenceManager.saveAlbum(album);
                persistenceManager.setCurrentAlbum(album);

				setResult(RESULT_OK);
				finish();
			}
		});
		alert.setNegativeButton("Cancel", null);

		alert.show();
	}

    @Override
    public void onBackPressed() {
        if(editingMode == EditMode.EDITING) {
            album.setThumbnailPath(album.getImagePaths().get(0));
            persistenceManager.saveAlbum(album);
            persistenceManager.setCurrentAlbum(album);

            setResult(RESULT_OK);
            finish();
        }
    }
}
