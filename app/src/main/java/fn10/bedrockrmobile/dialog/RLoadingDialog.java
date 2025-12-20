package fn10.bedrockrmobile.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import fn10.bedrockrmobile.R;

public class RLoadingDialog extends AppCompatDialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        setCancelable(false);
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setView(R.layout.r_loading_screen);
        AlertDialog dia = builder.create();
        dia.setCanceledOnTouchOutside(false);
        return dia;
    }
}
