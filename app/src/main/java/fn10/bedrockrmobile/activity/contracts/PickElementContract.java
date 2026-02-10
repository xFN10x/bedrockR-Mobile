package fn10.bedrockrmobile.activity.contracts;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.apache.commons.lang3.ObjectUtils;

import fn10.bedrockr.addons.source.interfaces.ElementSource;

public class PickElementContract extends ActivityResultContract<ObjectUtils.Null, Class<? extends ElementSource<?>>> {
    private final static String tag = "PickElementContract";

    @NonNull
    @Override
    public Intent createIntent(@NonNull Context context, ObjectUtils.Null aNull) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setAction("bedrockrmobile.intent.NEWELEMENT");
        return intent;
    }

    @Override
    public Class<? extends ElementSource<?>> parseResult(int i, @Nullable Intent intent) {
        if (i == Activity.RESULT_OK)
        return ((Class<? extends ElementSource<?>>) intent.getSerializableExtra("class"));
        else
            return null;
    }
}
