package mobile.resky.x_app.CRUD;

import static android.text.TextUtils.isEmpty;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import mobile.resky.x_app.R;


//digunakan untuk mengatur Bagaimana Data akan ditampilkan
public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.ViewHolder> implements Filterable {

    //Deklarasi Variabel
    private ArrayList<data_mahasiswa> listMahasiswa;
    private ListData context;
    private ArrayList<data_mahasiswa> listMahasiswaSearch;
    private Filter setSearch = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<data_mahasiswa> filterMahasiswa = new ArrayList<>();
            if (constraint == null || constraint.length()==0){
                filterMahasiswa.addAll(listMahasiswaSearch);
            }else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (data_mahasiswa item : listMahasiswaSearch){
                    if (item.getNama().toLowerCase().contains(filterPattern)){
                        filterMahasiswa.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filterMahasiswa;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            listMahasiswa.clear();
        listMahasiswa.addAll((List) results.values);
        notifyDataSetChanged();
        }

    };


    //Membuat Konstruktor, untuk menerima input dari Database
    public RecycleViewAdapter(ArrayList<data_mahasiswa> listMahasiswa, ListData context) {
        this.listMahasiswa = listMahasiswa;
        this.context = context;
        listener = (ListData)context;
        this.listMahasiswaSearch = listMahasiswa;
    }

    @Override
    public Filter getFilter() {
        return setSearch;
    }

    //Membuat interface

    public interface dataListener{
        void onDeleteData(data_mahasiswa data, int position);
    }
    //Deklarasi Objek Interface
    dataListener listener;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View V = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_design, parent,false);
        return new ViewHolder(V);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        //Mengambil Nilai yang terdapat pada Recycle View berdasarkan Posisi Tertentu
        final String NIM = listMahasiswa.get(position).getNim();
        final String Nama = listMahasiswa.get(position).getNama();
        final String Fakultas = listMahasiswa.get(position).getFakultas();
        final String Prodi = listMahasiswa.get(position).getProdi();
        final String Goldar = listMahasiswa.get(position).getHasilgol();
        final String JenisKelamin = listMahasiswa.get(position).getRjeniskelamin();
        final String Tgllahir = listMahasiswa.get(position).getTgllahir();
        final String Nohp = listMahasiswa.get(position).getNohp();
        final String Crudemail = listMahasiswa.get(position).getCrudemail();
        final String Ipk = listMahasiswa.get(position).getIpk();
        final String Alamat = listMahasiswa.get(position).getAlamat();
        final String Gambar = listMahasiswa.get(position).getGambar();

        //memasukkan Nilai kedalam View(text View : NIM, Nama ,Prodi)
        holder.NIM.setText(     ": "+NIM);
        holder.Nama.setText(    ": "+Nama);
        holder.Fakultas.setText( ": "+Fakultas);
        holder.Prodi.setText( ": "+Prodi);
        holder.Goldar.setText(": "+Goldar);
        holder.JenisKelamin.setText(": "+JenisKelamin);
        holder.Tgllahir.setText(": "+Tgllahir);
        holder.Nohp.setText(": "+Nohp);
        holder.Crudemail.setText(": "+Crudemail);
        holder.Ipk.setText(": "+Ipk);
        holder.Alamat.setText(": "+Alamat);

        if(isEmpty(Gambar)) {
            holder.Gambar.setImageResource(R.drawable.ic_school);
        } else {
            Glide.with(holder.itemView.getContext())
                    .load(Gambar.trim())
                    .into(holder.Gambar);
        }

        holder.ListItem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View v) {
                final String[] action = {"Update", "Delete"};
                AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
                alert.setTitle("Apa yang akan anda pilih?");
                alert.setItems(action, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        switch (i){
                            case 0:
                                /*
                                Berpindah Activity pada halaman layout UpdateData dan
                                mengambil data pada ListMahasiswa
                                 */

                                Bundle bundle = new Bundle();
                                bundle.putString("dataNIM",listMahasiswa.get(position).getNim());
                                bundle.putString("dataNama",listMahasiswa.get(position).getNama());
                                bundle.putString("dataFakultas",listMahasiswa.get(position).getFakultas());
                                bundle.putString("dataGol_dar",listMahasiswa.get(position).getHasilgol());
                                bundle.putString("dataJenis_kelamin",listMahasiswa.get(position).getRjeniskelamin());
                                bundle.putString("dataTanggal",listMahasiswa.get(position).getTgllahir());
                                bundle.putString("dataNohp",listMahasiswa.get(position).getNohp());
                                bundle.putString("dataEmail",listMahasiswa.get(position).getCrudemail());
                                bundle.putString("dataIpk",listMahasiswa.get(position).getIpk());
                                bundle.putString("dataAlamat",listMahasiswa.get(position).getAlamat());
                                bundle.putString("getPrimaryKey",listMahasiswa.get(position).getKey());
                                bundle.putString("dataProdi",listMahasiswa.get(position).getProdi());
                                bundle.putString("data_gambar",listMahasiswa.get(position).getGambar());
                                Intent intent = new Intent(v.getContext(), UpdateActivity.class);
                                intent.putExtras(bundle);
                                context.startActivity(intent);
                                break;

                            case 1:
                                AlertDialog.Builder alertt = new AlertDialog.Builder(v.getContext());
                                alertt.setTitle("Apakah anda yakin akan menghapus Data ini?");
                                alertt.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                listener.onDeleteData(listMahasiswa.get(position),position);
                                            }
                                        })
                                        .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                return;
                                            }
                                        });

                                alertt.create();
                                alertt.show();
                        }
                    }
                });
                alert.create();
                alert.show();
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return listMahasiswa.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        //inisialisasi Widget
        private TextView NIM, Nama, Prodi,Fakultas,Goldar,JenisKelamin,Tgllahir,Nohp,Crudemail,Ipk,Alamat;
        private ImageView Gambar;
        private LinearLayout ListItem;

        private Button btnDelete, btnUpdate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //Menginisialisasi view view yang terpasang pada layout RecycleView kita
            NIM = itemView.findViewById(R.id.nim);
            Nama = itemView.findViewById(R.id.nama);
            Fakultas = itemView.findViewById(R.id.fakultas);
            Prodi = itemView.findViewById(R.id.prodi);
            Goldar = itemView.findViewById(R.id.golongandarah);
            JenisKelamin = itemView.findViewById(R.id.rjeniskelamin);
            Tgllahir = itemView.findViewById(R.id.tgllahir);
            Nohp = itemView.findViewById(R.id.nohp);
            Crudemail = itemView.findViewById(R.id.crudemail);
            Ipk = itemView.findViewById(R.id.ipk);
            Alamat = itemView.findViewById(R.id.alamat);
            ListItem = itemView.findViewById(R.id.list_item);
            Gambar = itemView.findViewById(R.id.gambar);
        }
    }
}
