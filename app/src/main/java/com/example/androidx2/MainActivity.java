package com.example.androidx2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
//    private static final String FILE_NAME = "example.txt";
    EditText mResultEt;
    ImageView mPreviewIv;
    Button mSaveBtn;
    Button find;
    EditText mText1;
    String mText;
    String Disease;
//    StringBuffer filePath;
//    File file;
//    CSVFileWriter csv;

    private static final int CAMERA_REQUEST_CODE=200;
    private static final int STORAGE_REQUEST_CODE=400;
    private static final int IMAGE_PICK_GALLERY_CODE=1000;
    private static final int IMAGE_PICK_CAMERA_CODE=1001;
    private static final int WRITE_EXTERNAL_STORAGE_CODE = 1;

    String[] cameraPermission;
    String[] storagePermission;

    Uri image_uri;
//    private StringBuffer FilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setSubtitle("Click + Button To Insert Images");
        mResultEt = findViewById(R.id.resultEt);
        mPreviewIv = findViewById(R.id.imageIv);
        mSaveBtn = findViewById(R.id.saveBtn);
        find = findViewById(R.id.find_diseases);
        mText1 = findViewById(R.id.mText);

        //camera permission
        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        //storage permission
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File path = new File(getExternalFilesDir(null).getAbsolutePath());
                File dir = new File(path + "/Raj/" + "Disease_"+".txt");
                try {
                    FileReader fw1 = new FileReader(dir);
                    BufferedReader br =new BufferedReader(fw1);
                    String line=br.readLine();
                    Disease=" ";
                    while (line!=null){
                        Disease = Disease + "\n"+ line;
                        line=br.readLine();
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                StringBuilder sb =new StringBuilder();
                sb.append(Disease);
                mText1.setText(sb.toString());

            }
        });


        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mText = mResultEt.getText().toString().trim();
                if (mText.isEmpty()) {
                    Toast.makeText(MainActivity.this, "File contain nothing", Toast.LENGTH_SHORT).show();
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {
                            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                            requestPermissions(permissions, WRITE_EXTERNAL_STORAGE_CODE);
                        } else {
                            saveToTextFile(mText);
                        }
                    } else {
                        saveToTextFile(mText);
                    }

                }

            }
        });
    }
//
//        find.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });

    private void saveToTextFile(String mText) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(System.currentTimeMillis());
