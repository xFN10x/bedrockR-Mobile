package fn10.bedrockrmobile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.IntentCompat;

import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fn10.bedrockr.addons.element.elementSources.SourceItemElement;
import fn10.bedrockr.addons.element.interfaces.CreationScreenSeparator;
import fn10.bedrockr.addons.element.interfaces.ElementSource;
import fn10.bedrockr.utils.RAnnotation;
import fn10.bedrockr.utils.RFileOperations;
import fn10.bedrockrmobile.R;
import fn10.bedrockrmobile.activity.components.RMElementValue;
import fn10.bedrockrmobile.dialog.RAlertDialog;
import fn10.bedrockrmobile.dialog.RHtmlAlert;
import fn10.bedrockrmobile.elements.RMElementCreationHandler;

public class RMElementEditingScreen extends AppCompatActivity {

    private static final int AUTOMATIC_MODE = -1;
    private static final String tag = "RMElementEditingScreen";
    public final static int CREATED = 0;
    public final static int DRAFTED = 1;
    public final static int CANCELED = 2;
    private final Map<Field, RMElementValue<?, ?>> FieldRElementValues = new HashMap<>();

    private final Map<Class<? extends ElementSource<?>>, Integer> CREATION_SCREEN_LAYOUTS = Map.of(
            //use -1 to make it use automatic mode
            SourceItemElement.class, AUTOMATIC_MODE
    );

    private RMElementCreationHandler<?> handler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.relementcreationscreen);

        Intent intent = getIntent();

        boolean fromEmpty = true;

        if (!intent.hasExtra("ElementSource")) {
            RAlertDialog.showError(getSupportFragmentManager(), "Didn't get the element source intent. (No extra in intent: \"ElementSource\")");
            finish();
            return;
        }

        @SuppressWarnings("unchecked")
        Pair<Class<? extends ElementSource<?>>, String> srcPair = IntentCompat.getSerializableExtra(intent, "ElementSource", Pair.class);

        Class<? extends ElementSource<?>> srcClass = srcPair.getLeft();
        ElementSource<?> srcObj = RFileOperations.gson.fromJson(srcPair.getValue(), srcClass);
        if (!CREATION_SCREEN_LAYOUTS.containsKey(srcClass)) {
            RAlertDialog.showError(getSupportFragmentManager(), "No creation screen found for class: " + srcClass.getSimpleName() + ". Doing automatic.");
        }
        int screenMode = CREATION_SCREEN_LAYOUTS.getOrDefault(srcClass, AUTOMATIC_MODE);
        Log.i(tag, "Showing creation screen with class: " + srcClass.getName());
        if (screenMode == AUTOMATIC_MODE) {
            //AUTOMATIC MODE
            ParameterizedType genericInterface = (ParameterizedType) srcClass.getGenericSuperclass();
            assert genericInterface != null;
            Type elementFileClass = genericInterface.getActualTypeArguments()[0];
            //Add the handler
            handler = fields -> {
                if (elementFileClass instanceof Class<?>) {
                    for (Map.Entry<Field, RMElementValue<?, ?>> entry : fields.entrySet()) {
                        RMElementValue<?, ?> rmev = entry.getValue();
                        try {
                            Object value = rmev.getValue();
                            if (value != null)
                                entry.getKey().set(srcObj.getSerialized(), value);
                        } catch (IllegalAccessException e) {
                            Log.w("Failed to set value of element file: " + rmev.getValueName(), e);
                        }
                    }
                    return srcObj;
                }
                return null;

            };
            //Add the views
            LinearLayout InnerScroll = findViewById(R.id.innerScroll);

            if (elementFileClass instanceof Class<?>) {
                // lets see how optimized and clean i can make this
                // (not very much apparently)
                List<Field> fields = new ArrayList<>(List.of(((Class<?>) elementFileClass).getFields()));
                fields.sort((f1, f2) -> {
                    int o1 = f1.isAnnotationPresent(RAnnotation.Order.class)
                            ? f1.getAnnotation(RAnnotation.Order.class).value()
                            : -1;
                    int o2 = f2.isAnnotationPresent(RAnnotation.Order.class)
                            ? f2.getAnnotation(RAnnotation.Order.class).value()
                            : -1;
                    return Integer.compare(o1, o2);
                });

                Log.i(tag, "Fields found: ");
                for (Field field : fields) {
                    Log.i(tag, field.getName());

                    if (field.getType().equals(CreationScreenSeparator.class)) {
                        LayoutInflater.from(this).inflate(R.layout.rcreationscreen_div, InnerScroll);
                        continue;
                    }

                    if (field.isAnnotationPresent(RAnnotation.UneditableByCreation.class))
                        continue;


                    Class<?> InputType = field.getType();
                    RMElementValue<?, ?> rmev = RMElementValue.of(getSupportFragmentManager(), getBaseContext(), field, InputType, srcObj.getSerialized(), RFileOperations.getCurrentWorkspace().WorkspaceName);
                    FieldRElementValues.put(field, rmev);
                    InnerScroll.addView(rmev);

                }


            } else {
                RAlertDialog.showError(getSupportFragmentManager(), "Could not get generic class.");
                finish();
                return;
            }

            AppCompatButton cancelButton = findViewById(R.id.cancelButton);
            AppCompatButton draftButton = findViewById(R.id.draftButton);
            AppCompatButton createButton = findViewById(R.id.createButton);

            cancelButton.setOnClickListener(v -> {
                setResult(CANCELED, null);
                finish();
            });

            draftButton.setOnClickListener(v -> {
                ElementSource<?> es;
                if ((es = create(true)) != null) {
                    setResult(DRAFTED, new Intent().putExtra("source", es.getJSONString()));
                    finish();
                }
            });

            createButton.setOnClickListener(v -> {
                ElementSource<?> es;
                if ((es = create(false)) != null) {
                    setResult(CREATED, new Intent().putExtra("source", es.getJSONString()));
                    finish();
                }
            });
        }
    }

    //taken from RElementValue

    /**
     * Take the fields and turn them into an ElementSource with the contracted ElementFile.
     *
     * @param draft Specifies if this is creating into a draft or not.
     * @return the created ElementSource, or null if it fails.
     */
    public ElementSource<?> create(boolean draft) {
        Map<Field, String> incorrectFields = new HashMap<>();
        for (Map.Entry<Field, RMElementValue<?, ?>> entry : FieldRElementValues.entrySet()) {
            RAnnotation.VeryImportant important = entry.getKey().getAnnotation(RAnnotation.VeryImportant.class);
            if (!entry.getValue().valid((!draft) || important != null)) {
                incorrectFields.put(entry.getKey(), entry.getValue().getProblemMessage());
            }
        }
        if (!incorrectFields.isEmpty()) {
            StringBuilder messageBuilder = new StringBuilder("<html><i>Some fields were incorrect,</i><br /><ul>");
            for (Map.Entry<Field, String> entry : incorrectFields.entrySet()) {
                messageBuilder.append("<li><b>").append(entry.getKey().getName()).append(":</b> ").append(entry.getValue()).append(" </li>");
            }
            messageBuilder.append("</ul></html>");
            RHtmlAlert.show(getSupportFragmentManager(), "Couldn't Build", messageBuilder.toString());
            return null;
        }

        return handler.createElement(FieldRElementValues);
    }
}
