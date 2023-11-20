package mobile.resky.x_app.CRUD;

import static android.text.TextUtils.isEmpty;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

import mobile.resky.x_app.R;

public class CrudActivity extends Activity {
    //Yang Dipakai Untuk Latihan uji coba sudah berisi verifikasi email
    RadioGroup rg;
    RadioButton rb;
    //Deklarasi Variabel
    private ProgressBar progressBar;
    private EditText NIM, Nama,tgllahir,nohp,crudemail,ipk,alamat;
    DatePickerDialog datePickerDialog;
    SimpleDateFormat dateFormatter;
    private TextView hasil;
    private Spinner Fakultas,Prodi;
    private CheckBox golA,golB,golAB,golO;
    private ImageView ImageContainer;
    private Button Simpan, ShowData,getfoto;
    private String getNIM, getNama, getProdi,getFakultas,getAlamat,getCrudemail,getGambar;
    private String getTgllahir;
    private String getNohp,getIpk,getRjeniskelamin,getHasilgol,outputgolongan;
    private ArrayList<String> hasilgol;
    public Uri imageUrl,uri;
    public Bitmap bitmap;
    private StorageReference reference;

    DatabaseReference getReference;
    FirebaseStorage storage;
    DatabaseReference database;
    StorageReference storageReference;