//
        try{
            File path = new File(getExternalFilesDir(null).getAbsolutePath());
            File dir = new File(path + "/Raj/");
            dir.mkdirs();
            String fileName = "Raj_raw" + timeStamp + ".csv"; //npr Račun_20200810_153433.txt

            File file = new File(dir, fileName);

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(mText);
            bw.close();
            Toast.makeText(this, fileName+" is saved to\n" + dir, Toast.LENGTH_SHORT).show();

            String fileName1 = "Raj_" + timeStamp + ".csv"; //npr Račun_20200810_153433.txt
            File file1 = new File(dir, fileName1);
            FileWriter fw1 = new FileWriter(file1.getAbsoluteFile());
            BufferedWriter bw1 = new BufferedWriter(fw1);
            FileReader fr = new FileReader(file.getAbsoluteFile());
            BufferedReader br = new BufferedReader(fr);
            String temp=br.readLine();
            while (temp!=null){
                if("Haemoglobin".equals(temp)){
                    bw1.write(temp);
                    bw1.write(",");
                    String sr =br.readLine();
                    for (int i = 0;i<4;i++){
                        char s=sr.charAt(i);
                        bw1.write(s);
                    }
                    bw1.write("\n");
                }

                if("WBC Count".equals(temp)){
                    bw1.write(temp);
                    bw1.write(",");
                    String sr =br.readLine();
                    for (int i = 0;i<5;i++){
                        char s=sr.charAt(i);
                        bw1.write(s);
                    }
                    bw1.write("\n");
                }

                if("RBC Count".equals(temp)){
                    bw1.write(temp);
                    bw1.write(",");
                    String sr =br.readLine();
                    for (int i = 0;i<4;i++){
                        char s=sr.charAt(i);
                        bw1.write(s);
                    }
                    bw1.write("\n");
                }

                if("Polymorphs".equals(temp)){
                    bw1.write(temp);
                    bw1.write(",");
                    String sr =br.readLine();
                    for (int i = 0;i<2;i++){
                        char s=sr.charAt(i);
                        bw1.write(s);
                    }
                    bw1.write("\n");
                }

                if("Lymphocytes".equals(temp)){
                    bw1.write(temp);
                    bw1.write(",");
                    String sr =br.readLine();
                    for (int i = 0;i<2;i++){
                        char s=sr.charAt(i);
                        bw1.write(s);
                    }
                    bw1.write("\n");
                }

                if("Eosinophils".equals(temp)){
                    bw1.write(temp);
                    bw1.write(",");
                    String sr =br.readLine();
                    for (int i = 0;i<2;i++){
                        char s=sr.charAt(i);
                        bw1.write(s);
                    }
                    bw1.write("\n");
                }

                if("Monocytes".equals(temp)){
                    bw1.write(temp);
                    bw1.write(",");
                    String sr =br.readLine();
                    for (int i = 0;i<2;i++){
                        char s=sr.charAt(i);
                        bw1.write(s);
                    }
                    bw1.write("\n");
                }

                if("Basophils".equals(temp)){
                    bw1.write(temp);
                    bw1.write(",");
                    String sr =br.readLine();
                    for (int i = 0;i<2;i++){
                        char s=sr.charAt(i);
                        bw1.write(s);
                    }
                    bw1.write("\n");
                }

                if("MCV".equals(temp)){
                    bw1.write(temp);
                    bw1.write(",");
                    String sr =br.readLine();
                    for (int i = 0;i<3;i++){
                        char s=sr.charAt(i);
                        bw1.write(s);
                    }
                    bw1.write("\n");
                }

                if("MCH".equals(temp)){
                    bw1.write(temp);
                    bw1.write(",");
                    String sr =br.readLine();
                    for (int i = 0;i<3;i++){
                        char s=sr.charAt(i);
                        bw1.write(s);
                    }
                    bw1.write("\n");
                }

                if("MCHC".equals(temp)){
                    bw1.write(temp);
                    bw1.write(",");
                    String sr =br.readLine();
                    for (int i = 0;i<3;i++){
                        char s=sr.charAt(i);
                        bw1.write(s);
                    }
                    bw1.write("\n");
                }

                if("RDW".equals(temp)){
                    bw1.write(temp);
                    bw1.write(",");
                    String sr =br.readLine();
                    for (int i = 0;i<3;i++){
                        char s=sr.charAt(i);
                        bw1.write(s);
                    }
                    bw1.write("\n");
                }

                if("MPV".equals(temp)){
                    bw1.write(temp);
                    bw1.write(",");
                    String sr =br.readLine();
                    for (int i = 0;i<3;i++){
                        char s=sr.charAt(i);
                        bw1.write(s);
                    }
                    bw1.write("\n");
                }

                if("P.D.W.".equals(temp)){
                    bw1.write(temp);
                    bw1.write(",");
                    String sr =br.readLine();
                    for (int i = 0;i<3;i++){
                        char s=sr.charAt(i);
                        bw1.write(s);
                    }
                    bw1.write("\n");
                }

                if("H.C.T.".equals(temp)){
                    bw1.write(temp);
                    bw1.write(",");
                    String sr =br.readLine();
                    for (int i = 0;i<3;i++){
                        char s=sr.charAt(i);
                        bw1.write(s);
                    }
                    bw1.write("\n");
                }

                if("Platelet Count".equals(temp)){
                    String st=br.readLine();
                    bw1.write(temp);
                    bw1.write(",");
                    String sr =br.readLine();
                    for (int i = 0;i<6;i++){
                        char s=sr.charAt(i);
                        bw1.write(s);
                    }
                    bw1.write("\n");
                }
                temp=br.readLine();

            }
            bw1.close();
            br.close();


            String splitBy = ",";
            String fileName2 = "Disease_"+".txt";
            File file2 = new File(dir, fileName2);
            FileWriter fw2 = new FileWriter(file2.getAbsoluteFile());
            BufferedWriter bw2 = new BufferedWriter(fw2);
            FileReader fr2 = new FileReader(file1.getAbsoluteFile());
            BufferedReader br2 = new BufferedReader(fr2);
            String temp1=br2.readLine();
            while (temp1 != null)   //returns a Boolean value
            {
                String[] data = temp1.split(splitBy);// use comma as separator
                float f1=Float.parseFloat(data[1]);

                //Haemoglobin
                if(("Haemoglobin".equals(data[0])) && (f1<12.0)){
                    bw2.write("Due to Low Haemoglobin: "+"\n"+"=Anemia \n");
                }else if(("Haemoglobin".equals(data[0])) && (f1>16.0)){
                    bw2.write("Due to High Haemoglobin: "+"\n"+"=Polycythemia \n");
                }

                //RBC Count
                if(("RBC Count".equals(data[0])) && (f1<4.50)){
                    bw2.write("Due to Low RBC Count: "+"\n"+"=Blood lose Anemia \n= Thalassemia");
                }else if(("RBC Count".equals(data[0])) && (f1>6.20)){
                    bw2.write("Due to High RBC Count: "+"\n"+"=Dihidretion \n=Polysaitemia\n");
                }

                //WBC Count
                if(("WBC Count".equals(data[0])) && (f1<4000 || f1>10000)){
                    bw2.write("Due to WBC Count: "+"\n"+"=infection \n = leukemia");
                }

                //MCV
                if(("MCV".equals(data[0])) && (f1<82.00)){
                    bw2.write("Due to Low MCV Count: "+"\n"+"=Iron defeciency Anemia\n");
                }else if(("MCV".equals(data[0])) && (f1>102.00)){
                    bw2.write("Due to High MCV Count: "+"\n"+"=Megaloblasticanemia \n");
                }

                //MCH
                if(("MCH Count".equals(data[0])) && (f1<27.00)){
                    bw2.write("Due to Low MCH Count: "+"\n"+"=Anemia"+"\n"+"\n");
                }else if(("MCH Count".equals(data[0])) && (f1>31.00)){
                    bw2.write("Due to High MCH Count: "+"\n"+"=Megaloblasticanemia\n"+"\n"+"\n");
                }

                //MCHC
                if(("MCHC Count".equals(data[0])) && (f1<32.00)){
                    bw2.write("Due to Low MCHC Count: "+"\n"+"=Anemia"+"\n"+"\n");
                }
                //PLATELETS
                if(("Platelet Count".equals(data[0])) && (f1<150000.00)){
                    bw2.write("Due to Low Platelet Count: "+"\n"+"=Bleeding disorder"+"\n"+"\n");
                }else if(("Platelet Count".equals(data[0])) && (f1>500000.00)){
                    bw2.write("Due to High Platelet Count: "+"\n"+"=clotting Disorder"+"\n"+"\n");
                }

                //Basophils
                if(("Basophils".equals(data[0])) && (f1<00.00)){
                    bw2.write("Due to Low Basophils Count: "+"\n"+"=basopenia"+"\n"+"\n");
                }else if(("Basophils".equals(data[0])) && (f1>01.00)){
                    bw2.write("Due to High Basophils Count: "+"\n"+"=hypothyroidism \n"+"\n"+"\n");
                }

                //Monocytes
                if(("Monocytes".equals(data[0])) && (f1<02.00)){
                    bw2.write("Due to Low Monocytes Count: "+"\n"+"=monocytopenia \n =bloodstream infection"+"\n"+"\n");
                }else if(("Monocytes".equals(data[0])) && (f1>06.00)){
                    bw2.write("Due to High Monocytes Count: "+"\n"+"=Chronic inflammatory disease\n=Leukemia\n=Tuberculosis\n=Viral infection"+"\n"+"\n");
                }

                //Eosinophils
                if(("Eosinophils".equals(data[0])) && (f1<01.00)){
                    bw2.write("Due to Low Eosinophils Count: "+"\n"+"=Bloodstream infections (sepsis)\n"+"\n"+"\n");
                }else if(("Eosinophils".equals(data[0])) && (f1>04.00)){
                    bw2.write("Due to High Eosinophils Count: "+"\n"+"=allegy\n"+"\n"+"\n");
                }


                //Lymphocytes
                if(("Lymphocytes".equals(data[0])) && (f1<20.00)){
                    bw2.write("Due to Low Lymphocytes Count: "+"\n"+"=lymphocytopenia or lymphopenia\n"+"\n"+"\n");
                }else if(("Lymphocytes".equals(data[0])) && (f1>30.00)){
                    bw2.write("Due to High Lymphocytes Count: "+"\n"+"=Viral Infection \n=Cancer of the blood\n=lymphatic system"+"\n"+"\n");
                }

                //neutrophils
                if(("neutrophils".equals(data[0])) && (f1<35.00)){
                    bw2.write("Due to Low Polymorphs Count: "+"\n"+"=lymphocytopenia or lymphopenia\n"+"\n"+"\n");
                }else if(("neutrophils".equals(data[0])) && (f1>75.00)){
                    bw2.write("Due to High Polymorphs Count: "+"\n"+"=bacterial infection\n=allergic reaction\n"+"\n"+"\n");
                }


                //Polymorphs
                if(("Polymorphs".equals(data[0])) && (f1<60.00)){
                    bw2.write("Due to Low Polymorphs Count: "+"\n"+"=lymphocytopenia or lymphopenia\n"+"\n"+"\n");
                }else if(("Polymorphs".equals(data[0])) && (f1>70.00)){
                    bw2.write("Due to High Polymorphs Count: "+"\n"+"=bacterial infection\n=allergic reaction\n"+"\n"+"\n");
                }

                //RDW
                if(("RDW".equals(data[0])) && (f1<10.00)){
                    bw2.write("Due to Low RDW Count: "+"\n"+"=thalassemia\n=sicklecellanemia\n"+"\n"+"\n");
                }

                //MPV
                if(("MPV".equals(data[0])) && (f1<00.10)){
                    bw2.write("Due to Low MPV Count: "+"\n"+"=low bleeding\n"+"\n"+"\n");
                }

                temp1 = br2.readLine();

            }
            bw2.close();
            br2.close();




            Toast.makeText(this, fileName1+" is saved to\n" + dir, Toast.LENGTH_SHORT).show();
        }
        catch (Exception e) {

            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();

        }
    }


