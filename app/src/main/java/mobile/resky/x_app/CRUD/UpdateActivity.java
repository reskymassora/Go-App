package mobile.resky.x_app.CRUD;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

import mobile.resky.x_app.R;


public class UpdateActivity extends AppCompatActivity {

    //Deklarasi Variabel
    private String[] ListProdi,ListFakultas;
    private EditText nimBaru, namaBaru, nohpbaru,emailbaru,ipkbaru,alamatbaru,newtgllahir;
    private Spinner new_fakultas,new_prodi;
    private ImageView fotolama;
    private ProgressBar progressBar;
    DatePickerDialog datePickerDialog;
    SimpleDateFormat dateFormatter, Formatdatalama;
    private RadioButton Pria,Wanita;
    private CheckBox golA,golB,golAB,golO;
    private Button update,btn_gantifoto;
    public Uri imageUrl;
    private DatabaseReference database;
    FirebaseStorage storage;
    private StorageReference storageReference;
    private String cekNim, cekNama,cekFakultas, ceknohp,cekemail,cekipk,cekalamat,cekprodi,cekgolongan,cekjeniskelamin,cektgl,outputgolongan,outputjeniskelamin;

    private static final int REQUEST_CODE_CAMERA = 1;
    private static final int REQUEST_CODE_GALERI = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        getSupportActionBar().setTitle("Update Data");
        nimBaru = findViewById(R.id.new_nim);
        namaBaru = findViewById(R.id.new_nama);
        new_fakultas = findViewById(R.id.new_fakultas);
        nohpbaru = findViewById(R.id.new_Nohp);
        emailbaru = findViewById(R.id.new_email);
        ipkbaru = findViewById(R.id.new_ipk);
        alamatbaru = findViewById(R.id.new_alamat);
        new_prodi = findViewById(R.id.new_prodi);
        golA = findViewById(R.id.golA);
        golB = findViewById(R.id.golB);
        golAB = findViewById(R.id.golAB);
        golO  = findViewById(R.id.golO);
        Pria = findViewById(R.id.Pria);
        Wanita = findViewById(R.id.Wanita);
        newtgllahir = findViewById(R.id.new_tgllahir);
        update = findViewById(R.id.update);

        //Gambar image view
        fotolama = findViewById(R.id.foto_lama);
        //btn ganti foto
        btn_gantifoto = findViewById(R.id.btn_gantifoto);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        storageReference = FirebaseStorage.getInstance().getReference();
        database = FirebaseDatabase.getInstance().getReference();
        getData();

        btn_gantifoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getimage();
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Mendapatkan Data Mahasiswa yang akan dicek
                cekNim = nimBaru.getText().toString();
                cekNama = namaBaru.getText().toString();
                cekFakultas = new_fakultas.getSelectedItem().toString();
                cekprodi = new_prodi.getSelectedItem().toString();
                ceknohp = nohpbaru.getText().toString();
                cekemail = emailbaru.getText().toString();
                cekipk = ipkbaru.getText().toString();
                cekalamat = alamatbaru.getText().toString();
                cektgl = newtgllahir.getText().toString();

                //mengecek agar tidak ada data yang kosong saat proses update
                if (isEmpty(cekNim)||isEmpty(cekNama)||isEmpty(cekemail)||isEmpty(cekFakultas)||isEmpty(cekipk)||outputgolongan==null ||outputjeniskelamin==null||isEmpty(cekalamat)||isEmpty(ceknohp)||isEmpty(cekprodi)||isEmpty(cektgl)){
                    Toast.makeText(UpdateActivity.this, "Data tidak boleh ada yang kosong", Toast.LENGTH_SHORT).show();
                } else {

                    progressBar.setVisibility(View.VISIBLE);
                    //mendapatkan data dari Image View sebagai Bytes
                    fotolama.setDrawingCacheEnabled(true);
                    fotolama.buildDrawingCache();
                    Bitmap bitmap = ((BitmapDrawable) fotolama.getDrawable()).getBitmap();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();

                    //mengkompress Bitmap menjadi JPG
                    bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);
                    byte[] bytes = stream.toByteArray();

                    //Lokasi Lengkap dimana gambar disimpan
                    String namaFile = UUID.randomUUID()+".jpg";
                    final String pathImage = "foto/"+namaFile;
                    UploadTask uploadTask = storageReference.child(pathImage).putBytes(bytes);
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    /*
                                    Menjalankan proses update data
                                    Method Setter digunakan untuk mendapatkan data baru yang diinputkan user */

                                    data_mahasiswa setMahasiswa = new data_mahasiswa();
                                    setMahasiswa.setNim(nimBaru.getText().toString());
                                    setMahasiswa.setNama(namaBaru.getText().toString());
                                    setMahasiswa.setFakultas(ListFakultas[new_fakultas.getSelectedItemPosition()]);
                                    setMahasiswa.setProdi(ListProdi[new_prodi.getSelectedItemPosition()]);
                                    setMahasiswa.setNohp(nohpbaru.getText().toString());
                                    setMahasiswa.setCrudemail(emailbaru.getText().toString());
                                    setMahasiswa.setIpk(ipkbaru.getText().toString());
                                    setMahasiswa.setAlamat(alamatbaru.getText().toString());
                                    setMahasiswa.setHasilgol(outputgolongan.trim());
                                    setMahasiswa.setRjeniskelamin(outputjeniskelamin.trim());
                                    setMahasiswa.setTgllahir(newtgllahir.getText().toString());
                                    setMahasiswa.setGambar(uri.toString().trim());

