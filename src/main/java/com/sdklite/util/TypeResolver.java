package com.sdklite.util;

import java.io.Serializable;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Arrays;

/**
 * Type resolver is used for generic type resolving
 * 
 * @author johnsonlee
 *
 */
public abstract class TypeResolver {

    private static final Type[] EMPTY_TYPE_ARRAY = new Type[] {};

    public static Type getGenericInterfaceTypeParameter(final Object o) {
        return getGenericInterfaceTypeParameter(o.getClass());
    }

    public static Type getGenericInterfaceTypeParameter(final Class<?> clazz) {
        final Type[] interfaces = clazz.getGenericInterfaces();
        if (null == interfaces || interfaces.length <= 0) {
            throw new IllegalArgumentException("Missing generic interface");
        }
        return getTypeParameter(interfaces[0]);
    }

    public static Type getSuperclassTypeParameter(final Object o) {
        return getSuperclassTypeParameter(o.getClass());
    }

    public static Type getSuperclassTypeParameter(final Class<?> clazz) {
        final Type superclass = clazz.getGenericSuperclass();
        if (superclass instanceof Class) {
            throw new RuntimeException("Missing type parameter");
        }
        return getTypeParameter(superclass);
    }

    public static Type getGenericTypeParameter(final Object o) {
        return getGenericTypeParameter(o.getClass());
    }

    public static Type getGenericTypeParameter(final Class<?> clazz) {
        Type type = null;

        try {
            type = getGenericInterfaceTypeParameter(clazz);
        } catch (final Exception e1) {
            try {
                type = getSuperclassTypeParameter(clazz);
            } catch (Exception e2) {
                throw new IllegalArgumentException("Missing generic type parameter");
            }
        }

        return type;
    }

    private static Type getTypeParameter(final Type superclass) {
        final ParameterizedType parameterized = (ParameterizedType) superclass;
        return canonicalize(parameterized.getActualTypeArguments()[0]);
    }

    /**
     * Returns the canonicalized type of the specified type
     * 
     * @param type
     *            The type to canonicalize
     * @return the canonicalized type
     */
    public static Type canonicalize(final Type type) {
        if (type instanceof Class) {
            final Class<?> c = (Class<?>) type;
            return c.isArray() ? new GenericArrayTypeImpl(canonicalize(c.getComponentType())) : c;

        } else if (type instanceof ParameterizedType) {
            final ParameterizedType p = (ParameterizedType) type;
            return new ParameterizedTypeImpl(p.getOwnerType(), p.getRawType(), p.getActualTypeArguments());

        } else if (type instanceof GenericArrayType) {
            final GenericArrayType g = (GenericArrayType) type;
            return new GenericArrayTypeImpl(g.getGenericComponentType());

        } else if (type instanceof WildcardType) {
            final WildcardType w = (WildcardType) type;
            return new WildcardTypeImpl(w.getUpperBounds(), w.getLowerBounds());

        } else {
            return type;
        }
    }

    /**
     * Returns a string represents the type
     * 
     * @param type
     *            The type instance
     * @return a string represents the type
     */
    public static String typeToString(final Type type) {
        return type instanceof Class ? ((Class<?>) type).getName() : type.toString();
    }

    /**
     * Determine if the two objects are equals
     */
    public static boolean equals(final Object a, final Object b) {
        return a == b || (a != null && a.equals(b));
    }

    private static <T> T checkNotNull(final T obj) {
        if (obj == null) {
            throw new NullPointerException();
        }
        return obj;
    }

    private static void checkNotPrimitive(final Type type) {
        checkArgument(!(type instanceof Class<?>) || !((Class<?>) type).isPrimitive());
    }

    private static void checkArgument(final boolean condition) {
        if (!condition) {
            throw new IllegalArgumentException();
        }
    }

    private static int hashCodeOrZero(final Object o) {
        return o != null ? o.hashCode() : 0;
    }

    @SuppressWarnings("serial")
    private static final class WildcardTypeImpl implements WildcardType, Serializable {

        private final Type upperBound;

        private final Type lowerBound;

        public WildcardTypeImpl(final Type[] upperBounds, final Type[] lowerBounds) {
            checkArgument(lowerBounds.length <= 1);
            checkArgument(upperBounds.length == 1);

            if (lowerBounds.length == 1) {
                checkNotNull(lowerBounds[0]);
                checkNotPrimitive(lowerBounds[0]);
                checkArgument(upperBounds[0] == Object.class);
                this.lowerBound = canonicalize(lowerBounds[0]);
                this.upperBound = Object.class;

            } else {
                checkNotNull(upperBounds[0]);
                checkNotPrimitive(upperBounds[0]);
                this.lowerBound = null;
                this.upperBound = canonicalize(upperBounds[0]);
            }
        }

