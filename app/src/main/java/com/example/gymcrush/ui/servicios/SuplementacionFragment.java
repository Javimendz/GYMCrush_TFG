package com.example.gymcrush.ui.servicios;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.gymcrush.R;

public class SuplementacionFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_suplementacion, container, false);

        // Botón volver
        view.findViewById(R.id.btnBackSuple).setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        // --- PROTEINAS ---
        setup(view, R.id.prod_whey_choc, "Whey Choco", "Sabor: Chocolate Suizo.\n\nConcentrado de suero de alta calidad.");
        setup(view, R.id.prod_whey_vain, "Whey Vainilla", "Sabor: Vainilla.\n\nProteína clásica con excelente sabor.");
        setup(view, R.id.prod_iso_fresa, "ISO Fresa", "Sabor: Fresa.\n\nAislado de suero de máxima pureza.");
        setup(view, R.id.prod_whey_monodosis, "Whey Monodosis", "Sabor: Variado.\n\nPack de sobres individuales, ideal para viajar.");
        setup(view, R.id.prod_caseina, "Caseína", "Sabor: Vainilla.\n\nProteína de liberación lenta para la noche.");
        setup(view, R.id.prod_vegan_protein, "Vegan Protein", "Sabor: Chocolate.\n\nMezcla de proteínas vegetales de alta calidad.");

        // --- CREATINAS ---
        setup(view, R.id.prod_creatina_mono, "Creatina Mono", "Creatina Monohidrato.\n\nAumenta tu fuerza y volumen.");
        setup(view, R.id.prod_creatina_creapure, "Creapure", "Sello Creapure.\n\nLa creatina más pura garantizada.");
        setup(view, R.id.prod_creatina_hcl, "Creatina HCL", "Creatina Clorhidrato.\n\nAbsorción superior sin retención de líquidos.");

        // --- HARINAS Y CREMAS ---
        setup(view, R.id.prod_avena_brownie, "Avena Brownie", "Harina de Avena.\n\nSabor intenso a brownie para tus recetas.");
        setup(view, R.id.prod_avena_lotus, "Avena Lotus", "Harina de Avena.\n\nEl increíble sabor a galleta Lotus en tus tortitas.");
        setup(view, R.id.prod_avena_vainilla, "Avena Vainilla", "Harina de Avena.\n\nSabor suave y dulce para el desayuno.");
        setup(view, R.id.prod_arroz_vain, "Arroz Vainilla", "Crema de Arroz.\n\nEnergía rápida y digestión ligera.");
        setup(view, R.id.prod_arroz_choc, "Arroz Choco", "Crema de Arroz.\n\nDeliciosa fuente de hidratos sabor chocolate.");
        setup(view, R.id.prod_arroz_galleta, "Arroz Galleta", "Crema de Arroz.\n\nTextura cremosa con sabor a galleta maría.");
        setup(view, R.id.prod_arroz_natural, "Arroz Natural", "Sin Sabor.\n\n100% crema de arroz pura sin añadidos.");

        // --- ACCESORIOS ---
        setup(view, R.id.prod_straps, "Straps Pro", "Accesorio.\n\nMejora el agarre en tirones pesados.");
        setup(view, R.id.prod_muñequeras, "Muñequeras", "Accesorio.\n\nEstabilidad máxima para tus muñecas.");
        setup(view, R.id.prod_shaker_pink, "Shaker Rosa", "Accesorio.\n\nMezclador resistente de 600ml color rosa neón.");
        setup(view, R.id.prod_shaker_black, "Shaker Negro", "Accesorio.\n\nEstilo elegante mate para tus batidos.");

        // --- BARRITAS Y SNACKS ---
        setup(view, R.id.prod_barrita_oreo, "Barrita Oreo", "Sabor: Cookies.\n\n20g de proteína. Tu snack favorito.");
        setup(view, R.id.prod_barrita_caramel, "Barrita Toffee", "Sabor: Caramelo.\n\nTextura crujiente y deliciosa.");
        setup(view, R.id.prod_barrita_jungli, "Barrita Jungli", "Sabor: Galleta Jungli.\n\nSabor auténtico a galletas de animales.");
        setup(view, R.id.prod_snack_protein, "Protein Snack", "Sabor: Salado.\n\nChips proteicas para picar sano.");

        // --- SALUD ---
        setup(view, R.id.prod_l_carnitina, "L-Carnitina", "Quema de grasa.\n\nAyuda a utilizar la grasa como energía.");
        setup(view, R.id.prod_magnesio, "Magnesio", "Salud.\n\nEvita calambres y mejora el descanso muscular.");
        setup(view, R.id.prod_artiflex, "Artiflex", "Articulaciones.\n\nProtege tus cartílagos del desgaste intenso.");
        setup(view, R.id.prod_ashwagandha, "Ashwagandha", "Salud.\n\nReduce el estrés y mejora la recuperación.");
        setup(view, R.id.prod_multi, "Multivit", "Vitaminas.\n\nComplejo completo para el bienestar general.");
        setup(view, R.id.prod_omega, "Omega 3", "Ácidos grasos.\n\nSalud cardiovascular y cognitiva.");
        setup(view, R.id.prod_zma, "ZMA", "Recuperación.\n\nZinc, Magnesio y B6 para optimizar el descanso.");
        setup(view, R.id.prod_vit_c, "Vitamina C", "Salud.\n\nPotente antioxidante y refuerzo inmune.");

        // --- RENDIMIENTO ---
        setup(view, R.id.prod_pre_apple, "Pre-Workout Apple", "Energía.\n\nMáxima concentración y pump muscular.");
        setup(view, R.id.prod_pre_blue, "Pre-Workout Blue", "Energía.\n\nFórmula explosiva para entrenos extremos.");
        setup(view, R.id.prod_pre_pump, "Pre-Pump", "Congestión.\n\nSin estimulantes, enfocado en el bombeo.");
        setup(view, R.id.prod_bcaa, "BCAA 8:1:1", "Sabor: Sandía.\n\nRecuperación intra-entrenamiento.");
        setup(view, R.id.prod_glutamina, "Glutamina", "Recuperación.\n\nAminoácido clave para el tejido muscular.");

        return view;
    }

    private void setup(View root, int id, String nombre, String detalles) {
        View item = root.findViewById(id);
        if (item != null) {
            TextView tv = item.findViewById(R.id.tv_nombre_producto);
            if (tv != null) tv.setText(nombre);
            item.setOnClickListener(v -> mostrarDialogoDetalles(nombre, detalles));
        }
    }

    private void mostrarDialogoDetalles(String nombre, String detalles) {
        if (getContext() == null) return;

        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_entrenador_info);
        
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        TextView tvNombre = dialog.findViewById(R.id.tv_dialog_nombre);
        TextView tvDesc = dialog.findViewById(R.id.tv_dialog_descripcion);
        View btnCerrar = dialog.findViewById(R.id.btn_cerrar_dialog);

        tvNombre.setText(nombre);
        tvDesc.setText(detalles);

        btnCerrar.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}