                                    updateMahasiswa(setMahasiswa);
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UpdateActivity.this, "Update Gagal", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        progressBar.setVisibility(View.VISIBLE);
                        double progress = (100.0 * snapshot.getBytesTransferred())/ snapshot.getTotalByteCount();
                        progressBar.setProgress((int) progress);
                        }
                    });
                }
            }
        });
    }
    //mengecek apakah ada data yang kosong, sebelum di update
    private boolean isEmpty(String s){
        return TextUtils.isEmpty(s);
    }
    private void getData() {
        //Menampilkan data dari item yang dipilih sebelumnya
        final String getNIM = getIntent().getExtras().getString("dataNIM");
        final String getNama = getIntent().getExtras().getString("dataNama");
        final String getFakultas = getIntent().getExtras().getString("dataFakultas");
        final String getNohp = getIntent().getExtras().getString("dataNohp");
        final String getEmail = getIntent().getExtras().getString("dataEmail");
        final String getIpk = getIntent().getExtras().getString("dataIpk");
        final String getAlamat = getIntent().getExtras().getString("dataAlamat");
        final String getProdi = getIntent().getExtras().getString("dataProdi");
        final String getHasilgol = getIntent().getExtras().getString("dataGol_dar");
        final String getJenisKelamin = getIntent().getExtras().getString("dataJenis_kelamin");
        final String getTgl = getIntent().getExtras().getString("dataTanggal");
        final String getGambar =getIntent().getExtras().getString("data_gambar");

        //mengatur tampilan gambar
        if (isEmpty(getGambar)){
            //foto lama = nama imageview
            fotolama.setImageResource(R.drawable.ic_school);
        }else {
            Glide.with(UpdateActivity.this)
                    .load(getGambar)
                    .into(fotolama);
        }

        golA.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
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
                if(isChecked) {
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
                if(isChecked) {
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
                if(isChecked) {
                    golA.setChecked(false);
                    golAB.setChecked(false);
                    golB.setChecked(false);
                    outputgolongan = "O";
                }
            }
        });

        Pria.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    outputjeniskelamin = "Pria";
                }
            }
        });
        Wanita.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    outputjeniskelamin = "Wanita";
                }
            }
        });

        //Mengatur DatePicker
        dateFormatter = new SimpleDateFormat("dd MMM yyyy");
        newtgllahir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialog();
            }
        });


        ListProdi = new String[]{"Informatika", "Sistem Informasi", "Teknologi Informasi", "Bisnis Digital", "Bahasa Inggris"};
        ArrayAdapter<String> prodiadapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, ListProdi);
        new_prodi.setAdapter(prodiadapter);
        new_prodi.setSelection(prodiadapter.getPosition(getProdi.trim()));


        ListFakultas = new String[]{"Ilmu Komputer", "Bisnis dan Ilmu Sosial", "Ekonomi dan Sosial"};
        ArrayAdapter<String> fakultasadapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, ListFakultas);
        new_fakultas.setAdapter(fakultasadapter);
        new_fakultas.setSelection(fakultasadapter.getPosition(getFakultas.trim()));


        if(getHasilgol.trim().toString().equals("Golongan Darah A")) {
            golA.setChecked(true);
        } else if(getHasilgol.trim().toString().equals("Golongan Darah B")) {
            golB.setChecked(true);
        } else if(getHasilgol.trim().toString().equals("Golongan Darah AB")) {
            golAB.setChecked(true);
        } else if(getHasilgol.trim().toString().equals("Golongan Darah O")){
            golO.setChecked(true);
        }

        if (getJenisKelamin.trim().equals("Pria")){
            Pria.setChecked(true);
        } else if (getJenisKelamin.trim().equals("Wanita")){
            Wanita.setChecked(true);
        }

        nimBaru.setText(getNIM);
        namaBaru.setText(getNama);
        nohpbaru.setText(getNohp);
        emailbaru.setText(getEmail);
        ipkbaru.setText(getIpk);
        alamatbaru.setText(getAlamat);
        newtgllahir.setText(getTgl);

    }

    private void getimage() {
        Intent imageIntenGallery = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(imageIntenGallery, REQUEST_CODE_GALERI);
    }
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        switch (requestCode){
            case REQUEST_CODE_CAMERA:
                break;
            case REQUEST_CODE_GALERI:
                if (resultCode==RESULT_OK){
                    fotolama.setVisibility(View.VISIBLE);
                    Uri uri = data.getData();
                    fotolama.setImageURI(uri);

                }
                break;
        }
    }

    private void showDateDialog() {
        Calendar calendar = Calendar.getInstance();

        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                Calendar newDate = Calendar.getInstance();
                newDate.set(year,month,dayOfMonth);
                newtgllahir.setText(dateFormatter.format(newDate.getTime()));
            }
        },calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void updateMahasiswa(data_mahasiswa setMahasiswa) {
        progressBar.setVisibility(View.GONE);
        String getKey = getIntent().getExtras().getString("getPrimaryKey");
        database.child("Admin")
                .child("Mahasiswa")
                .child(getKey)
                .setValue(setMahasiswa)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        nimBaru.setText("");
                        namaBaru.setText("");
                        nohpbaru.setText("");
                        emailbaru.setText("");
                        ipkbaru.setText("");
                        alamatbaru.setText("");
                        newtgllahir.setText("");
                        Toast.makeText(UpdateActivity.this, "Data Berhasil di Update", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });

    }
}