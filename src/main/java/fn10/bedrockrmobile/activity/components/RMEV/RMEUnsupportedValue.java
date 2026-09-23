package fn10.bedrockrmobile.activity.components.RMEV;

import android.content.Context;
import android.widget.TextView;

import fn10.bedrockrmobile.R;
import fn10.bedrockrmobile.activity.components.RMElementValue;

public class RMEUnsupportedValue extends RMElementValue<Void, TextView> {
    public RMEUnsupportedValue(Context context) {
        super(context);
    }

    @Override
    protected int getView() {
        return R.layout.rev_unsupported;
    }

    @Override
    public void setValue(Void val) {

    }

    @Override
    public Void getValue() {
        return null;
    }

    @Override
    public boolean valid(boolean strict) {
        return true;
    }
}
