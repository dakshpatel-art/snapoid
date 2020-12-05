package com.example.snapoid;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.example.snapoid.Interface.AddTextFragmentListener;
import com.example.snapoid.Interface.BrushFragmentListener;
import com.example.snapoid.Interface.EditImageFragmentListener;
import com.example.snapoid.Interface.FilterListFragmentListener;
import com.example.snapoid.Utils.BitmapUtils;
import com.google.android.material.snackbar.Snackbar;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.yalantis.ucrop.UCrop;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.ContrastSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.SaturationSubfilter;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import ja.burhanrashid52.photoeditor.OnSaveBitmap;
import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;

public class MainActivity extends AppCompatActivity implements FilterListFragmentListener, EditImageFragmentListener, BrushFragmentListener, AddTextFragmentListener {

    public static final String pictureName = "banner-student.jpg";
    public static final int PERMISSION_PICK_IMAGE = 1000;
    public static final int PERMISSION_INSERT_IMAGE = 1001;
    private static final int CAMERA_REQUEST = 1002 ;

    SeekBar seekBar;
    TextView textView;

    private CircleImageView ProfileImage;
    private static final int PICK_IMAGE=1;


    PhotoEditorView photoEditorView;
PhotoEditor photoEditor;

  CoordinatorLayout coordinatorLayout;

  Bitmap originalBitmap,filteredBitmap,finalBitmap;

 FiltersListFragment filtersListFragment;
  EditImageFragement editImageFragement;

  CardView btn_filters_list,btn_edit,btn_brush,btn_add_image,btn_crop;

  int brightnessFinal=21;
  float saturationFinal=1.0f;
  float constrantFinal = 1.0f;

  Uri imageuri;
  Uri image_selected_uri;

  static {
      System.loadLibrary("NativeImageProcessor");
  }

