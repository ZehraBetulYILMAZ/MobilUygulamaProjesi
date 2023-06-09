package com.kubrahuseyinzehra.mobilproje;



import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.kubrahuseyinzehra.mobilproje.Models.HousingPojo;
import com.kubrahuseyinzehra.mobilproje.Models.ResimEklePojo;
import com.kubrahuseyinzehra.mobilproje.RestApi.ApiUtils;
import com.kubrahuseyinzehra.mobilproje.RestApi.RestApi;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ExtraHInfoFragment extends Fragment {


    AppCompatButton resimsecbutton, resimeklebutton,buttonKyaydet;
    ImageView secilenresimImageView;
    String uye_id, resim;
    private RestApi restApi;
    Bitmap bitmap;
    ProgressDialog progressDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View tasarim = inflater.inflate(R.layout.fragment_extra_h_info, container, false);
        uye_id = HousingPojo.getUye_id();
        tanımla(tasarim);
        progressDialog = new ProgressDialog(requireContext());
        return tasarim;
    }

    /* public void ilanKaydet(){
         restApi.ilanver(HousingPojo.getBaslik(),HousingPojo.getUye_id(),HousingPojo.getAciklama(),HousingPojo.getIl(),HousingPojo.getIlce(),HousingPojo.getMahalle(),HousingPojo.getIlan_durumu(),HousingPojo.getBrut_metrekare(),HousingPojo.getNet_metrekare(),HousingPojo.getBina_yasi(),HousingPojo.getBina_kat_sayisi(),HousingPojo.getOda_sayisi(),HousingPojo.getBulundugu_kat(),HousingPojo.getBanyo_sayisi(),HousingPojo.getIsitma_tipi(),HousingPojo.getSon_gun_tarihi(),HousingPojo.getKonut_sekli(),"esyasız",HousingPojo.getKullanim_durumu(),"Ön").enqueue(new Callback<IlanSonucPojo>() {
             @Override
             public void onResponse(Call<IlanSonucPojo> call, Response<IlanSonucPojo> response) {
               if(response.body().isTf()){
                   ilan_id = response.body().getId();

               }
             }

             @Override
             public void onFailure(Call<IlanSonucPojo> call, Throwable t) {

             }
         });
     }*/
    ActivityResultLauncher<String> resimcercevesisecme = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri result) {
                    secilenresimImageView.setImageURI(result);
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), result);
                        secilenresimImageView.setVisibility(View.VISIBLE);
                        secilenresimImageView.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
    );

    public void resimyukle() {
        resim = imageToString();
        restApi.resimyukle(HousingPojo.getUye_id(), HousingInfoFragment.ilan_id, resim).enqueue(new Callback<ResimEklePojo>() {
            @Override
            public void onResponse(Call<ResimEklePojo> call, Response<ResimEklePojo> response) {
                if (response.body() != null) {
                    if (response.body().isTf()) {
                        Toast.makeText(requireContext(), response.body().getSonuc(), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(requireContext(), response.body().getSonuc(), Toast.LENGTH_LONG).show();
                    }
                }

            }

            @Override
            public void onFailure(Call<ResimEklePojo> call, Throwable t) {

            }
        });
    }


    public void tanımla(View tasarim) {
        resimeklebutton = tasarim.findViewById(R.id.resimeklebutton);
        resimsecbutton = tasarim.findViewById(R.id.resimsecbuuton);
        buttonKyaydet=tasarim.findViewById(R.id.buttonKyaydet);
        secilenresimImageView = tasarim.findViewById(R.id.secilenresimImageView);


        restApi = ApiUtils.getRestApiInterface();
        resimsecbutton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotificationPermission")
            @Override
            public void onClick(View view) {

                resimcercevesisecme.launch("image/*");

                // Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(), secilenresimImageView.getId());


            }
        });
        resimeklebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("v", "yükleme öncesi");
                if (imageToString() != "") {
                    if (HousingInfoFragment.ilan_id != null) {
                        resimyukle();
                    }
                } else {
                    Toast.makeText(requireContext(), "Lütfen Resim Seçiniz", Toast.LENGTH_LONG).show();
                }

            }
        });
        buttonKyaydet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExtraHInfoFragmentDirections.ExtradanMain gecis = ExtraHInfoFragmentDirections.extradanMain(uye_id);
                Navigation.findNavController(view).navigate(gecis);
               //Navigation.findNavController(view).navigate(R.id.extradan_main);
                bildirimGoster();
            }
        });

    }
    @SuppressLint("NotificationPermission")
    private void bildirimGoster() {

        NotificationCompat.Builder builder;

        NotificationManager bildirimYoneticisi =
                (NotificationManager) requireContext().getSystemService(Context.NOTIFICATION_SERVICE);

        Intent ıntent = new Intent(requireContext(), AnaSayfaFragment.class);

        PendingIntent gidilecekIntent = PendingIntent.getActivity(requireContext(), 1, ıntent, PendingIntent.FLAG_UPDATE_CURRENT);


        String kanalId = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // Oreo sürümü için bu kod çalışacak.

            kanalId = "kanalId";
            String kanalAd = "kanalAd";
            String kanalTanım = "kanalTanım";
            int kanalOnceligi = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel kanal = bildirimYoneticisi.getNotificationChannel(kanalId);

            if (kanal == null) {
                kanal = new NotificationChannel(kanalId, kanalAd, kanalOnceligi);
                kanal.setDescription(kanalTanım);
                bildirimYoneticisi.createNotificationChannel(kanal);
            }

            builder = new NotificationCompat.Builder(requireContext(), kanalId);

            builder.setContentTitle("Başlık")  // gerekli
                    .setContentText("İçerik")  // gerekli
                    .setSmallIcon(R.drawable.image) // gerekli
                    .setAutoCancel(true)  // Bildirim tıklandıktan sonra kaybolur."
                    .setContentIntent(gidilecekIntent);

        } else { // OREO Sürümü haricinde bu kod çalışacak.

            builder = new NotificationCompat.Builder(requireContext());

            builder.setContentTitle("Başlık")  // gerekli
                    .setContentText("İçerik")  // gerekli
                    .setSmallIcon(R.drawable.image) // gerekli
                    .setContentIntent(gidilecekIntent)
                    .setAutoCancel(true)  // Bildirim tıklandıktan sonra kaybolur."
                    .setPriority(Notification.PRIORITY_HIGH);
        }

        bildirimYoneticisi.notify(1, builder.build());

    }
    public String imageToString() {
        String text;

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        if(bitmap != null) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] bytes = byteArrayOutputStream.toByteArray();

            text = Base64.encodeToString(bytes, Base64.DEFAULT);
        }
        else{
            text="";
        }

        return text;
    }
}