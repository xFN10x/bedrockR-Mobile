package fn10.bedrockrmobile.elements;

import android.view.View;

import java.lang.reflect.Field;
import java.util.Map;

import fn10.bedrockr.addons.element.interfaces.ElementSource;
import fn10.bedrockrmobile.activity.components.RMElementValue;

public interface RMElementCreationHandler<S extends ElementSource<?>> {
    S createElement(Map<Field, RMElementValue<?,?>> fieldViews);
}
