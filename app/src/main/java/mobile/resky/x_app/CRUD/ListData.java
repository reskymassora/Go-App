package mobile.resky.x_app.CRUD;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import mobile.resky.x_app.R;


public class ListData extends AppCompatActivity implements RecycleViewAdapter.dataListener {


    //Deklarasi Variabel untuk RecycleView
    private RecyclerView recyclerView;
    RecycleViewAdapter adapter;
    private  RecyclerView.LayoutManager layoutManager;

    //Deklarasi Variabel Database Reference dan Array List dengan parameter Class Model Kita
    private DatabaseReference reference;
    private ArrayList<data_mahasiswa> dataMahasiswa;

    private FloatingActionButton fab,home;
    //Deklarasi Variabel untuk SearchView
    private EditText searcView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_data);
        getSupportActionBar().hide();

        recyclerView = findViewById(R.id.datalist);
//        fab = findViewById(R.id.fab);
//        home = findViewById(R.id.home1);


        GetData("");
//        searcView =findViewById(R.id.etSearch);
//        searcView.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                if(s.toString().isEmpty()){
//                    GetData(s.toString());
//                }else {
//                    adapter.getFilter().filter(s);
//                }
//
//
//            }
//        });
        MyRecycleView();
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(ListData.this,CrudActivity.class);
//                startActivity(intent);
//            }
//        });
//        home.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(ListData.this, MainActivity.class);
//                startActivity(intent);
//                finish();
//
//            }
//        });

    }

    private void GetData(String data) {

        //Mendapatkan Referensi Database
        reference = FirebaseDatabase.getInstance().getReference();
        reference.child("Admin").child("Mahasiswa")/*.orderByChild("nama").startAt(data.toUpperCase()).endAt(data.toLowerCase())*/
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        dataMahasiswa = new ArrayList<>();
                        dataMahasiswa.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            //mapping data pada DataSnapshot ke dalam objek mahasiswa
                            data_mahasiswa mahasiswa = snapshot.getValue(data_mahasiswa.class);


                            //mengambil Primary Key, digunakan untuk proses Update dan Delete
                            mahasiswa.setKey(snapshot.getKey());
                            dataMahasiswa.add(mahasiswa);
                        }
                        //inisialisasi Adapter dan data Mahasiswa dalam bentuk Array
                        adapter = new RecycleViewAdapter(dataMahasiswa, ListData.this);
                        adapter.notifyDataSetChanged();

                        //memasang Adapter pada RecycleView
                        recyclerView.setAdapter(adapter);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        /*
                        Kode ini akan dijalankan ketika ada eror dan pengambilan data errorr tersebut
                        lalu menampilkan errorr nya ke LogCat
                         */
                        Toast.makeText(getApplicationContext(), "Data Gagal Dimuat", Toast.LENGTH_SHORT).show();
                        Log.e("MyListActivity",error.getDetails()+" "+error.getMessage());

                    }
                });
    }

    //Method yang berisi kumpulan baris kode untuk mengatur Recycle View
    private void MyRecycleView() {
        //menggunakan Layout Manager, dan Membuat List Secara Vertical
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);


        //membuat UnderLine pada Setiap Item didalam List
        DividerItemDecoration ItemDecoration = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        ItemDecoration.setDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.line));
        recyclerView.addItemDecoration(ItemDecoration);
    }

    @Override
    public void onDeleteData(data_mahasiswa data, int position) {        /*
        Kode ini akan dipanggil ketika method OndeleteData dipanggil dari adapter
        pada recycleview melalui interface
         */
        if (reference != null){
            reference.child("Admin")
                    .child("Mahasiswa")
                    .child(data.getKey())
                    .removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(ListData.this, "Data Berhasil Dihapus", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}