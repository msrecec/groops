package hr.tvz.groops.util;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.*;
import hr.tvz.groops.command.searchPaginated.BaseEntitySearchCommand;
import hr.tvz.groops.exception.ExceptionEnum;
import hr.tvz.groops.exception.InternalServerException;
import hr.tvz.groops.model.QBaseEntity;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class QueryBuilderUtil {

    public static void like(BooleanBuilder builder, StringPath path, String value) {
        if (value != null) {
            builder.and(path.containsIgnoreCase(value.trim()));
        } else {
            builder.and(path.isNull());
        }
    }

    public static void like(BooleanBuilder builder, EnumPath path, String value) {
        if (value != null) {
            builder.and(path.stringValue().containsIgnoreCase(value.trim()));
        } else {
            builder.and(path.isNull());
        }
    }

    public static void equals(BooleanBuilder builder, StringPath path, String value) {
        if (value != null) {
            builder.and(path.equalsIgnoreCase(value.trim()));
        } else {
            builder.and(path.isNull());
        }
    }

    public static void equals(BooleanBuilder builder, EnumPath path, String value) {
        if (value != null) {
            builder.and(path.stringValue().equalsIgnoreCase(value.trim()));
        } else {
            builder.and(path.isNull());
        }
    }

    public static void equals(BooleanBuilder builder, NumberPath<Integer> path, Integer value) {
        if (value != null) {
            builder.and(path.eq(value));
        } else {
            builder.and(path.isNull());
        }
    }

    public static void equals(BooleanBuilder builder, NumberPath<Long> path, Long value) {
        if (value != null) {
            builder.and(path.eq(value));
        } else {
            builder.and(path.isNull());
        }
    }

    public static void equals(BooleanBuilder builder, BooleanPath path, Boolean value) {
        if (value != null) {
            builder.and(path.eq(value));
        } else {
            builder.and(path.isNull());
        }
    }

    public static void gte(BooleanBuilder builder, NumberPath<BigDecimal> path, BigDecimal value) {
        if (value != null) {
            builder.and(path.goe(value));
        }
    }

    public static void lte(BooleanBuilder builder, NumberPath<BigDecimal> path, BigDecimal value) {
        if (value != null) {
            builder.and(path.loe(value));
        }
    }

    public static void gte(BooleanBuilder builder, DateTimePath path, Instant value) {
        if (value != null) {
            builder.and(path.goe(value));
        }
    }

    public static void lte(BooleanBuilder builder, DateTimePath path, Instant value) {
        if (value != null) {
            builder.and(path.loe(value));
        }
    }

    public static void gte(BooleanBuilder builder, DatePath path, Date value) {
        if (value != null) {
            builder.and(path.goe(value));
        }
    }

    public static void lte(BooleanBuilder builder, DatePath path, Date value) {
        if (value != null) {
            builder.and(path.loe(value));
        }
    }

    public static void gte(BooleanBuilder builder, NumberPath<Integer> path, Integer value) {
        if (value != null) {
            builder.and(path.goe(value));
        }
    }

    public static void lte(BooleanBuilder builder, NumberPath<Integer> path, Integer value) {
        if (value != null) {
            builder.and(path.loe(value));
        }
    }

    // ------------------------------------------------------------------------------------------------

    public static <T extends Enum<T>> void exactEquals(BooleanBuilder builder, EnumPath<?> path, Optional<T> value) {
        if (isSet(value)) {
            if (value.isPresent()) {
                builder.and(path.stringValue().eq(value.map(Enum::toString).orElse(null)));
            } else {
                builder.and(path.isNull());
            }
        }
    }

    public static <T extends Enum<T>> void exactMatches(BooleanBuilder builder, EnumPath<?> path, Optional<List<T>> values) {
        if (isSet(values) && !getOrNull(values).isEmpty()) {
            BooleanBuilder builderTemp = new BooleanBuilder();
            for (T val : getOrNull(values)) {
                if (val == null) {
                    builderTemp.or(path.isNull());
                    continue;
                }
                builderTemp.or(path.stringValue().eq(val.toString()));
            }
            builder.and(builderTemp);
        }
    }

    public static void in(BooleanBuilder builder, StringPath path, Optional<List<String>> serials) {
        if (isSet(serials) && serials.isPresent() && !serials.get().isEmpty()) {
            builder.and(path.toLowerCase().in(serials.get().stream().map(s -> s.toLowerCase(Locale.ROOT)).collect(Collectors.toList())));
        }
    }

    public static void notIn(BooleanBuilder builder, StringPath path, String... values) {
        if (values.length != 0) {
            builder.and(path.notIn(values));
        }
    }

    public static void like(BooleanBuilder builder, StringPath path, Optional<String> value) {
        if (isSet(value)) {
            if (value.isPresent()) {
                builder.and(path.containsIgnoreCase(value.map(String::trim).orElse(null)));
            } else {
                builder.and(path.isNull());
            }
        }
    }

    public static <T extends Enum<T>> void like(BooleanBuilder builder, EnumPath path, Optional<T> value) {
        if (isSet(value)) {
            if (value.isPresent()) {
                builder.and(path.stringValue().containsIgnoreCase(value.map(Enum::toString).orElse(null)));
            } else {
                builder.and(path.isNull());
            }
        }
    }

    public static <T extends Enum<T>> void equalsEnum(BooleanBuilder builder, EnumPath path, Optional<T> value) {
        if (isSet(value)) {
            if (value.isPresent()) {
                builder.and(path.stringValue().eq(value.map(Enum::toString).orElse(null)));
            } else {
                builder.and(path.isNull());
            }
        }
    }

    public static void equals(BooleanBuilder builder, BooleanPath path, Optional<Boolean> value) {
        if (isSet(value)) {
            if (value.isPresent()) {
                builder.and(path.eq(value.orElse(null)));
            } else {
                builder.and(path.isNull());
            }
        }
    }

    public static void equalsAndNotNull(BooleanBuilder builder, BooleanPath path, Optional<Boolean> value) {
        if (isSet(value)) {
            if (value.isPresent()) {
                builder.and(path.eq(value.orElse(null)));
            }
        }
    }

    public static void equals(BooleanBuilder builder, StringPath path, Optional<String> value) {
        if (isSet(value)) {
            if (value.isPresent()) {
                builder.and(path.eq(value.map(String::trim).orElse(null)));
            } else {
                builder.and(path.isNull());
            }
        }
    }

    public static void equals(BooleanBuilder builder, EnumPath path, Optional<String> value) {
        if (isSet(value)) {
            if (value.isPresent()) {
                builder.and(path.stringValue().equalsIgnoreCase(value.map(String::trim).orElse(null)));
            } else {
                builder.and(path.isNull());
            }
        }
    }

    public static <T extends Number & Comparable<?>> void equals(BooleanBuilder builder, NumberPath<T> path, Optional<T> value) {
        if (isSet(value)) {
            if (value.isPresent()) {
                builder.and(path.eq(value.orElse(null)));
            } else {
                builder.and(path.isNull());
            }
        }
    }

    public static <T extends Number & Comparable<?>> void gte(BooleanBuilder builder, NumberPath<T> path, Optional<T> value) {
        if (isSet(value) && value.isPresent()) {
            builder.and(path.goe(value.orElse(null)));
        }
    }

    public static <T extends Number & Comparable<?>> void lte(BooleanBuilder builder, NumberPath<T> path, Optional<T> value) {
        if (isSet(value) && value.isPresent()) {
            builder.and(path.loe(value.orElse(null)));
        }
    }

    public static void gte(BooleanBuilder builder, DateTimePath<Instant> path, Optional<Instant> value) {
        if (isSet(value) && value.isPresent()) {
            builder.and(path.goe(value.orElse(null)));
        }
    }

    public static void lte(BooleanBuilder builder, DateTimePath<Instant> path, Optional<Instant> value) {
        if (isSet(value) && value.isPresent()) {
            builder.and(path.loe(value.orElse(null)));
        }
    }

    public static void gte(BooleanBuilder builder, DatePath<java.sql.Date> path, Optional<java.sql.Date> value) {
        if (isSet(value) && value.isPresent()) {
            builder.and(path.goe(value.orElse(null)));
        }
    }

    public static void lte(BooleanBuilder builder, DatePath<java.sql.Date> path, Optional<java.sql.Date> value) {
        if (isSet(value) && value.isPresent()) {
            builder.and(path.loe(value.orElse(null)));
        }
    }

    public static void isNotNull(BooleanBuilder builder, NumberPath<Long> path, Optional<Boolean> value) {
        if (isSet(value)) {
            if (value.isPresent() && value.get()) {
                builder.and(path.isNotNull());
            } else {
                builder.and(path.isNull());
            }
        }
    }

    // ------------------------------------------------------------------------------------------------

    public static void like(Map<String, Object> form, StringPath path, BooleanBuilder builder, String key) {
        if (form != null && form.containsKey(key)) {
            if (form.get(key) != null) {
                QueryBuilderUtil.like(builder, path, (String) form.get(key));
            } else {
                builder.and(path.isNull());
            }
        }
    }

    public static void equals(Map<String, Object> form, StringPath path, BooleanBuilder builder, String key) {
        if (form != null && form.containsKey(key)) {
            if (form.get(key) != null) {
                QueryBuilderUtil.equals(builder, path, (String) form.get(key));
            } else {
                builder.and(path.isNull());
            }
        }
    }

    public static void equals(Map<String, Object> form, EnumPath path, BooleanBuilder builder, String key) {
        if (form != null && form.containsKey(key)) {
            if (form.get(key) != null) {
                QueryBuilderUtil.equals(builder, path, (String) form.get(key));
            } else {
                builder.and(path.isNull());
            }
        }
    }

    public static void like(Map<String, Object> form, EnumPath path, BooleanBuilder builder, String key) {
        if (form != null && form.containsKey(key)) {
            if (form.get(key) != null) {
                QueryBuilderUtil.like(builder, path, (String) form.get(key));
            } else {
                builder.and(path.isNull());
            }
        }
    }

    public static <T extends Number & Comparable<?>, U> void equals(Map<String, Object> form, NumberPath<T> path, BooleanBuilder builder, String key) {
        if (form != null && form.containsKey(key)) {
            if (form.get(key) != null) {
                if (path.getType().equals(Long.class)) {
                    equals(builder, (NumberPath<Long>) path, Long.valueOf((Integer) form.get(key)));
                }
                if (path.getType().equals(Integer.class)) {
                    equals(builder, (NumberPath<Integer>) path, (Integer) form.get(key));
                }
            } else {
                builder.and(path.isNull());
            }
        }
    }

    public static void equals(Map<String, Object> form, BooleanPath path, BooleanBuilder builder, String key) {
        if (form != null && form.containsKey(key)) {
            if (form.get(key) != null) {
                equals(builder, path, (Boolean) form.get(key));
            } else {
                builder.and(path.isNull());
            }
        }
    }

    public static void gteBD(Map<String, Object> form, NumberPath<BigDecimal> path, BooleanBuilder builder, String key) {
        if (form != null && form.containsKey(key)) {
            if (form.get(key) != null) {
                if (!(form.get(key) instanceof String)) {
                    throw new IllegalArgumentException("Input value for " + key + " must be a numeric string");
                }
                gte(builder, path, new BigDecimal((String) form.get(key)));
            } else {
                builder.and(path.isNull());
            }
        }
    }

    public static void gteDate(Map<String, Object> form, DatePath path, BooleanBuilder builder, String key) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            if (form != null && form.containsKey(key)) {
                if (form.get(key) != null) {
                    gte(builder, path, formatter.parse((String) form.get(key)));
                } else {
                    builder.and(path.isNotNull());
                }
            }
        } catch (ParseException ex) {
            throw new InternalServerException(ExceptionEnum.PARSE_EXCEPTION.getFullMessage(), ex);
        }
    }

    public static void gteInstant(Map<String, Object> form, DateTimePath path, BooleanBuilder builder, String key) {
        if (form != null && form.containsKey(key)) {
            if (form.get(key) != null) {
                gte(builder, path, Instant.parse((String) form.get(key)));
            } else {
                builder.and(path.isNotNull());
            }
        }
    }

    public static void lteInstant(Map<String, Object> form, DateTimePath path, BooleanBuilder builder, String key) {
        if (form != null && form.containsKey(key)) {
            if (form.get(key) != null) {
                lte(builder, path, Instant.parse((String) form.get(key)));
            } else {
                builder.and(path.isNotNull());
            }
        }
    }

    public static void lteDate(Map<String, Object> form, DatePath path, BooleanBuilder builder, String key) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        if (form != null && form.containsKey(key)) {
            if (form.get(key) != null) {
                lte(builder, path, formatter.parse((String) form.get(key)));
            } else {
                builder.and(path.isNotNull());
            }
        }
    }

    public static void lteBD(Map<String, Object> form, NumberPath<BigDecimal> path, BooleanBuilder builder, String key) {
        if (form != null && form.containsKey(key)) {
            if (form.get(key) != null) {
                lte(builder, path, new BigDecimal((String) form.get(key)));
            } else {
                builder.and(path.isNull());
            }
        }
    }

    public static void gteInt(Map<String, Object> form, NumberPath<Integer> path, BooleanBuilder builder, String key) {
        if (form != null && form.containsKey(key)) {
            if (form.get(key) != null) {
                gte(builder, path, (Integer) form.get(key));
            } else {
                builder.and(path.isNull());
            }
        }
    }

    public static void lteInt(Map<String, Object> form, NumberPath<Integer> path, BooleanBuilder builder, String key) {
        if (form != null && form.containsKey(key)) {
            if (form.get(key) != null) {
                lte(builder, path, (Integer) form.get(key));
            } else {
                builder.and(path.isNull());
            }
        }
    }

    public static void isNotNull(Map<String, Object> form, NumberPath<Long> path, BooleanBuilder builder, String key) {
        if (form != null) {
            if (form.containsKey(key)) {
                if ((Boolean) form.get(key)) {
                    builder.and(path.isNotNull());
                } else {
                    builder.and(path.isNull());
                }
            }
        }
    }

    public static void buildCreatedModifiedAndIdConditions(BaseEntitySearchCommand command, QBaseEntity baseEntity, NumberPath<Long> id, BooleanBuilder builder) {
        if (command != null) {
            if (isSet(command.getId())) {
                equals(builder, id, command.getId());
            }
            if (isSet(command.getCreatedTsFrom())) {
                gte(builder, baseEntity.createdTs, command.getCreatedTsFrom());
            }
            if (isSet(command.getCreatedTsTo())) {
                lte(builder, baseEntity.createdTs, command.getCreatedTsTo());
            }
            if (isSet(command.getModifiedTsFrom())) {
                gte(builder, baseEntity.modifiedTs, command.getModifiedTsFrom());
            }
            if (isSet(command.getModifiedTsTo())) {
                lte(builder, baseEntity.modifiedTs, command.getModifiedTsTo());
            }
            if (isSet(command.getCreatedBy())) {
                like(builder, baseEntity.createdBy, command.getCreatedBy());
            }
            if (isSet(command.getModifiedBy())) {
                like(builder, baseEntity.modifiedBy, command.getModifiedBy());
            }
        }
    }

    public static void buildCreatedModifiedAndIdConditions(Map<String, Object> form, QBaseEntity baseEntity, BooleanBuilder builder) {
        if (form != null) {
            if (form.get("createdTsFrom") != null) {
                gte(builder, baseEntity.createdTs, Instant.parse((String) form.get("createdTsFrom")));
            }
            if (form.get("createdTsTo") != null) {
                lte(builder, baseEntity.createdTs, Instant.parse((String) form.get("createdTsTo")));
            }
            if (form.get("modifiedTsFrom") != null) {
                gte(builder, baseEntity.createdTs, Instant.parse((String) form.get("modifiedTsFrom")));
            }
            if (form.get("modifiedTsTo") != null) {
                lte(builder, baseEntity.createdTs, Instant.parse((String) form.get("modifiedTsTo")));
            }
            if (form.get("getCreatedBy") != null) {
                like(builder, baseEntity.createdBy, (String) form.get("getCreatedBy"));
            }
            if (form.get("getModifiedBy") != null) {
                like(builder, baseEntity.modifiedBy, (String) form.get("getModifiedBy"));
            }
            if (form.get("createdBy") != null) {
                like(builder, baseEntity.createdBy, (String) form.get("createdBy"));
            }
            if (form.get("modifiedBy") != null) {
                like(builder, baseEntity.modifiedBy, (String) form.get("modifiedBy"));
            }
        }
    }

    private static boolean isSet(Optional<?> val) {
        return val != null;
    }

    private static <T> T getOrNull(Optional<T> value) {
        return value.orElse(null);
    }

    public static <T extends Number & Comparable<T>> BooleanExpression eqOrNull(NumberPath<T> path, T searchVal) {
        if (searchVal == null) {
            return path.isNull();
        }
        return path.eq(searchVal);
    }
}
