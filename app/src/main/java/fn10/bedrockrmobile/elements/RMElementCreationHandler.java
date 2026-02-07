package fn10.bedrockrmobile.elements;

import android.view.View;

import java.lang.reflect.Field;
import java.util.Map;

import fn10.bedrockr.addons.source.interfaces.ElementSource;

public interface RMElementCreationHandler<S extends ElementSource<?>> {
    S createElement(Map<Field, View> fieldViews);
}
