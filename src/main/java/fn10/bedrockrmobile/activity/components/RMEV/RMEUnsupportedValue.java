package fn10.bedrockrmobile.activity.components.RMEV;

import android.content.Context;
import android.widget.TextView;

import fn10.bedrockrmobile.R;
import fn10.bedrockrmobile.activity.components.RMElementValue;

public class RMEUnsupportedValue<N> extends RMElementValue<N, TextView> {
    public RMEUnsupportedValue(Context context) {
        super(context);
    }

    @Override
    public TextView getInput(Context context) {
        TextView building = new TextView(context);
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
