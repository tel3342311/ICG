package com.liteon.icampusguardian;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import com.liteon.icampusguardian.util.CustomDialog;
import com.liteon.icampusguardian.util.PhotoItem;
import com.liteon.icampusguardian.util.PhotoItemAdapter;
import com.liteon.icampusguardian.util.PhotoItemAdapter.ViewHolder.IPhotoViewHolderClicks;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

public class ChoosePhotoActivity extends AppCompatActivity implements IPhotoViewHolderClicks {

	private final static String TAG = ChoosePhotoActivity.class.getName();
	private ImageView mCancel;
	private ImageView mUsedPhoto;
	private RecyclerView mRecyclerView;
	private RecyclerView.Adapter mAdapter;
	private RecyclerView.LayoutManager mLayoutManager;	
	private ArrayList<PhotoItem> mDataSet;
	private final static int CROP_PIC_REQUEST_CODE = 1;
    private final static int PERMISSION_REQUEST = 3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_photo);
		findViews();
		setListener();
		initRecycleView();
		askPermisson();
		
	}
	
	private void initRecycleView() {
		// set a GridLayoutManager with default vertical orientation and 3 number of columns
		mLayoutManager = new GridLayoutManager(getApplicationContext(),3);
		mRecyclerView.setLayoutManager(mLayoutManager);
		mDataSet = getPhotoItem();
		mAdapter = new PhotoItemAdapter(mDataSet, this);
		mRecyclerView.setAdapter(mAdapter);
	}
	
	private void findViews() {
		mCancel = (ImageView) findViewById(R.id.cancel);
		mRecyclerView = (RecyclerView) findViewById(R.id.photo_list);
		
	}
	
	private void setListener() {

	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	private void askPermisson() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (this.checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
				requestPermissions(new String[] {android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST);
			}
		}
	}
	
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		switch(requestCode) {
		case PERMISSION_REQUEST:
			if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				Log.d(TAG, "read storage permission granted");
			} else {
				CustomDialog dialog = new CustomDialog();
        		dialog.setTitle("應用程式要求權限以繼續");
        		dialog.setIcon(0);
        		dialog.setBtnText("好");
        		dialog.show(getSupportFragmentManager(), "dialog_fragment");
			}
			
			if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
				Log.d(TAG, "write storage permission granted");
			} else {
				CustomDialog dialog = new CustomDialog();
        		dialog.setTitle("應用程式要求權限以繼續");
        		dialog.setIcon(0);
        		dialog.setBtnText("好");
        		dialog.show(getSupportFragmentManager(), "dialog_fragment");
			}
			break;
		}
	}
	
	public ArrayList<PhotoItem> getPhotoItem() {
		ArrayList<PhotoItem> list = new ArrayList<>();
        int int_position = 0;
        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name;

        String absolutePathOfImage = null;
        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
        cursor = getApplicationContext().getContentResolver().query(uri, projection, null, null, orderBy + " DESC");

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
        if (cursor != null && cursor.moveToFirst()) {
	        while (cursor.moveToNext()) {
	            absolutePathOfImage = cursor.getString(column_index_data);
	            Log.e("Column", absolutePathOfImage);
	            Log.e("Folder", cursor.getString(column_index_folder_name));
	            PhotoItem item = new PhotoItem();
	            item.setUri(absolutePathOfImage);
	            list.add(item);
	        }
        }
        return list;
    }

	@Override
	public void onPhotoClick(int position) {
		// TODO Auto-generated method stub
		
	}
	
	private void doCrop(Uri picUri) {
	    try {

	        Intent cropIntent = new Intent("com.android.camera.action.CROP");

	        cropIntent.setDataAndType(picUri, "image/*");           
	        cropIntent.putExtra("crop", "true");           
	        cropIntent.putExtra("aspectX", 1);
	        cropIntent.putExtra("aspectY", 1);           
	        cropIntent.putExtra("outputX", 128);
	        cropIntent.putExtra("outputY", 128);           
	        cropIntent.putExtra("return-data", true);
	        startActivityForResult(cropIntent, CROP_PIC_REQUEST_CODE);
	    }
	    // respond to users whose devices do not support the crop action
	    catch (ActivityNotFoundException anfe) {
	        // display an error message
	        String errorMessage = "Whoops - your device doesn't support the crop action!";
	        Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
	        toast.show();
	    }
	}
	
	public Uri getImageUri(Context inContext, Bitmap inImage) {
		  ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		  inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
		  String path = Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
		  return Uri.parse(path);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);

	    if (requestCode == CROP_PIC_REQUEST_CODE) {
	        if (data != null) {
	            Bundle extras = data.getExtras();
	            Bitmap bitmap= extras.getParcelable("data");
	            //yourImageView.setImageBitmap(bitmap);
	        }
	    }

	}
}
