package fn10.bedrockrmobile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.IntentCompat;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Logger;

import fn10.bedrockr.addons.source.FieldFilters;
import fn10.bedrockr.addons.source.SourceItemElement;
import fn10.bedrockr.addons.source.interfaces.CreationScreenSeperator;
import fn10.bedrockr.addons.source.interfaces.ElementSource;
import fn10.bedrockr.interfaces.ElementCreationListener;
import fn10.bedrockr.utils.RAnnotation;
import fn10.bedrockrmobile.R;
import fn10.bedrockrmobile.dialog.RAlertDialog;

public class RMElementCreationScreen extends AppCompatActivity {

    private static final int AUTOMATIC_MODE = -1;
    private static ElementCreationListener creationListener = null;
    private static final String tag = "NewAddonActivity";

    private final List<Field> Fields = new ArrayList<>();
    private final Map<Field, View> FieldRElementValues = new HashMap<>();

    public static void setCreationListener(ElementCreationListener listener) {
        creationListener = listener;
    }

    public static ElementCreationListener getCreationListener() {
        return creationListener;
    }

    private final Map<Class<? extends ElementSource<?>>, Integer> CREATION_SCREEN_LAYOUTS = Map.of(
            //use -1 to make it use automatic mode
            SourceItemElement.class, AUTOMATIC_MODE
    );

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.relementcreationscreen);

        Intent intent = getIntent();

        boolean fromEmpty = true;

        if (creationListener == null) {
            RAlertDialog.showError(getSupportFragmentManager(), "No creation listener set. Cannot proceed.");
            finish();
            return;
        }

        if (!intent.hasExtra("ElementSource")) {
            RAlertDialog.showError(getSupportFragmentManager(), "Didn't get the element source class. (No extra in intent: \"ElementSource\")");
            finish();
            return;
        }

        @SuppressWarnings("unchecked")
        Class<? extends ElementSource<?>> srcClass = IntentCompat.getSerializableExtra(intent, "ElementSource", Class.class);
        if (srcClass == null) {
            RAlertDialog.showError(getSupportFragmentManager(), "Didn't get the element source class. (No extra in intent: \"ElementSource\")");
            finish();
            return;
        } else if (!CREATION_SCREEN_LAYOUTS.containsKey(srcClass)) {
            RAlertDialog.showError(getSupportFragmentManager(), "No creation screen found for class: " + srcClass.getSimpleName() + ". Doing automatic.");
        }
        Log.i(tag, "Showing creation screen with class: " + srcClass.getName());
        if (CREATION_SCREEN_LAYOUTS.getOrDefault(srcClass, AUTOMATIC_MODE) == AUTOMATIC_MODE) {
            //AUTOMATIC MODE
            LinearLayout InnerScroll = findViewById(R.id.innerScroll);

            ParameterizedType genericInterface = (ParameterizedType) srcClass.getGenericSuperclass();
            assert genericInterface != null;
            Type elementFileClass = genericInterface.getActualTypeArguments()[0];

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

                    if (field.getType().equals(CreationScreenSeperator.class)) {
                        LayoutInflater.from(this).inflate(R.layout.rcreationscreen_div, InnerScroll);
                        continue;
                    }

                    Fields.add(field);

                    if (field.isAnnotationPresent(RAnnotation.UneditableByCreation.class))
                        continue;

                    Class<?> InputType = field.getType();

                    if (String.class.isAssignableFrom(InputType)) {
                        //if its a string
                        if (!field.isAnnotationPresent(RAnnotation.StringDropdownField.class)) {
                            //normal
                            View ElementValue = LayoutInflater.from(this.peekAvailableContext()).inflate(R.layout.string_relementvalue, null);

                            TextView fieldName = ElementValue.findViewById(R.id.fieldNameTextView);
                            EditText fieldInput = ElementValue.findViewById(R.id.fieldBox);
                            ImageButton fieldHelp = ElementValue.findViewById(R.id.helpFieldButton);
                            CheckBox enabledCheck = ElementValue.findViewById(R.id.enabledCheck);

                            FieldRElementValues.put(field, ElementValue);

                            if (field.isAnnotationPresent(RAnnotation.FieldDetails.class)) {
                                RAnnotation.FieldDetails fieldDetailsAnno = field.getAnnotation(RAnnotation.FieldDetails.class);
                                assert fieldDetailsAnno != null;
                                if (fieldDetailsAnno.displayName() != null)
                                    fieldName.setText(fieldDetailsAnno.displayName());
                                else
                                    fieldName.setText(field.getName());


                                enabledCheck.setClickable(fieldDetailsAnno.Optional());
                                if (!fieldDetailsAnno.Optional()) {
                                    enabledCheck.setVisibility(TextView.GONE);
                                }
                            } else {
                                fieldName.setText(field.getName());
                            }

                            if (field.isAnnotationPresent(RAnnotation.HelpMessage.class)) {
                                RAnnotation.HelpMessage helpMessageAnno = field.getAnnotation(RAnnotation.HelpMessage.class);
                                fieldHelp.setOnClickListener(v -> {
                                    RAlertDialog.showError(getSupportFragmentManager(), helpMessageAnno.value());
                                });
                            } else {
                                fieldHelp.setEnabled(false);
                            }

                            enabledCheck.setOnCheckedChangeListener((b, checked) -> fieldInput.setEnabled(checked));
                            InnerScroll.addView(ElementValue);
                        } else {
                            //dropdown
                        }


                    } else if (Integer.class.isAssignableFrom(InputType) || int.class.isAssignableFrom(InputType) || Float.class.isAssignableFrom(InputType) || float.class.isAssignableFrom(InputType)) {
                        View ElementValue = LayoutInflater.from(this).inflate(R.layout.number_relementvalue, null);

                        TextView fieldName = ElementValue.findViewById(R.id.fieldNameTextView);
                        EditText fieldInput = ElementValue.findViewById(R.id.fieldBox);
                        ImageButton fieldHelp = ElementValue.findViewById(R.id.helpFieldButton);
                        CheckBox enabledCheck = ElementValue.findViewById(R.id.enabledCheck);

                        FieldRElementValues.put(field, ElementValue);

                        if (Float.class.isAssignableFrom(InputType) || float.class.isAssignableFrom(InputType)) {
                            fieldInput.setInputType(android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL + android.text.InputType.TYPE_CLASS_NUMBER + android.text.InputType.TYPE_NUMBER_FLAG_SIGNED);
                        }

                        if (field.isAnnotationPresent(RAnnotation.NumberRange.class)) {
                            RAnnotation.NumberRange numberRangeAnno = field.getAnnotation(RAnnotation.NumberRange.class);

                            assert numberRangeAnno != null;
                            fieldInput.setFilters(new InputFilter[]{new InputFilter() {

                                //taken from https://stackoverflow.com/questions/14212518/is-there-a-way-to-define-a-min-and-max-value-for-edittext-in-android
                                private boolean isInRange(float a, float b, float c) {
                                    return b > a ? c >= a && c <= b : c >= b && c <= a;
                                }

                                @Override
                                public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                                    try {
                                        // Removes string that is to be replaced from destination
                                        // and adds the new string in.
                                        String newVal = dest.subSequence(0, dstart)
                                                // Note that below "toString()" is the only required:
                                                + source.subSequence(start, end).toString()
                                                + dest.subSequence(dend, dest.length());
                                        float input = Float.parseFloat(newVal);
                                        if (isInRange(numberRangeAnno.min(), numberRangeAnno.max(), input))
                                            return null;
                                    } catch (NumberFormatException ignored) {
                                    }
                                    return "";
                                }
                            }});
                        }

                        if (field.isAnnotationPresent(RAnnotation.FieldDetails.class)) {
                            RAnnotation.FieldDetails fieldDetailsAnno = field.getAnnotation(RAnnotation.FieldDetails.class);
                            assert fieldDetailsAnno != null;
                            if (fieldDetailsAnno.displayName() != null)
                                fieldName.setText(fieldDetailsAnno.displayName());
                            else
                                fieldName.setText(field.getName());


                            enabledCheck.setClickable(fieldDetailsAnno.Optional());
                            if (!fieldDetailsAnno.Optional()) {
                                enabledCheck.setVisibility(TextView.GONE);
                            }
                        } else {
                            fieldName.setText(field.getName());
                        }

                        if (field.isAnnotationPresent(RAnnotation.HelpMessage.class)) {
                            RAnnotation.HelpMessage helpMessageAnno = field.getAnnotation(RAnnotation.HelpMessage.class);
                            fieldHelp.setOnClickListener(v -> {
                                assert helpMessageAnno != null;
                                RAlertDialog.showError(getSupportFragmentManager(), helpMessageAnno.value());
                            });
                        } else {
                            fieldHelp.setEnabled(false);
                        }

                        enabledCheck.setOnCheckedChangeListener((b, checked) -> fieldInput.setEnabled(checked));

                        InnerScroll.addView(ElementValue);
                    }
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
                creationListener.onElementCancel();
                finish();
            });

            draftButton.setOnClickListener(v -> {
                ElementSource<?> es;
                if ((es = create(true)) != null) {
                    creationListener.onElementDraft(es);
                    finish();
                }
            });

            createButton.setOnClickListener(v -> {
                ElementSource<?> es;
                if ((es = create(false)) != null) {
                    creationListener.onElementCreate(es);
                    finish();
                }
            });
        }
    }

    public static class FieldValidReturn implements Comparable<Boolean> {
        public Boolean valid;
        public String reason;

        public FieldValidReturn(boolean vaild) {
            new FieldValidReturn(vaild, "");
        }

        public FieldValidReturn(boolean vaild, String reason) {
            this.valid = vaild;
            this.reason = reason;
        }

        public static final FieldValidReturn True = new FieldValidReturn(true);

        public static FieldValidReturn of(boolean bool, String reasonIfFalse) {
            return new FieldValidReturn(bool, reasonIfFalse);
        }

        public static FieldValidReturn False(String reason) {
            return new FieldValidReturn(false, reason);
        }

        @Override
        public int compareTo(Boolean o) {
            return valid.compareTo(o);
        }
    }

    //taken from RElementValue
    public FieldValidReturn isFieldValid(Field field, boolean strict) {
        var log = Logger.getGlobal();
        String Target = field.getName();
        log.info("================== Checking field " + field.getName() + "... ==================");
        Class<?> InputType = field.getType();
        View RElementValue = FieldRElementValues.get(field);
        if (RElementValue == null) {
            Log.w(tag, "Field: " + field.getName() + " doesn't have a RElementValue.");
            return FieldValidReturn.False("No ElementValue to validate.");
        }
        View InputView = RElementValue.findViewById(R.id.fieldBox);

        if (!strict) {
            if (field.getAnnotation(RAnnotation.VeryImportant.class) == null) {
                log.info(field.getName() + ": its not important, and drafting; so it passes");

                return FieldValidReturn.True; // if its not strict (drafing) and not important (not like ElementName)
            }
        }

        if (!(((CheckBox) Objects.requireNonNull(FieldRElementValues.get(field)).findViewById(R.id.enabledCheck))).isChecked()) {
            log.info(Target + ": Not Enabled, so it passes");
            return FieldValidReturn.True;// if its disabled, true, because it wont get written anyways
        }

        try {
            if (List.class.isAssignableFrom(InputType)) {
                log.info(Target + ": Arrays cannot be wrong, so it passes");

                return FieldValidReturn.True;
            } else if (Boolean.class.isAssignableFrom(InputType) || boolean.class.isAssignableFrom(InputType)) {
                log.info(Target + ": Bool cannot be wrong, so it passes");

                return FieldValidReturn.True;
            } else if (Integer.class.isAssignableFrom(InputType) || int.class.isAssignableFrom(InputType) || Float.class.isAssignableFrom(InputType) || float.class.isAssignableFrom(InputType)) { // numbers
                log.info(Target + ": All numbers cannot be wrong, so it passes");

                return FieldValidReturn.True;
            } else if (UUID.class.isAssignableFrom(InputType)) {

                //TODO
                if (false) {
                    log.info(Target + ": No UUID can be read, so it fails");

                    return FieldValidReturn.False("No Texture Selected");
                } else {
                    log.info(Target + ": Texture is selected, and cannot be wrong. it passes");
                    return FieldValidReturn.True;
                }
            } else if (Map.class.isAssignableFrom(InputType)) {
                log.info(Target + ": Map cannot be wrong. it passes");

                return FieldValidReturn.True;
            } else if (InputView instanceof Spinner || InputView instanceof EditText) {
                RAnnotation.FieldDetails anno = field.getAnnotation(RAnnotation.FieldDetails.class);
                if (anno == null) {
                    log.info(Target + ": Since no field details are here, a filter cannot be provided, so anything goes. Vaild.");
                    return FieldValidReturn.True;
                }
                FieldFilters.FieldFilter Filter = anno.Filter().getDeclaredConstructor().newInstance();

                if (InputView instanceof Spinner spinner) { // for a string drop down
                    if (!Filter.getValid((spinner.getSelectedItem().toString()))) {
                        log.info(Target + ": Dropdown didn't pass filter, " + Filter.getClass().getName()
                                + "it doesn't pass");

                        return FieldValidReturn.False("String is not valid.");
                    } else
                        return FieldValidReturn.True;
                } else { //its a normal string
                    String text = ((EditText) InputView).getText().toString(); // get the text if its not specilized
                    if (InputType.equals(String.class)) { // string
                        log.info(Target + ": String is checking if vaild");
                        return FieldValidReturn.of(Filter.getValid(text), "String is not valid.");
                    } else {

                    }
                }
            }
        } catch (Exception e) {
            return FieldValidReturn.False(e.getMessage());
        }
        return FieldValidReturn.False("This is a bug. This message should never appear");
    }


    /**
     * Take the fields and turn them into an ElementSource with the contracted ElementFile.
     *
     * @param draft Specifies if this is creating into a draft or not.
     * @return the created ElementSource, or null if it fails.
     */
    public ElementSource<?> create(boolean draft) {
        for (Field field : Fields) {
            isFieldValid(field, !draft);
        }
        return null;
    }
}