//
//    public void save(View v) {
//        String text = mResultEt.getText().toString();
//        FileOutputStream fos = null;
//        try {
//            fos = openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
//            fos.write(text.getBytes());
//            mResultEt.getText().clear();
//            Toast.makeText(this, "Saved to " + getFilesDir() + "/" + FILE_NAME,
//                    Toast.LENGTH_LONG).show();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//
//        catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (fos != null) {
//                try {
//                    fos.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//


    //action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflate menu
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }


    //handle actionbar item clicks
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.addImage) {
            showImageImportDialog();
        }
        if(id==R.id.settings){
            Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showImageImportDialog() {
        //item to displayin dialog
        String[] items={"Camera","Gallery"};
        AlertDialog.Builder dialog=new AlertDialog.Builder(this);
        //set title
        dialog.setTitle("Select Image");
        dialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which==0)
                {
                    //camera option clicked
                    if(!checkCameraPermission()){
                        //camera permission not allowed,request it
                        requestCameraPermission();
                    }
                    else{
                        //permission allowed,take picture
                        pickCamera();
                    }

                }
                if (which==1)
                {
                    //gallery option clicked
                    if(!checkStoragePermission()){
                        //Storage permission not allowed,request it
                        requestStoragePermission();
                    }
                    else{
                        //permission allowed,take picture
                        pickGallery();
                    }
                }
            }
        });
        dialog.create().show();//show dialog

    }

    private void pickGallery() {
        //intent to pic image from gallery
        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,IMAGE_PICK_GALLERY_CODE);
    }

    private void pickCamera() {
        //intent to take image from camera,it will also be saved to the storage to get high quality image
        ContentValues values= new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"NewPic");//title of image
        values.put(MediaStore.Images.Media.DESCRIPTION,"Image to text");//description
        image_uri=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);

        Intent cameraIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(cameraIntent,IMAGE_PICK_CAMERA_CODE);

    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this,storagePermission,STORAGE_REQUEST_CODE);
    }

    private boolean checkStoragePermission() {
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this,cameraPermission,CAMERA_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {
        boolean result= ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1= ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return  result && result1;
    }


    //Handle permission Request

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case CAMERA_REQUEST_CODE:
                if(grantResults.length>0){
                    boolean cameraAccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted && writeStorageAccepted){
                        pickCamera();
                    }
                    else{
                        Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case STORAGE_REQUEST_CODE:
                if(grantResults.length>0){
                    boolean writeStorageAccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    if(writeStorageAccepted){
                        pickGallery();
                    }
                    else{
                        Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
//            case WRITE_EXTERNAL_STORAGE_CODE: {
//
//                if (grantResults.length > 0 && grantResults[0]
//                        == PackageManager.PERMISSION_GRANTED) {
//                    saveToTextFile(mText);
//                } else {
//                    Toast.makeText(this, "Storage permition required to store data", Toast.LENGTH_SHORT).show();
//                }
//            }
//                break;
        }
    }

    //Handle image result

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                //got image from gallery now crop it
                assert data != null;
                CropImage.activity(data.getData()).setGuidelines(CropImageView.Guidelines.ON)//enable image guide line
                        .start(this);
            }
            if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                //got image from camera now crop it
                CropImage.activity(image_uri).setGuidelines(CropImageView.Guidelines.ON)//enable image guide line
                        .start(this);
            }
        }
        //get cropped images
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                assert result != null;
                Uri resultUri = result.getUri();//gete image uri
                //set image to image view
                mPreviewIv.setImageURI(resultUri);
                //get drawble bitmap for text recognition
                BitmapDrawable bitmapDrawable = (BitmapDrawable) mPreviewIv.getDrawable();
                Bitmap bitmap = bitmapDrawable.getBitmap();
                TextRecognizer recognizer = new TextRecognizer.Builder(getApplicationContext()).build();
                if (!recognizer.isOperational()) {
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                } else {
                    Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                    SparseArray<TextBlock> items = recognizer.detect(frame);
                    StringBuilder sb = new StringBuilder();
                    //get text from sb until there is no text
                    for (int i = 0; i < items.size(); i++) {
                        TextBlock myItem = items.valueAt(i);
                        sb.append(myItem.getValue());
//                        sb.append(",");
//                        if((i%2) == 0){
                        sb.append("\n");
//                        }

                    }
                    //set text to edit text
                    mResultEt.setText(sb.toString());
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                //if there is any error show it
                assert result != null;
                Exception error = result.getError();
                Toast.makeText(this, "" + error, Toast.LENGTH_SHORT).show();

            }
        }
    }
}