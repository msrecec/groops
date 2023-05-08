package hr.tvz.groops.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

public class ReflectionUtil {

    public static List<Field> getDeclaredFieldsFromClass(Class<?> clazz) {
        return getDeclaredFieldsFromClass(clazz, Object.class);
    }

    public static List<Field> getDeclaredFieldsFromClass(Class<?> clazz, Class<?> delimiter) {
        List<Field> fields = new ArrayList<>();
        for (Class<?> temp = clazz; temp != null && !temp.equals(delimiter) && !temp.equals(Object.class); temp = temp.getSuperclass()) {
            fields.addAll(List.of(temp.getDeclaredFields()));
        }
        return fields;
    }

    public static boolean isTransient(Field field) {
        return field.isAnnotationPresent(Transient.class);
    }

    public static boolean isEmbedded(Field field) {
        return field.isAnnotationPresent(Embedded.class);
    }

    public static boolean isElementCollection(Field field) {
        return field.isAnnotationPresent(ElementCollection.class);
    }

    public static boolean isIdClass(Class<?> clazz) {
        return clazz.isAnnotationPresent(IdClass.class);
    }

    public static boolean isEmbeddedId(Field field) {
        return field.isAnnotationPresent(EmbeddedId.class);
    }

    public static boolean isId(Field field) {
        return field.isAnnotationPresent(Id.class);
    }

    public static boolean isOneToOne(Field field) {
        return field.isAnnotationPresent(OneToOne.class);
    }

    public static boolean isManyToOne(Field field) {
        return field.isAnnotationPresent(ManyToOne.class);
    }

    public static boolean isOneToMany(Field field) {
        return field.isAnnotationPresent(OneToMany.class);
    }

    public static boolean isManyToMany(Field field) {
        return field.isAnnotationPresent(ManyToMany.class);
    }

    public static boolean isEntity(Field field) {
        return field.getType().isAnnotationPresent(Entity.class);
    }

    public static String generateFieldName(Field field) {
        return field.getType().isAnnotationPresent(Entity.class) ? field.getName() + "Id" : field.getName();
    }
}
