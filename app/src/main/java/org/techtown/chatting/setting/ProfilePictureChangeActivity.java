package org.techtown.chatting.setting;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.pedro.library.AutoPermissions;
import com.pedro.library.AutoPermissionsListener;

import org.techtown.chatting.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ProfilePictureChangeActivity extends AppCompatActivity implements AutoPermissionsListener {
    ImageView imageView;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference ref = storage.getReference().child("profile_picture");
    Uri file;
    Bitmap image;
    Boolean pictureSelected = false;
    Boolean alreadyUploaded = false;
    String file_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_picture_change);

        AutoPermissions.Companion.loadAllPermissions(this,102);


        imageView = findViewById(R.id.imageView);
        file_name = "profile_picture_"+restoreState();

        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent,101);
            }
        });

        Button button2 = findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pictureSelected == true && alreadyUploaded == false){

                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.JPEG, 10, bytes);
                    String path = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), image, "temp", null);
                    StorageReference ref2 = ref.child(file_name);
                    ref2.putFile(Uri.parse(path));
                } else{
                    Toast.makeText(getApplicationContext(),"이미지를 선택하지 않았거나 이미 업로드한 사진입니다.",Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 101){
            alreadyUploaded = false;
            Log.d("asd","1");
            file = data.getData();
            Log.d("asd",file.getPath());
            // ExifInterface 생성자 오류 해결 --> https://guitaryc.tistory.com/16
            String[] filePath = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(file, filePath, null, null, null);
            cursor.moveToFirst();
            String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));
            cursor.close();
            ExifInterface exif = null;
            try{
                exif = new ExifInterface(imagePath);
            } catch(IOException e){
                e.printStackTrace();
            }

            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,1);
            image = BitmapFactory.decodeFile(imagePath);
            image = rotate(image, exifOrientationToDegrees(orientation));
            imageView.setImageBitmap(image);
            pictureSelected = true;
        }
    }

    public int exifOrientationToDegrees(int exifOrientation)
    {
        Toast.makeText(getApplicationContext(),Integer.toString(exifOrientation),Toast.LENGTH_LONG).show();
        if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_90)
        {
            return 90;
        }
        else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_180)
        {
            return 180;
        }
        else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_270)
        {
            return 270;
        }
        return 0;
    }

    public Bitmap rotate(Bitmap bitmap, int degrees)
    {
        if(degrees != 0 && bitmap != null)
        {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) bitmap.getWidth() / 2,
                    (float) bitmap.getHeight() / 2);

            try
            {
                Bitmap converted = Bitmap.createBitmap(bitmap, 0, 0,
                        bitmap.getWidth(), bitmap.getHeight(), m, true);
                if(bitmap != converted)
                {
                    bitmap.recycle();
                    bitmap = converted;
                }
            }
            catch(OutOfMemoryError ex)
            {
                // 메모리가 부족하여 회전을 시키지 못할 경우 그냥 원본을 반환합니다.
            }
        }
        return bitmap;
    }

    protected String restoreState(){
        SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        return pref.getString("id","");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        AutoPermissions.Companion.parsePermissions(this,requestCode,permissions,this);
    }
    @Override
    public void onDenied(int i, String[] strings) {
    }

    @Override
    public void onGranted(int i, String[] strings) {

    }
}
