package hr.tvz.groops.util;

import hr.tvz.groops.exception.ExceptionEnum;
import hr.tvz.groops.exception.InternalServerException;
import hr.tvz.groops.model.BaseEntity;
import javax.persistence.Entity;
import org.modelmapper.ModelMapper;
import org.springframework.beans.MethodInvocationException;
import org.springframework.beans.PropertyAccessor;
import org.springframework.beans.PropertyAccessorFactory;

import java.lang.reflect.Field;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class MapUtil extends ReflectionUtil {

    public static <T, C> C commandToEntity(T command, C entity) {
        try {
            Class<?> commandClass = command.getClass();
            Class<?> entityClass = entity.getClass();
            Class<?> optionalClass = Optional.class;
            Class<?> objectClass = Object.class;
            if (!entityClass.isAnnotationPresent(Entity.class)) {
                throw new IllegalArgumentException("Can't map to non Entity classes");
            }
            if (!commandClass.getSuperclass().equals(objectClass)) {
                throw new IllegalArgumentException("Command class must not extend any class other than " + objectClass);
            }
            List<Field> entityFields = getDeclaredFieldsFromClass(entity.getClass(), BaseEntity.class);
            PropertyAccessor myAccessor = PropertyAccessorFactory.forBeanPropertyAccess(entity);

            for (Field entityField : entityFields) {
                if (isAny(entityField)) {
                    continue;
                }
                Field commandField;
                try {
                    commandField = commandClass.getDeclaredField(entityField.getName());
                } catch (NoSuchFieldException ex) {
                    continue;
                }
                if (!commandField.getType().equals(optionalClass)) {
                    throw new IllegalArgumentException("Fields in class  " + commandClass.getName() + " must be of type " + optionalClass.getName());
                }
                commandField.setAccessible(true);
                entityField.setAccessible(true);
                if (!isSet(commandField, command)) {
                    continue;
                }
                Object value;
                try {
                    value = getValue(commandField, command);
                } catch (NoSuchElementException e) {
                    value = null;
                }
                if (value != null && !entityField.getType().equals(value.getClass())) {
                    throw new IllegalArgumentException("Value of type " + value.getClass() + " doesn't correspond to entity of type " + entityField.getType());
                }
                myAccessor.setPropertyValue(entityField.getName(), value);
            }
            return entity;
        } catch (MethodInvocationException | IllegalAccessException ex) {
            if (ex instanceof MethodInvocationException && ex.getCause() instanceof RuntimeException) {
                throw new InternalServerException(ExceptionEnum.INTERNAL_SERVER_EXCEPTION.getFullMessage(), ex.getCause());
            }
            throw new InternalServerException(ExceptionEnum.INTERNAL_SERVER_EXCEPTION.getFullMessage(), ex);
        }
    }


    public static <T> int getTotalNonNullOptionalsInCommand(T command) {
        try {
            Class<?> commandClass = command.getClass();
            Class<?> objectClass = Object.class;
            Class<?> optionalClass = Optional.class;
            int setFieldsCount = 0;
            if (commandClass.isAnnotationPresent(Entity.class)) {
                throw new IllegalArgumentException("Can't check Entity classes");
            }
            if (!commandClass.getSuperclass().equals(objectClass)) {
                throw new IllegalArgumentException("Command class must not extend any class other than " + objectClass);
            }
            for (Field declaredField : getDeclaredFieldsFromClass(commandClass)) {
                if (isAny(declaredField)) {
                    continue;
                }
                Field field;
                try {
                    field = commandClass.getDeclaredField(declaredField.getName());
                } catch (NoSuchFieldException ex) {
                    continue;
                }
                if (!field.getType().equals(optionalClass)) {
                    throw new IllegalArgumentException("Fields in class  " + commandClass.getName() + " must be of type " + optionalClass.getName());
                }
                field.setAccessible(true);
                declaredField.setAccessible(true);
                if (!isSet(field, command)) {
                    continue;
                }
                ++setFieldsCount;
                Object value;
                try {
                    value = getValue(field, command);
                } catch (NoSuchElementException e) {
                    value = null;
                }
                if (value != null && !declaredField.getType().equals(optionalClass)) {
                    throw new IllegalArgumentException("Value of type " + value.getClass() + " doesn't correspond to entity of type " + declaredField.getType());
                }
            }
            return setFieldsCount;
        } catch (MethodInvocationException | IllegalAccessException ex) {
            if (ex instanceof MethodInvocationException && ex.getCause() instanceof RuntimeException) {
                throw new InternalServerException(ExceptionEnum.INTERNAL_SERVER_EXCEPTION.getFullMessage(), ex.getCause());
            }
            throw new InternalServerException(ExceptionEnum.INTERNAL_SERVER_EXCEPTION.getFullMessage(), ex);
        }
    }

    private static <T> boolean isSet(Field commandField, T commandObject) throws IllegalAccessException {
        return commandField.get(commandObject) != null;
    }

    private static <T> Object getValue(Field commandField, T commandObject) throws IllegalAccessException {
        Object attribute = commandField.get(commandObject);
        if (!(attribute instanceof Optional)) {
            throw new IllegalArgumentException("Attribute in command class must be of type " + Optional.class.getName());
        }
        return ((Optional<?>) attribute).orElse(null);
    }

    private static boolean isAny(Field entityField) {
        return isId(entityField) ||
                isManyToMany(entityField) ||
                isManyToOne(entityField) ||
                isOneToMany(entityField) ||
                isTransient(entityField) ||
                isEmbedded(entityField);
    }

    public static <T, C> C mapOrNull(T value, Class<C> clazz, ModelMapper mapper) {
        return value != null ? mapper.map(value, clazz) : null;
    }
}
