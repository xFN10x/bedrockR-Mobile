package fn10.bedrockrmobile.activity.creationscreen;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import fn10.bedrockr.addons.source.SourceItemElement;
import fn10.bedrockr.addons.source.interfaces.ElementFile;
import fn10.bedrockr.addons.source.interfaces.ElementSource;
import fn10.bedrockr.interfaces.ElementCreationListener;

public abstract class RMElementCreationScreen<T extends ElementSource<? extends ElementFile<T>>> extends AppCompatActivity {

    public static ElementCreationListener creationListener;

    //abstract ElementFile<T> getFile();

    private static final Map<Class<? extends ElementSource<?>>, Class<? extends RMElementCreationScreen<?>>>  CREATION_SCREENS = Map.of(
            SourceItemElement.class, ItemElementCreationScreen.class
    );
    public static <E extends ElementSource<? extends ElementFile<E>>> Class<? extends RMElementCreationScreen<?>> getCreationScreenFromElementFile(ElementFile<E> file, ElementCreationListener ecl) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        return  getCreationScreenFromElementSourceClass(file.getSourceClass(), ecl);
    }

    public static <E extends ElementSource<? extends ElementFile<E>>> Class<? extends RMElementCreationScreen<?>> getCreationScreenFromElementSourceClass(Class<? extends  ElementSource<?>> srcClass, ElementCreationListener ecl) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        if (CREATION_SCREENS.containsKey(srcClass)) {
            return CREATION_SCREENS.get(srcClass);
        } else {
            return null;
        }
    }
}
