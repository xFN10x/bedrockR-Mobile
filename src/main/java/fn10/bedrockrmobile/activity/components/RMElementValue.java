package fn10.bedrockrmobile.activity.components;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;

import java.lang.reflect.Field;

import fn10.bedrockr.addons.element.ValidatableValue;
import fn10.bedrockr.addons.element.interfaces.SourcelessElementFile;
import fn10.bedrockr.utils.RAnnotation;
import fn10.bedrockr.utils.RLogUtils;
import fn10.bedrockrmobile.R;
import fn10.bedrockrmobile.activity.components.RMEV.RMEStringValue;
import fn10.bedrockrmobile.activity.components.RMEV.RMEUnsupportedValue;
import fn10.bedrockrmobile.dialog.RHtmlAlert;

public abstract class RMElementValue<T, U extends View> extends ConstraintLayout implements ValidatableValue {

    protected static boolean are(Field field, Class<?> cls) {
        return are(field.getType(), cls);
    }

    protected static boolean are(@NonNull Class<?> cls0, @NonNull Class<?> cls1) {
        return cls1.isAssignableFrom(cls0);
    }

    public static <T> RMElementValue<T, ?> of(FragmentManager frag, Context context,
                                              @Nullable Field field,
                                              @NonNull Class<T> type,
                                              @Nullable SourcelessElementFile TargetFile,
                                              @Nullable String WorkspaceName) {
        RAnnotation.FieldDetails details = field.getAnnotation(RAnnotation.FieldDetails.class);
        RAnnotation.HelpMessage helpMsg = field.getAnnotation(RAnnotation.HelpMessage.class);
        RAnnotation.StringDropdownField dropdown = field.getAnnotation(RAnnotation.StringDropdownField.class);

        RMElementValue<T, ?> returning = null;
        String fieldName;
        String fieldHelp;

        if (details != null) fieldName = details.displayName();
        else fieldName = field.getName();

        if (helpMsg != null) fieldHelp = helpMsg.value();
        else fieldHelp = null;

        if (are(type, String.class)) {
            if (dropdown == null)
                returning = (RMElementValue<T, ?>) new RMEStringValue(context);
        }
        if (returning == null)
        {
            returning = (RMElementValue<T, ?>) new RMEUnsupportedValue(context);
        }

        returning.giveType(type);
        returning.giveDetails(details);

        returning.getFieldNameView().setText(fieldName);
        if (fieldHelp != null) {
            returning.getHelpButtonView().setOnClickListener(l -> RHtmlAlert.show(frag, "Help for: " + fieldName, fieldHelp));
        } else returning.getHelpButtonView().setVisibility(GONE);

        if (details != null)
            if (!details.Optional()) returning.getEnabledCheckView().setVisibility(GONE);

        if (field != null && !(returning instanceof RMEUnsupportedValue))
            try {
                Object v = field.get(TargetFile);
                if (v != null && returning.getType().isAssignableFrom(v.getClass())) {
                    returning.setValue(returning.getType().cast(v));
                }
            } catch (IllegalAccessException e) {
                RLogUtils.exception("This field doesn't belong to the target file.", e);
            }
        return returning;
    }

    /// Returns the bool passed to it, but if it's false, changes the problem text.
    public boolean problem(boolean bool, String fals) {
        if (!bool) problem = fals;
        return bool;
    }

    private Class<T> type;
    private RAnnotation.FieldDetails details;
    private String problem;

    public RMElementValue(Context context) {
        super(context);
        inflate(context, getView(), this);
    }

    public void giveType(Class<T> cls) {
        type = cls;
    }

    public void giveDetails(RAnnotation.FieldDetails details) {
        this.details = details;
    }

    public Class<T> getType() {
        return type;
    }

    public RAnnotation.FieldDetails getDetails() {
        return details;
    }

    protected abstract int getView();

    public TextView getFieldNameView() {
        return findViewById(R.id.fieldNameTextView);
    }

    public ImageButton getHelpButtonView() {
        return findViewById(R.id.helpFieldButton);
    }

    public CheckBox getEnabledCheckView() {
        return findViewById(R.id.enabledCheck);
    }

    public U getInputView() {
        return findViewById(R.id.fieldBox);
    }

    public abstract void setValue(T val);

    public abstract T getValue();

    @Override
    public String getProblemMessage() {
        return problem;
    }

    @Override
    public String getValueName() {
        return getFieldNameView().getText().toString();
    }

}
