package pinacolada.misc;

import pinacolada.resources.PCLResources;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class PCLGenericData<T> {
    public final PCLResources<?, ?, ?, ?> resources;
    private Constructor<? extends T> constructor;
    public String ID;
    public Class<? extends T> invokeClass;

    public PCLGenericData(String id, Class<? extends T> invokeClass, PCLResources<?, ?, ?, ?> resources) {
        this.ID = id;
        this.invokeClass = invokeClass;
        this.resources = resources;
    }

    public T create() {
        try {
            if (constructor == null) {
                constructor = invokeClass.getConstructor();
                constructor.setAccessible(true);
            }

            return constructor.newInstance();
        }
        catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException(ID, e);
        }
    }
}