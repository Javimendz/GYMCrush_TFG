package com.example.gymcrush.ui.dieta;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.example.gymcrush.R;
import com.example.gymcrush.data.remote.dto.ComidaDiariaResponseDto;
import java.util.Locale;

public class DetalleComidaFragment extends Fragment {

    private ComidaDiariaResponseDto comida;

    public static DetalleComidaFragment newInstance(ComidaDiariaResponseDto comida) {
        DetalleComidaFragment fragment = new DetalleComidaFragment();
        Bundle args = new Bundle();
        args.putSerializable("comida", comida);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            comida = (ComidaDiariaResponseDto) getArguments().getSerializable("comida");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detalle_comida, container, false);

        setupUI(view);

        return view;
    }

    private void setupUI(View view) {
        ImageView ivComida = view.findViewById(R.id.ivComidaDetalle);
        TextView tvNombre = view.findViewById(R.id.tvNombreComidaDetalle);
        TextView tvMomento = view.findViewById(R.id.tvMomentoDetalle);
        TextView tvCal = view.findViewById(R.id.tvCalDetail);
        TextView tvProt = view.findViewById(R.id.tvProtDetail);
        TextView tvCarb = view.findViewById(R.id.tvCarbDetail);
        TextView tvGras = view.findViewById(R.id.tvGrasDetail);

        view.findViewById(R.id.btnBackDetalleComida).setOnClickListener(v -> getParentFragmentManager().popBackStack());

        if (comida != null) {
            tvNombre.setText(comida.getNombreAlimento());
            tvMomento.setText(comida.getMomento());
            
            tvCal.setText(String.format(Locale.getDefault(), "%.0f kcal", comida.getCalorias() != null ? comida.getCalorias() : 0));
            tvProt.setText(String.format(Locale.getDefault(), "%.0fg", comida.getProteina() != null ? comida.getProteina() : 0));
            tvCarb.setText(String.format(Locale.getDefault(), "%.0fg", comida.getCarbohidratos() != null ? comida.getCarbohidratos() : 0));
            tvGras.setText(String.format(Locale.getDefault(), "%.0fg", comida.getGrasas() != null ? comida.getGrasas() : 0));

            String urlImagen = comida.getImagenUrl();
            if (urlImagen != null && !urlImagen.isEmpty()) {
                // Forzar HTTPS para evitar bloqueos de tráfico claro
                if (urlImagen.startsWith("http://")) {
                    urlImagen = urlImagen.replace("http://", "https://");
                }

                Glide.with(this)
                        .load(urlImagen)
                        .centerCrop()
                        .placeholder(R.drawable.ic_compras)
                        .error(R.drawable.ic_compras)
                        .into(ivComida);
            }

            TextView tvDesc = view.findViewById(R.id.tvDescripcionDetalle);
            if (comida.getDescripcion() != null && !comida.getDescripcion().isEmpty()) {
                tvDesc.setText(comida.getDescripcion());
            } else {
                tvDesc.setText("Receta nutritiva seleccionada para tu plan de " + (comida.getMomento() != null ? comida.getMomento().toLowerCase() : "comida") + ".");
            }

            com.google.android.material.button.MaterialButton btnReceta = view.findViewById(R.id.btnVerRecetaWeb);
            WebView webView = view.findViewById(R.id.wvReceta);
            View webViewContainer = view.findViewById(R.id.cvWebViewContainer);
            View tvLabelRecetaWeb = view.findViewById(R.id.tvLabelRecetaWeb);

            if (comida.getRecipeUrl() != null && !comida.getRecipeUrl().isEmpty()) {
                // Opción 1: Botón para abrir en navegador externo (la mantenemos por si acaso)
                btnReceta.setVisibility(View.VISIBLE);
                btnReceta.setOnClickListener(v -> {
                    android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(comida.getRecipeUrl()));
                    startActivity(intent);
                });

                // Opción 2: Mostrar dentro de la app con WebView (la manera "correcta" para mejor UX)
                tvLabelRecetaWeb.setVisibility(View.VISIBLE);
                webViewContainer.setVisibility(View.VISIBLE);
                
                WebSettings webSettings = webView.getSettings();
                webSettings.setJavaScriptEnabled(true);
                webSettings.setDomStorageEnabled(true);
                webSettings.setLoadWithOverviewMode(true);
                webSettings.setUseWideViewPort(true);
                webSettings.setBuiltInZoomControls(true);
                webSettings.setDisplayZoomControls(false);
                
                webView.setWebViewClient(new WebViewClient());
                
                // IMPORTANTE: Para que el WebView se ajuste al contenido y permita el scroll del padre
                webView.setNestedScrollingEnabled(false); 

                webView.loadUrl(comida.getRecipeUrl());
            } else {
                btnReceta.setVisibility(View.GONE);
                tvLabelRecetaWeb.setVisibility(View.GONE);
                webViewContainer.setVisibility(View.GONE);
            }
        }
    }
}
