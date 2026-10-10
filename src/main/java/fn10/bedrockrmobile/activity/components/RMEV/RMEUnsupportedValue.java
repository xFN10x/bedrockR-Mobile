package fn10.bedrockrmobile.activity.components.RMEV;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatTextView;

import fn10.bedrockr.utils.RAnnotation;
import fn10.bedrockrmobile.R;
import fn10.bedrockrmobile.activity.components.RMElementValue;

@SuppressLint("ViewConstructor")
public class RMEUnsupportedValue<N> extends RMElementValue<N, AppCompatTextView> {
    public RMEUnsupportedValue(Context context, Class<N> cls, RAnnotation.FieldDetails details) {
        super(context, cls, details);
    }

    @Override
    public AppCompatTextView getInput(Context context) {
        AppCompatTextView building = new AppCompatTextView(context);
        building.setText(getContext().getString(R.string.unsupported_value_text).formatted(getType()));
        return building;
    }

    @Override
    public void setValue(N val) {

    }

    @Override
    public N getValue() {
        return null;
    }

    @Override
    public boolean valid(boolean strict) {
        return true;
    }
}
