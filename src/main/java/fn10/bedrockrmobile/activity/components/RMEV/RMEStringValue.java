package fn10.bedrockrmobile.activity.components.RMEV;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.reflect.InvocationTargetException;

import fn10.bedrockr.addons.element.FieldFilters;
import fn10.bedrockr.utils.RAnnotation;
import fn10.bedrockrmobile.R;
import fn10.bedrockrmobile.activity.components.RMElementValue;

public class RMEStringValue extends RMElementValue<String, EditText> {

    private FieldFilters.FieldFilter filter = new FieldFilters.RegularStringFilter();

    public RMEStringValue(Context context) {
        super(context);
        RAnnotation.FieldDetails details = getDetails();
        if (details != null)
            try {
                filter = details.Filter().getConstructor().newInstance();
            } catch (IllegalAccessException | InstantiationException | InvocationTargetException |
                     NoSuchMethodException e) {
                Log.e(getTag().toString(), "Failed to create filter.", e);
            }
    }

    @Override
    public EditText getInput() {
        EditText building = new EditText(getContext(), null, 0, R.style.Theme_BedrockRMobile);
        building.setBackgroundResource(R.drawable.buttone);
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
