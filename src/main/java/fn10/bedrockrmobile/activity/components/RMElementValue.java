package fn10.bedrockrmobile.activity.components;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public abstract class RMElementValue<T> extends LinearLayout {
    public RMElementValue(Context context) {
        super(context);
        inflate(context, getView(), null);
    }

    protected abstract int getView();
    public abstract void setValue(T val);
    public abstract T getValue();
    public abstract boolean valid(boolean strict);
    public boolean valid() {
        return valid(true);
    }


}
