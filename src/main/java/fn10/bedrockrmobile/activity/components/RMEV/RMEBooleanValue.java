package fn10.bedrockrmobile.activity.components.RMEV;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.SpinnerAdapter;

import androidx.appcompat.widget.AppCompatSpinner;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import fn10.bedrockr.addons.element.FieldFilters;
import fn10.bedrockr.utils.RAnnotation;
import fn10.bedrockrmobile.R;
import fn10.bedrockrmobile.activity.components.RMElementValue;

@SuppressLint("ViewConstructor")
public class RMEBooleanValue extends RMElementValue<Boolean, AppCompatSpinner> {

    public RMEBooleanValue(Context context, RAnnotation.FieldDetails details) {
        super(context, Boolean.class, details);
    }

    @Override
    public AppCompatSpinner getInput(Context context) {
        AppCompatSpinner building = new AppCompatSpinner(context);
        building.setBackgroundResource(R.drawable.rspinnerbg);
        building.setPadding(40,10,40,10);
        building.setAdapter(new SimpleAdapter(
                context,
                List.of(Map.of("val", false), Map.of("val", true)),
                R.layout.rspinneritem,
                new String[]{"val"},
                new int[]{R.id.itemText}));
        return building;
    }

    @Override
    public void setValue(Boolean val) {
        getInputView().setSelection(val ? 0 : 1);
    }

    @Override
    public Boolean getValue() {
        return getInputView().getSelectedItemPosition() == 0;
    }

    @Override
    public boolean valid(boolean strict) {
        return true;
    }
}
