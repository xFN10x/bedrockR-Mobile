package fn10.bedrockrmobile.activity.contracts;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import fn10.bedrockr.addons.element.interfaces.ElementSource;

public class PickElementContract extends ActivityResultContract<ObjectUtils.Null, Pair<Class<? extends ElementSource<?>>, String>> {
    private final static String tag = "PickElementContract";

    @NonNull
    @Override
    public Intent createIntent(@NonNull Context context, ObjectUtils.Null aNull) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setAction("bedrockrmobile.intent.NEWELEMENT");
        return intent;
    }

    @Override
    public Pair<Class<? extends ElementSource<?>>, String> parseResult(int i, @Nullable Intent intent) {
        if (i == Activity.RESULT_OK)
            return Pair.of(((Class<? extends ElementSource<?>>) intent.getSerializableExtra("class")), intent.getStringExtra("json"));
        else
            return null;
    }
}