        public Type[] getUpperBounds() {
            return new Type[] { this.upperBound };
        }

        public Type[] getLowerBounds() {
            return this.lowerBound != null ? new Type[] { this.lowerBound } : EMPTY_TYPE_ARRAY;
        }

        @Override
        public boolean equals(final Object other) {
            return other instanceof WildcardType
                    && TypeResolver.equals(this.lowerBound, ((WildcardType) other).getLowerBounds())
                    && TypeResolver.equals(this.upperBound, ((WildcardType) other).getUpperBounds());
        }

        @Override
        public int hashCode() {
            return (this.lowerBound != null ? 31 + this.lowerBound.hashCode() : 1)
                    ^ (31 + this.upperBound.hashCode());
        }

        @Override
        public String toString() {
            if (this.lowerBound != null) {
                return "? super " + typeToString(this.lowerBound);
            } else if (this.upperBound == Object.class) {
                return "?";
            } else {
                return "? extends " + typeToString(this.upperBound);
            }
        }
    }

    @SuppressWarnings("serial")
    private static final class ParameterizedTypeImpl implements ParameterizedType, Serializable {

        private final Type ownerType;
        private final Type rawType;
        private final Type[] typeArguments;

        public ParameterizedTypeImpl(final Type ownerType, final Type rawType, final Type... typeArguments) {
            if (rawType instanceof Class<?>) {
                final Class<?> rawTypeAsClass = (Class<?>) rawType;
                final boolean isStaticOrTopLevelClass = Modifier.isStatic(rawTypeAsClass.getModifiers()) || rawTypeAsClass.getEnclosingClass() == null;
                checkArgument(ownerType != null || isStaticOrTopLevelClass);
            }

            this.ownerType = ownerType == null ? null : canonicalize(ownerType);
            this.rawType = canonicalize(rawType);
            this.typeArguments = typeArguments.clone();

            for (int t = 0; t < this.typeArguments.length; t++) {
                checkNotNull(this.typeArguments[t]);
                checkNotPrimitive(this.typeArguments[t]);
                this.typeArguments[t] = canonicalize(this.typeArguments[t]);
            }
        }

        public Type[] getActualTypeArguments() {
            return this.typeArguments.clone();
        }

        public Type getRawType() {
            return this.rawType;
        }

        public Type getOwnerType() {
            return this.ownerType;
        }

        @Override
        public boolean equals(final Object other) {
            return other instanceof ParameterizedType
                    && TypeResolver.equals(this.ownerType, ((ParameterizedType) other).getOwnerType())
                    && TypeResolver.equals(this.rawType, ((ParameterizedType) other).getRawType())
                    && Arrays.equals(this.typeArguments, ((ParameterizedType) other).getActualTypeArguments());
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(this.typeArguments) ^ this.rawType.hashCode() ^ hashCodeOrZero(this.ownerType);
        }

        @Override
        public String toString() {
            StringBuilder stringBuilder = new StringBuilder(30 * (this.typeArguments.length + 1));
            stringBuilder.append(typeToString(this.rawType));

            if (this.typeArguments.length == 0) {
                return stringBuilder.toString();
            }

            stringBuilder.append("<").append(typeToString(this.typeArguments[0]));
            for (int i = 1; i < this.typeArguments.length; i++) {
                stringBuilder.append(", ").append(typeToString(this.typeArguments[i]));
            }
            return stringBuilder.append(">").toString();
        }
    }

    @SuppressWarnings("serial")
    private static final class GenericArrayTypeImpl implements GenericArrayType, Serializable {

        private final Type componentType;

        public GenericArrayTypeImpl(final Type componentType) {
            this.componentType = canonicalize(componentType);
        }

        public Type getGenericComponentType() {
            return this.componentType;
        }

        @Override
        public boolean equals(final Object o) {
            return o instanceof GenericArrayType && TypeResolver.equals(this.componentType, ((GenericArrayType) o).getGenericComponentType());
        }

        @Override
        public int hashCode() {
            return this.componentType.hashCode();
        }

        @Override
        public String toString() {
            return typeToString(this.componentType) + "[]";
        }
    }

    private TypeResolver() {
    }
}
