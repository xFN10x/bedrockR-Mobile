package fn10.bedrockrmobile.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.FragmentManager;

import fn10.bedrockrmobile.R;

public class RHtmlAlert extends AppCompatDialogFragment {

    private RHtmlAlert(String title, String htmlString) {
        this.title = title;
        this.message = htmlString;
    }

    private final String message;
    private final String title;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        setCancelable(false);
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity(), R.style.Theme_BedrockRMobile_AlertDialog);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.rhtmlalert, null);
        TextView title = view.findViewById(R.id.titleText);
        TextView message = view.findViewById(R.id.messageText);
        title.setText(this.title);
        message.setText(Html.fromHtml(this.message, 0));
        builder.setView(view);
        builder.setNeutralButton(R.string.acknowledge, (di,i) -> {
            assert this.getDialog() != null;
            this.getDialog().cancel();
        });
        AlertDialog dia = builder.create();
        dia.setCanceledOnTouchOutside(false);
        return dia;
    }

    public static void show(FragmentManager frag, String title, String html) {
        new RHtmlAlert(title, html).show(frag, "RHtmlAlert");
    }

}
