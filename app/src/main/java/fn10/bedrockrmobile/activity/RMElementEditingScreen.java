package fn10.bedrockrmobile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
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
import fn10.bedrockr.addons.source.interfaces.ElementFile;
import fn10.bedrockr.addons.source.interfaces.ElementSource;
import fn10.bedrockr.utils.RAnnotation;
import fn10.bedrockrmobile.R;
import fn10.bedrockrmobile.dialog.RAlertDialog;
import fn10.bedrockrmobile.dialog.RHtmlAlert;
import fn10.bedrockrmobile.elements.RMElementCreationHandler;

public class RMElementEditingScreen extends AppCompatActivity {

    private static final int AUTOMATIC_MODE = -1;
    private static final String tag = "RMElementEditingScreen";

    public final static int CREATED = 0;
    public final static int DRAFTED = 1;
    public final static int CANCELED = 2;
    private final List<Field> Fields = new ArrayList<>();
    private final Map<Field, View> FieldRElementValues = new HashMap<>();

    private final Map<Class<? extends ElementSource<?>>, Integer> CREATION_SCREEN_LAYOUTS = Map.of(
            //use -1 to make it use automatic mode
            SourceItemElement.class, AUTOMATIC_MODE
    );

    private int screenMode;
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
        this.screenMode = CREATION_SCREEN_LAYOUTS.getOrDefault(srcClass, AUTOMATIC_MODE);
        Log.i(tag, "Showing creation screen with class: " + srcClass.getName());
        if (screenMode == AUTOMATIC_MODE) {
            //AUTOMATIC MODE
            ParameterizedType genericInterface = (ParameterizedType) srcClass.getGenericSuperclass();
            assert genericInterface != null;
            Type elementFileClass = genericInterface.getActualTypeArguments()[0];
            //Add the handler
            handler = fields -> {
                try {
                    if (elementFileClass instanceof Class<?>) {
                        ElementFile<?> file = ((Class<ElementFile<?>>) elementFileClass).getConstructor().newInstance();
                        for (Map.Entry<Field, View> entry : fields.entrySet()) {
                            entry.getKey().set(file, getValueFromRElementView(entry.getValue()));
                        }
                        return srcClass.getConstructor((Class<?>) elementFileClass).newInstance((ElementFile<?>) file);
                    }
                    return null;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
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

                    if (field.getType().equals(CreationScreenSeperator.class)) {
                        LayoutInflater.from(this).inflate(R.layout.rcreationscreen_div, InnerScroll);
                        continue;
                    }


                    if (field.isAnnotationPresent(RAnnotation.UneditableByCreation.class))
                        continue;

                    Fields.add(field);

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
                        } else{
                            //dropdown
                            RAnnotation.StringDropdownField annotation = field.getAnnotation(RAnnotation.StringDropdownField.class);
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_spinner_dropdown_item, annotation.value());
                            if (annotation.strict()) {
                                View ElementValue = LayoutInflater.from(this.peekAvailableContext()).inflate(R.layout.dropdown_relementvalue, null);

                                TextView fieldName = ElementValue.findViewById(R.id.fieldNameTextView);
                                Spinner fieldInput = ElementValue.findViewById(R.id.fieldBox);
                                ImageButton fieldHelp = ElementValue.findViewById(R.id.helpFieldButton);
                                CheckBox enabledCheck = ElementValue.findViewById(R.id.enabledCheck);
                                fieldInput.setAdapter(adapter);

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
                                View ElementValue = LayoutInflater.from(this.peekAvailableContext()).inflate(R.layout.notstrict_dropdown_relementvalue, null);

                                TextView fieldName = ElementValue.findViewById(R.id.fieldNameTextView);
                                MultiAutoCompleteTextView fieldInput = ElementValue.findViewById(R.id.fieldBox);
                                ImageButton fieldHelp = ElementValue.findViewById(R.id.helpFieldButton);
                                CheckBox enabledCheck = ElementValue.findViewById(R.id.enabledCheck);
                                fieldInput.setAdapter(adapter);

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
                            }
                        }


                    }
                    else if (Integer.class.isAssignableFrom(InputType) || int.class.isAssignableFrom(InputType) || Float.class.isAssignableFrom(InputType) || float.class.isAssignableFrom(InputType)) {
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
                    else if (Boolean.class.isAssignableFrom(InputType) || boolean.class.isAssignableFrom(InputType)) {
                        View ElementValue = LayoutInflater.from(this).inflate(R.layout.dropdown_relementvalue, null);

                        TextView fieldName = ElementValue.findViewById(R.id.fieldNameTextView);
                        Spinner fieldInput = ElementValue.findViewById(R.id.fieldBox);
                        ImageButton fieldHelp = ElementValue.findViewById(R.id.helpFieldButton);
                        CheckBox enabledCheck = ElementValue.findViewById(R.id.enabledCheck);

                        FieldRElementValues.put(field, ElementValue);

                        fieldInput.setAdapter(new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_spinner_dropdown_item, new String[]{"false", "true"}));

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
                    else {
                        //we end up here if a fields type doesn't do anything
                        View ElementValue = LayoutInflater.from(this.peekAvailableContext()).inflate(R.layout.unsupported_relementvalue, null);

                        TextView fieldName = ElementValue.findViewById(R.id.fieldNameTextView);
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

                        //make the checkbox unchecked so the validations skip this
                        enabledCheck.setChecked(false);
                        enabledCheck.setClickable(false);
                        enabledCheck.setVisibility(TextView.GONE);

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

    public static class FieldValidReturn {
        public Boolean valid = true;
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

        public boolean equals(FieldValidReturn other) {
            return valid.equals(other.valid);
        }

        public boolean equals(boolean other) {
            return valid.equals(other);
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
                } else { //its a normal string (or a not strict dropdown)
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

    public static Object getValueFromRElementView(View view) {
        View inputView = view.findViewById(R.id.fieldBox);

        if (inputView instanceof Spinner) {
            return ((Spinner) inputView).getSelectedItem();
        } else if (inputView instanceof EditText) {
            return ((EditText) inputView).getText();
        }
        return null;
    }


    /**
     * Take the fields and turn them into an ElementSource with the contracted ElementFile.
     *
     * @param draft Specifies if this is creating into a draft or not.
     * @return the created ElementSource, or null if it fails.
     */
    public ElementSource<?> create(boolean draft) {
        Map<Field, String> incorrectFields = new HashMap<>();
        for (Field field : Fields) {
            FieldValidReturn fieldValid = isFieldValid(field, !draft);
            if (fieldValid.equals(false)) {
                incorrectFields.put(field, fieldValid.reason);
            }
        }
        if (!incorrectFields.isEmpty()) {
            StringBuilder messageBuilder = new StringBuilder("<html><i>Some fields were incorrect,</i><br /><ul>");
            for (Map.Entry<Field, String> entry : incorrectFields.entrySet()) {
                messageBuilder.append("<li><b>"+ entry.getKey().getName() +":</b> "+ entry.getValue() +" </li>");
            }
            messageBuilder.append("</ul></html>");
            RHtmlAlert.show(getSupportFragmentManager(),"Couldn't Build",messageBuilder.toString());
            return null;
        }

        return handler.createElement(FieldRElementValues);
    }
}
