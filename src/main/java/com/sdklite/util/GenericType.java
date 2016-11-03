package com.sdklite.util;

import java.lang.reflect.Type;

/**
 * <p>This class is used to obtain the generic type parameter</p>
 * 
 * <pre>
 * Type listOfString = new GenericType&lt;List&lt;String&gt;&gt;(){}.getType();
 * </pre>
 * 
 * <pre>
 * Type stringToObjectMap = new GenericType&lt;Map&lt;String, Object&gt;&gt;(){}.getType();
 * </pre>
 * 
 * @author johnsonlee
 *
 * @param <T>
 *            The generic type parameter
 */
public abstract class GenericType<T> {

    private final Type type;

    /**
     * Default constructor
     */
    protected GenericType() {
        this.type = TypeResolver.getSuperclassTypeParameter(this);
    }

    /**
     * Returns the generic type parameter
     */
    public Type getType() {
        return this.type;
    }

}
