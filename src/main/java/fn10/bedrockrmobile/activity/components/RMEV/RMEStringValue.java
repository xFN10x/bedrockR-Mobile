package fn10.bedrockrmobile.activity.components.RMEV;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import fn10.bedrockr.addons.element.FieldFilters;
import fn10.bedrockr.utils.RAnnotation;
import fn10.bedrockrmobile.R;
import fn10.bedrockrmobile.activity.components.RMElementValue;
import kotlin.Unit;

@SuppressLint("ViewConstructor")
public class RMEStringValue extends RMElementValue<String, AppCompatEditText> {

    private FieldFilters.FieldFilter filter = new FieldFilters.RegularStringFilter();

    public RMEStringValue(Context context, RAnnotation.FieldDetails details, @Nullable Field field) {
        super(context, String.class, details, field);
        if (details != null)
            try {
                filter = details.Filter().getConstructor().newInstance();
            } catch (IllegalAccessException | InstantiationException | InvocationTargetException |
                     NoSuchMethodException e) {
                Log.e(getTag().toString(), "Failed to create filter.", e);
            }
    }

    @Override
    public AppCompatEditText getInput(Context context) {
        AppCompatEditText building = new AppCompatEditText(context);
        building.setBackgroundResource(R.drawable.button);
        building.setPadding(40,10,40,10);
        building.setTextSize(14f);
        building.setHint("...");
        return building;
    }

    @Override
    public void setValue(String val) {
        getInputView().setText(val);
    }

    @Override
    public String getValue() {
        return getInputView().getText().toString();
    }

    @Override
    public boolean valid(boolean strict) {
        if (strict)
        return problem(filter.getValid(getValue()), "String not valid.");
        else return true;
    }
}
