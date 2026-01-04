package fn10.bedrockrmobile.dialog;

import android.app.Dialog;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.FragmentManager;

import java.util.Objects;

import fn10.bedrockrmobile.R;

public class RAlertDialog extends AppCompatDialogFragment {

    public String message = "Not Provided";

    public RAlertDialog() {
    }

    public RAlertDialog(String message) {
        this.message = message;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), R.style.Theme_BedrockRMobile_AlertDialog);
        builder.setTitle("Alert");
        builder.setMessage(message);
        builder.setNeutralButton(R.string.acknowledge, (dia, which) -> {
            dia.dismiss();
        });

        return builder.create();
    }

    public static void showError(Resources res, FragmentManager fm, int id) {
        showError(fm, res.getString(id));
    }
    public static void showError(FragmentManager fm, Throwable e) {
        showError(fm, e.getMessage());
    }

    public static void showError(FragmentManager fm, String message) {
        RAlertDialog dialog = new RAlertDialog(message);
        dialog.show(fm, "R_ALERT");
    }
}