    private static final int REQUEST_CODE_CAMERA = 1;
    private static final int  REQUEST_CODE_GALLERY = 2;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crud);

        rg = (RadioGroup) findViewById(R.id.rjeniskelamin);
        rg = findViewById(R.id.rjeniskelamin);


        //inisialisasi ID (ProgressBar)
        progressBar = findViewById(R.id.progress);
        progressBar.setVisibility(View.GONE);

        //inisialisasi ID (Button)
        Simpan = findViewById(R.id.save);
        ShowData = findViewById(R.id.showdata);
        //Inisialisasi Untuk Upload foto
        getfoto = findViewById(R.id.getfoto);
        ImageContainer = findViewById(R.id.imageContainer);

        //inisialisasi ID (EditText)
        NIM = findViewById(R.id.nim);
        Nama = findViewById(R.id.nama);
        Fakultas = findViewById(R.id.fakultas);
        Prodi = findViewById(R.id.prodi);

        nohp = findViewById(R.id.nohp);
        crudemail = findViewById(R.id.crudemail);
        ipk = findViewById(R.id.ipk);
        alamat = findViewById(R.id.alamat);

        //inisialisasi ID (Check button)
        golA = findViewById(R.id.golA);
        golB = findViewById(R.id.golB);
        golAB = findViewById(R.id.golAB);
        golO = findViewById(R.id.golO);
        //checkbox untuk golongan darah
        hasil = findViewById(R.id.hasil);
        hasilgol = new ArrayList<>();
        hasil.setEnabled(false);

        //mengatur Datepicker
        tgllahir = findViewById(R.id.tgllahir);
        dateFormatter = new SimpleDateFormat("dd MMM yyyy");
        tgllahir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialog();
            }
        });


        golA.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    golB.setChecked(false);
                    golAB.setChecked(false);
                    golO.setChecked(false);
                    outputgolongan = "A";
                }
            }
        });
        golB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    golA.setChecked(false);
                    golAB.setChecked(false);
                    golO.setChecked(false);
                    outputgolongan = "B";
                }
            }
        });

        golAB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    golA.setChecked(false);
                    golB.setChecked(false);
                    golO.setChecked(false);
                    outputgolongan = "AB";
                }
            }
        });
        golO.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    golA.setChecked(false);
                    golAB.setChecked(false);
                    golB.setChecked(false);
                    outputgolongan = "O";
                }
            }
        });
        //Mendapatkan Referensi dari Firebase Storage
        reference = FirebaseStorage.getInstance().getReference();

        //mendapatkan Instance dari Database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        getReference = database.getReference(); //mendapatkan Referensi dari Database

        //Membuat Fungsi Tombol Simpan
        Simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //menyimpan data yang diinputkan User kedapam Variabel
                getNIM = NIM.getText().toString();
                getNama = Nama.getText().toString();
                getFakultas = Fakultas.getSelectedItem().toString();
                getProdi = Prodi.getSelectedItem().toString();
                if (outputgolongan == null){
                    getHasilgol = null;
                }else {
                    getHasilgol = outputgolongan.trim();
                }
                if (rb ==null){
                    getRjeniskelamin = null;
                }else {
                    getRjeniskelamin = rb.getText().toString();
                }

                getTgllahir = tgllahir.getText().toString();
                getNohp = nohp.getText().toString();
                getCrudemail = crudemail.getText().toString();
                getIpk = ipk.getText().toString();
                getAlamat = alamat.getText().toString();
                getGambar = ImageContainer.toString().trim();

                checkUser();

            }
        });
        ShowData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(CrudActivity.this, ListData.class);
                startActivity(intent);*/
                onBackPressed();
            }
        });

        getfoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getimage();
            }
        });

    }

    private void showDateDialog() {
        Calendar calendar = Calendar.getInstance();

        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year,month,dayOfMonth);
                tgllahir.setText(dateFormatter.format(newDate.getTime()));
            }
        }, calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }


    private void getimage(){
        Intent imageIntentGallery = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(imageIntentGallery, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        //menghandle hasil data yang diambil dari kamera atau galeri pada ImageVIew
        switch (requestCode){
            case REQUEST_CODE_CAMERA:
                if (resultCode==RESULT_OK){
                    ImageContainer.setVisibility(View.VISIBLE);
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    ImageContainer.setImageBitmap(bitmap);
                }
                break;

            case REQUEST_CODE_GALLERY:
                if (resultCode==RESULT_OK){
                    ImageContainer.setVisibility(View.VISIBLE);
                    uri = data.getData();
                    ImageContainer.setImageURI(uri);
                }
                break;
        }

    }


    private void checkUser() {

        //mengecek apakah ada data yang kosong
        if(isEmpty(getNIM)|| isEmpty(getNama)|| isEmpty(getProdi)|| isEmpty(getFakultas)|| isEmpty(getAlamat) || isEmpty(getHasilgol)||outputgolongan.equals("")||outputgolongan==null|| isEmpty(getIpk)|| isEmpty(getRjeniskelamin)|| isEmpty(getCrudemail)|| isEmpty(getTgllahir)|| isEmpty(getNohp)||uri == null){
            //Jika ada, maka akan menampilkan pesan singkat
            Toast.makeText(CrudActivity.this, "Data Tidak Boleh ada yang Kosong", Toast.LENGTH_SHORT).show();
        }else{
            //Mendapatkan data dari ImageView sebagai bytes
            ImageContainer.setDrawingCacheEnabled(true);
            ImageContainer.buildDrawingCache();
            Bitmap bitmap = ((BitmapDrawable) ImageContainer.getDrawable()).getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            //mengkompres Bitmap menjadi JPG dengan kualitas gambar 100%
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);
            byte[] bytes =stream.toByteArray();

            //Lokasi lengkap dimana gambar akan disimpan
            String namaFile = UUID.randomUUID()+".jpg";
            final String pathImage = "gambar/"+namaFile;
            UploadTask uploadTask = reference.child(pathImage).putBytes(bytes);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            getReference.child("Admin").child("Mahasiswa").push()
                                    .setValue(new data_mahasiswa(getNIM,getNama,getFakultas,getProdi,getHasilgol,getRjeniskelamin,getTgllahir,getNohp,getCrudemail,getIpk,getAlamat,uri.toString().trim()))

                                    .addOnCompleteListener( new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            //Hal ini terjadi saat user berhasil menyimpan datanya kedalam database
                                            NIM.setText("");
                                            Nama.setText("");
                                            tgllahir.setText("");
                                            nohp.setText("");
                                            crudemail.setText("");
                                            ipk.setText("");
                                            alamat.setText("");
                                            Toast.makeText(CrudActivity.this, "Data Berhasil Tersimpan", Toast.LENGTH_SHORT).show();
                                            progressBar.setVisibility(View.GONE);
                                            progressBar.bringToFront();
                                            Intent intent = new Intent(CrudActivity.this, ListData.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    });
                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(CrudActivity.this, "Uploading Gagal", Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    progressBar.setVisibility(View.VISIBLE);
                    double progress = (100.0 * snapshot.getBytesTransferred())/ snapshot.getTotalByteCount();
                    progressBar.setProgress((int) progress);
                }
            });
            //jika tidak maka data dapat diproses dan menyimpan pada database
            //Menyimpan data referensi pada Database berdasarkan User ID dari masing-masing Akun
        }
    }
    public void rbclick(View v){
        int radiobuttonid = rg.getCheckedRadioButtonId();
        rb =(RadioButton)findViewById(radiobuttonid);
    }
}