    public MainActivity() {
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ProfileImage = (CircleImageView) findViewById(R.id.Profile_Image);
        TextView textView = findViewById(R.id.asConfigured);
        ProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent gallery = new Intent();
                gallery.setType("image/*");
                gallery.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(gallery,"Sellect Picture"),PICK_IMAGE);
            }
        });

        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Snapoid Filter");

        


        //View
        photoEditorView = findViewById(R.id.image_preview);
        photoEditor = new PhotoEditor.Builder(this,photoEditorView)
                .setPinchTextScalable(true)
                .build();
        coordinatorLayout = findViewById(R.id.coordinator);

        btn_edit =(CardView) findViewById(R.id.btn_edit);
        btn_filters_list =(CardView)  findViewById(R.id.btn_filters_list);
        btn_brush =(CardView)  findViewById(R.id.btn_brush);
        btn_add_image =(CardView)  findViewById(R.id.btn_add_image);
        btn_crop =(CardView)  findViewById(R.id.btn_crop);

        btn_crop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCrop(image_selected_uri);
            }
        });

        btn_filters_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                  if (filtersListFragment != null)
                  {

                      filtersListFragment.show(getSupportFragmentManager(),filtersListFragment.getTag());
                  }
                    else
                  {
                      FiltersListFragment filtersListFragment = FiltersListFragment.getInstance(null);
                      filtersListFragment.setListener(MainActivity.this);
                      filtersListFragment.show(getSupportFragmentManager(),filtersListFragment.getTag());
                  }
            }
        });

        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditImageFragement editImageFragement = EditImageFragement.getInstance();
                editImageFragement.setListener(MainActivity.this);
                editImageFragement.show(getSupportFragmentManager(),editImageFragement.getTag());
            }
        });

        btn_brush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //Enable Brush Mode

                photoEditor.setBrushDrawingMode(true);
                BrushFragment brushFragment = BrushFragment.getInstance();
                brushFragment.setListener(MainActivity.this);
                brushFragment.show(getSupportFragmentManager(),brushFragment.getTag());
            }
        });



        btn_add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addImageToPicture();
            }
        });

        loadImage();




    }



    private void startCrop(Uri uri) {

      String destinationFileName = new StringBuilder(UUID.randomUUID().toString()).append(".jpg").toString();
        UCrop ucrop = UCrop.of(uri,Uri.fromFile(new File(getCacheDir(),destinationFileName)));

        ucrop.start(MainActivity.this);

    }

    private void addImageToPicture() {
      Dexter.withActivity(this)
              .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                      Manifest.permission.WRITE_EXTERNAL_STORAGE)
              .withListener(new MultiplePermissionsListener() {
                  @Override
                  public void onPermissionsChecked(MultiplePermissionsReport report) {
                      if (report.areAllPermissionsGranted())
                      {
                          Intent intent = new Intent(Intent.ACTION_PICK);
                          intent.setType("image/*");
                          startActivityForResult(intent,PERMISSION_INSERT_IMAGE);
                      }
                  }

                  @Override
                  public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                      Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                  }
              }).check();
    }

    private void loadImage() {

      originalBitmap = BitmapUtils.getBitmapFromAssets(this,pictureName,300,300);
        assert originalBitmap != null;
        filteredBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888,true);
      finalBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888,true);
      photoEditorView.getSource().setImageBitmap(originalBitmap);

    }

    @Override
    public void onBrightnessChanged(int brightness) {

      brightnessFinal = brightness;
      Filter myFilter = new Filter();
      myFilter.addSubFilter(new BrightnessSubFilter(brightness));
        photoEditorView.getSource().setImageBitmap(myFilter.processFilter(finalBitmap.copy(Bitmap.Config.ARGB_8888,true)));

    }

    @Override
    public void onSaturationChanged(float saturation) {

        saturationFinal = saturation;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new SaturationSubfilter(saturation));
        photoEditorView.getSource().setImageBitmap(myFilter.processFilter(finalBitmap.copy(Bitmap.Config.ARGB_8888,true)));

    }

    @Override
    public void onConstrantChanged(float constrant) {

        constrantFinal = constrant;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new ContrastSubFilter(constrant));
        photoEditorView.getSource().setImageBitmap(myFilter.processFilter(finalBitmap.copy(Bitmap.Config.ARGB_8888,true)));
    }

    @Override
    public void onEditStarted() {

    }

    @Override
    public void onEditCompleted() {

      Bitmap bitmap = filteredBitmap.copy(Bitmap.Config.ARGB_8888,true);

      Filter myFilter = new Filter();
        myFilter.addSubFilter(new BrightnessSubFilter(brightnessFinal));
        myFilter.addSubFilter(new SaturationSubfilter(saturationFinal));
        myFilter.addSubFilter(new ContrastSubFilter(constrantFinal));

        finalBitmap = myFilter.processFilter(bitmap);

    }

    @Override
    public void onFilterSelected(Filter filter) {

    //  resetcontrol();
      filteredBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888,true);
        photoEditorView.getSource().setImageBitmap(filter.processFilter(filteredBitmap));
      finalBitmap = filteredBitmap.copy(Bitmap.Config.ARGB_8888,true);
    }

    private void resetcontrol() {
        
        if (editImageFragement != null)
            editImageFragement.resetControls();
        brightnessFinal=0;
        saturationFinal=1.0f;
        constrantFinal=1.0f;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_open)
        {
            openImageFromGalllery();
            return true;
        }
      else if (id == R.id.action_save)
        {
            saveImageToGalllery();
            return true;
        }
        else if (id == R.id.action_camera)
        {
            openCamera();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    private void openCamera() {

        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @SuppressLint("ShowToast")
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {

                            ContentValues values = new ContentValues();
                            values.put(MediaStore.Images.Media.TITLE,"New Picture");
                            values.put(MediaStore.Images.Media.DESCRIPTION,"From Camera");
                            image_selected_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
                            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,image_selected_uri);
                            startActivityForResult(cameraIntent,CAMERA_REQUEST);
                        }
                        else
                        {
                            Toast.makeText(MainActivity.this,"Permission Denied",Toast.LENGTH_LONG);
                        }
                    }


                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                        token.continuePermissionRequest();
                    }
                })
                .check();
    }

    private void saveImageToGalllery() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @SuppressLint("ShowToast")
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {

                            photoEditor.saveAsBitmap(new OnSaveBitmap() {
                                @Override
                                public void onBitmapReady(Bitmap saveBitmap) {
                                    try {
                                        photoEditorView.getSource().setImageBitmap(saveBitmap);

                                        final String path = BitmapUtils.insertImage(getContentResolver(),
                                                saveBitmap,
                                                System.currentTimeMillis()+"_profile.jpg"
                                                ,null);

                                        if (!TextUtils.isEmpty(path))
                                        {
                                            Snackbar snackbar = Snackbar.make(coordinatorLayout,
                                                    "Image saved to gallery!",
                                                    Snackbar.LENGTH_LONG);

                                            snackbar.show();

                                        }
                                        else
                                        {
                                            Snackbar snackbar = Snackbar.make(coordinatorLayout,
                                                    "Unable to save image !",
                                                    Snackbar.LENGTH_LONG);
                                            snackbar.show();
                                        }
                                    } catch (IOException e){
                                        e.printStackTrace();
                                    }
                                }


                                @Override
                                public void onFailure(Exception e) {

                                }
                            });
                        }
                        else
                        {
                            Toast.makeText(MainActivity.this,"Permission Denied",Toast.LENGTH_LONG);
                        }
                    }


                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                        token.continuePermissionRequest();
                    }
                })
        .check();


    }



        public void openImage(String path) {
        Intent  intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(path),"image/*");
        startActivity(intent);
    }

    private void openImageFromGalllery() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {

                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        if (report.areAllPermissionsGranted()) {
                            Intent intent = new Intent(Intent.ACTION_PICK);
                            intent.setType("Image/*");
                            startActivityForResult(intent, PERMISSION_PICK_IMAGE);
                        }
                        else {
                            Toast.makeText(MainActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
                        }


                    }


                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                            token.continuePermissionRequest();
                    }
                });




    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

            if ( requestCode == RESULT_OK && requestCode == PICK_IMAGE ) {
               // Bitmap bitmap = BitmapUtils.getBitmapFromGallery(this, data.getData(), 800, 800);

                imageuri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imageuri);
                    ProfileImage.setImageBitmap(bitmap);
                }catch (IOException e){
                    e.printStackTrace();
                }
                originalBitmap.recycle();
                finalBitmap.recycle();
                filteredBitmap.recycle();

              //  originalBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                finalBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
                filteredBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);

                photoEditorView.getSource().setImageBitmap(originalBitmap);
               // bitmap.recycle();

                //Render Selected img thumbnai
                filtersListFragment = FiltersListFragment.getInstance(originalBitmap);
                filtersListFragment.setListener(this);
            }
            if ( requestCode == CAMERA_REQUEST) {
                Bitmap bitmap = BitmapUtils.getBitmapFromGallery(this,image_selected_uri, 800, 800);


                originalBitmap.recycle();
                finalBitmap.recycle();
                filteredBitmap.recycle();

                originalBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                finalBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
                filteredBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
                photoEditorView.getSource().setImageBitmap(originalBitmap);
                bitmap.recycle();

                //Render Selected img thumbnai
                filtersListFragment = FiltersListFragment.getInstance(originalBitmap);
                filtersListFragment.setListener(this);

            }
            else if (requestCode == PERMISSION_INSERT_IMAGE)
            {
                Bitmap bitmap = BitmapUtils.getBitmapFromGallery(this,data.getData(),250,250);
                photoEditor.addImage(bitmap);
            }
            else if (requestCode == UCrop.REQUEST_CROP)
                handleCropResult(data);


        else if (requestCode == UCrop.RESULT_ERROR)
            handleCropError(data);
        
    }


    private void handleCropError(Intent data) {
      final Throwable cropError = UCrop.getError(data);
      if (cropError != null)
      {
          Toast.makeText(this, ""+cropError.getMessage(), Toast.LENGTH_SHORT).show();
      }
      else {
          Toast.makeText(this, "Unexcepted Error", Toast.LENGTH_SHORT).show();
      }
    }

    private void handleCropResult(Intent data) {
      final Uri reslutUri = UCrop.getOutput(data);
      if (reslutUri!=null)
      {
          photoEditorView.getSource().setImageURI(reslutUri);

          Bitmap bitmap = ((BitmapDrawable)photoEditorView.getSource().getDrawable()).getBitmap();
          originalBitmap = bitmap.copy(Bitmap.Config.ARGB_8888,true);
          filteredBitmap = originalBitmap;
          finalBitmap = originalBitmap;
      }
      else
          Toast.makeText(this, "Cannot retrieve crop image", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onBrushSizeChangedListener(float size) {
        photoEditor.setBrushSize(size);
    }

    @Override
    public void OnBrushOpacityChangedListener(int opacity) {
        photoEditor.setOpacity(opacity);
    }

    @Override
    public void OnBrushColorChangedListener(int color) {
        photoEditor.setBrushColor(color);
    }

    @Override
    public void onBrushStateChangedListener(boolean isEraser) {
        if (isEraser)
            photoEditor.brushEraser();
        else
            photoEditor.setBrushDrawingMode(true);
    }

    @Override
    public void onBrushColorChangedListener(int color) {
        photoEditor.setBrushColor(color);
    }

    @Override
    public void onBrushOpacityChangedListener(int progress) {

    }

    @Override
    public void onAddTextButtonClick(String text, int color) {
        photoEditor.addText(text,color);
    }
}

