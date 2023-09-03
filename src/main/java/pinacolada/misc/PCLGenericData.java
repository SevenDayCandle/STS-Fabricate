package pinacolada.misc;

import pinacolada.resources.PCLResources;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class PCLGenericData<T> {
    public final PCLResources<?, ?, ?, ?> resources;
    public final Class<? extends T> invokeClass;
    protected Constructor<? extends T> constructor;
    public String ID;

    public PCLGenericData(String id, Class<? extends T> invokeClass, PCLResources<?, ?, ?, ?> resources) {
        this.ID = id;
        this.invokeClass = invokeClass;
        this.resources = resources;
    }

    protected static Integer[] expandArray(Integer[] input, int targetSize) {
        Integer[] newArray = Arrays.copyOf(input, targetSize);
        for (int j = input.length; j < newArray.length; j++) {
            newArray[j] = input[input.length - 1];
        }
        return newArray;
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
