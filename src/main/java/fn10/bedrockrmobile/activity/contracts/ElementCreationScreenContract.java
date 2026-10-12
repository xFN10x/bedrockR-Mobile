package fn10.bedrockrmobile.activity.contracts;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import fn10.bedrockr.addons.element.interfaces.ElementFile;
import fn10.bedrockr.addons.element.interfaces.ElementSource;
import fn10.bedrockr.utils.RFileOperations;
import fn10.bedrockrmobile.activity.RMElementEditingScreen;

/**
 * Input - a Pair, holding the ElementSrc class, and Json.
 * Output - a Pair, holding if the element is drafted, and the ElementSource..
 */
public class ElementCreationScreenContract extends ActivityResultContract<Pair<Class<? extends ElementSource<?>>, String>, Pair<Boolean, ElementSource<?>>> {
    private final static String tag = "ElementCreationScreenContract";

    @NonNull
    @Override
    public Intent createIntent(@NonNull Context context, Pair<Class<? extends ElementSource<?>>, String> src) {
        Intent creationScreenIntent = new Intent();
        creationScreenIntent.setAction("bedrockrmobile.intent.CREATEELEMENT")
                .putExtra("ElementSource", src);
        return creationScreenIntent;
    }

    @Override
    public Pair<Boolean, ElementSource<?>> parseResult(int i, @Nullable Intent intent) {
        try {
            if (i == RMElementEditingScreen.CANCELED) {
                return null;
            }
            String fileJson = intent.getStringExtra("filejson");
            Class<ElementFile<?>> fileCls = (Class<ElementFile<?>>) (Class<?>) intent.getSerializableExtra("fileCls", Class.class);
            if (fileCls == null) return null;
            ElementFile<?> elementFile = RFileOperations.gson.fromJson(fileJson, fileCls);
            return Pair.of(i == RMElementEditingScreen.DRAFTED, elementFile.getNewSource());
        } catch (Exception e) {
            Log.e(tag, "Failed to parse result of "+ tag);
            return null;
        }
    }
}
