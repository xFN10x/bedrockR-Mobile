package fn10.bedrockrmobile.activity.components.RMEV;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.ArrayAdapter;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSpinner;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;

import fn10.bedrockr.utils.RAnnotation;
import fn10.bedrockrmobile.R;
import fn10.bedrockrmobile.activity.components.RMElementValue;

@SuppressLint("ViewConstructor")
public class RMEStringDropdownValue extends RMElementValue<String, AppCompatSpinner> {


    private String[] array;

    public RMEStringDropdownValue(Context context, RAnnotation.FieldDetails details, @Nullable Field field) {
        super(context, String.class, details, field);
    }

    @Override
    public AppCompatSpinner getInput(Context context) {
        RAnnotation.StringDropdownField anno = null;
        Field field = getField();
        if (field != null) {
            anno = field.getAnnotation(RAnnotation.StringDropdownField.class);
        }
        if (anno == null) return null;
        array = anno.value();
        AppCompatSpinner building = new AppCompatSpinner(context);
        building.setBackgroundResource(R.drawable.rspinnerbg);
        building.setPopupBackgroundResource(R.color.background);
        building.setPadding(40, 10, 40, 10);
        building.setAdapter(new ArrayAdapter<>(context, R.layout.rspinneritem, R.id.itemText, List.of(array)));
        return building;
    }

    @Override
    public void setValue(String val) {
        getInputView().setSelection(List.of(array).indexOf(val));
    }

    @Override
    public String getValue() {
        return getInputView().getSelectedItem().toString();
    }

    @Override
    public boolean valid(boolean strict) {
        return true;
    }
}
