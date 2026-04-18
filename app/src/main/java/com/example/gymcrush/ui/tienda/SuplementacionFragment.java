package com.example.gymcrush.ui.tienda;

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

        // Configuración de productos (Catálogo extendido)

        // PROTEINAS
        setup(view, R.id.prod_whey_choc, "Whey Choco", "Sabor: Chocolate Suizo.\n\nProteína de suero de alta calidad para recuperación.");
        setup(view, R.id.prod_whey_vain, "Whey Vainilla", "Sabor: Vainilla Bourbon.\n\nFácil disolución y excelente sabor cremoso.");
        setup(view, R.id.prod_iso_fresa, "ISO Fresa", "Sabor: Fresa.\n\nAislado de proteína sin grasas ni azúcares.");

        // CREATINAS
        setup(view, R.id.prod_creatina_mono, "Creatina Mono", "Creatina Monohidrato pura.\n\nMejora la fuerza y potencia muscular.");
        setup(view, R.id.prod_creatina_creapure, "Creapure", "Sello Creapure.\n\nLa creatina más pura del mercado garantizada.");
        setup(view, R.id.prod_creatina_hcl, "Creatina HCL", "Creatina Clorhidrato.\n\nMejor absorción, sin necesidad de fase de carga.");

        // HARINAS Y CREMAS
        setup(view, R.id.prod_avena_brownie, "Avena Brownie", "Sabor: Chocolate Brownie.\n\nHarina de avena integral ideal para desayunos.");
        setup(view, R.id.prod_avena_lotus, "Avena Lotus", "Sabor: Galleta Lotus.\n\nSabor premium para tus tortitas fitness.");
        setup(view, R.id.prod_arroz_vain, "Arroz Vainilla", "Crema de Arroz.\n\nEnergía rápida y digestión ligera pre-entreno.");
        setup(view, R.id.prod_arroz_choc, "Arroz Choco", "Crema de Arroz.\n\nFuente de hidratos de alta calidad con sabor cacao.");
        setup(view, R.id.prod_arroz_galleta, "Arroz Galleta", "Crema de Arroz.\n\nSabor tradicional a galleta maría.");
        setup(view, R.id.prod_arroz_natural, "Arroz Natural", "Sin Sabor.\n\n100% harina de arroz instantánea sin añadidos.");

        // ACCESORIOS
        setup(view, R.id.prod_straps, "Straps Pro", "Accesorio de Agarre.\n\nMejora tu tracción en pesos muertos y remos.");
        setup(view, R.id.prod_muñequeras, "Muñequeras", "Soporte Articular.\n\nProtección para tus muñecas en presses pesados.");
        setup(view, R.id.prod_shaker_pink, "Shaker Rosa", "Accesorio.\n\nMezclador resistente de 600ml color neón.");
        setup(view, R.id.prod_shaker_black, "Shaker Negro", "Accesorio.\n\nMezclador premium con rejilla antigrumos.");

        // BARRITAS Y SNACKS
        setup(view, R.id.prod_barrita_oreo, "Barrita Oreo", "Sabor: Cookies & Cream.\n\n20g de proteína por barrita. Snack delicioso.");
        setup(view, R.id.prod_barrita_caramel, "Barrita Toffee", "Sabor: Caramelo.\n\nSnack alto en proteína y bajo en azúcar.");
        setup(view, R.id.prod_snack_protein, "Protein Chips", "Sabor: Queso.\n\nSnack salado proteico para picar sin culpa.");

        // PRE-ENTRENOS
        setup(view, R.id.prod_pre_apple, "Pre-Workout", "Sabor: Manzana Ácida.\n\nMáximo enfoque, energía y pump muscular.");
        setup(view, R.id.prod_pre_blue, "Pre-Workout", "Sabor: Blue Raspberry.\n\nFórmula explosiva para entrenos intensos.");
        setup(view, R.id.prod_pre_pump, "Pre-Pump", "Sin Estimulantes.\n\nEnfoque en vasodilatación sin cafeína.");

        // OTROS
        setup(view, R.id.prod_multi, "Multivit Pro", "Complejo Vitamínico.\n\nRefuerza tu sistema inmune y energía diaria.");
        setup(view, R.id.prod_omega, "Omega 3", "Ácidos Grasos.\n\nSalud cardiovascular y antiinflamatorio natural.");
        setup(view, R.id.prod_bcaa, "BCAA 8:1:1", "Sabor: Sandía.\n\nRecuperación y protección de masa muscular.");

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
        dialog.setContentView(R.layout.dialog_entrenador_info); // Usamos el diseño horizontal existente
        
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
