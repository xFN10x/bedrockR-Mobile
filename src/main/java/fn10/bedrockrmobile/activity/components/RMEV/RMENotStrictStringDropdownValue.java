package fn10.bedrockrmobile.activity.components.RMEV;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.AppCompatEditText;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import fn10.bedrockr.addons.element.FieldFilters;
import fn10.bedrockr.utils.RAnnotation;
import fn10.bedrockrmobile.R;
import fn10.bedrockrmobile.activity.components.RMElementValue;

@SuppressLint("ViewConstructor")
public class RMENotStrictStringDropdownValue extends RMElementValue<String, AppCompatAutoCompleteTextView> {

    private FieldFilters.FieldFilter filter = new FieldFilters.RegularStringFilter();

    public RMENotStrictStringDropdownValue(Context context, RAnnotation.FieldDetails details, @Nullable Field field) {
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
    public AppCompatAutoCompleteTextView getInput(Context context) {
        RAnnotation.StringDropdownField anno = null;
        Field field = getField();
        if (field != null) {
            anno = field.getAnnotation(RAnnotation.StringDropdownField.class);
        }
        if (anno == null) return null;
        AppCompatAutoCompleteTextView building = new AppCompatAutoCompleteTextView(context);
        building.setThreshold(0);
        building.setDropDownBackgroundResource(R.color.background);
        building.setOnFocusChangeListener((v,h) -> {
            if (h)
                building.showDropDown();
        });
        building.setAdapter(new ArrayAdapter<>(context, R.layout.rspinneritem, R.id.itemText, anno.value()));
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
