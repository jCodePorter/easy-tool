package cn.augrain.easy.tool.bean;

import cn.augrain.easy.tool.core.ObjectUtils;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.Type;
import ma.glasnost.orika.metadata.TypeFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 对 {@link MapperFacade} 的简单封装
 *
 * @author biaoy
 * @since 2025/06/07
 */
public class BeanMapper {

    private static final MapperFacade mapper;

    static {
        MapperFactory mapperFactory = new DefaultMapperFactory
                .Builder()
                .mapNulls(false)
                .build();
        mapper = mapperFactory.getMapperFacade();
    }

    /**
     * 简单的复制出新类型对象.
     * <p>
     * 通过source.getClass() 获得源Class
     */
    public static <S, D> D map(S source, Class<D> destinationClass) {
        return mapper.map(source, destinationClass);
    }

    /**
     * 简单的复制出新类型对象.
     * <p>
     * 通过source.getClass() 获得源Class
     */
    public static <S, D> void map(S source, D d) {
        mapper.map(source, d);
    }

    /**
     * 简单的复制出新类型对象.
     * 指定待复制的class
     */
    public static <S, D> void map(S source, D d, Class<S> sClass, Class<D> dClass) {
        mapper.map(source, d, TypeFactory.valueOf(sClass), TypeFactory.valueOf(dClass));
    }

    /**
     * 极致性能的复制出新类型对象.
     * <p>
     * 预先通过BeanMapper.getType() 静态获取并缓存Type类型，在此处传入
     */
    public static <S, D> D map(S source, Type<S> sourceType, Type<D> destinationType) {
        return mapper.map(source, sourceType, destinationType);
    }

    /**
     * 对象复制
     */
    public static <S, T> T map(S source, Class<T> destinationClass, BeanMappingHandler<S, T> handler) {
        return handler.map(source, destinationClass);
    }

    /**
     * 简单的复制出新对象列表到ArrayList
     * <p>
     * 不建议使用mapper.mapAsList(Iterable<S>,Class<D>)接口, sourceClass需要反射，实在有点慢
     */
    public static <S, D> List<D> mapList(Iterable<S> sourceList, Class<S> sourceClass, Class<D> destinationClass) {
        return mapper.mapAsList(sourceList, TypeFactory.valueOf(sourceClass), TypeFactory.valueOf(destinationClass));
    }

    /**
     * 极致性能的复制出新类型对象到ArrayList.
     * <p>
     * 预先通过BeanMapper.getType() 静态获取并缓存Type类型，在此处传入
     */
    public static <S, D> List<D> mapList(Iterable<S> sourceList, Type<S> sourceType, Type<D> destinationType) {
        return mapper.mapAsList(sourceList, sourceType, destinationType);
    }

    /**
     * 简单的复制出新对象列表到ArrayList
     */
    public static <T> List<T> mapList(List sourceList, Class<T> destinationClass) {
        if (ObjectUtils.isEmpty(sourceList)) {
            return Collections.emptyList();
        }
        List<T> destinationList = new ArrayList<>();
        for (Object sourceObject : sourceList) {
            T destinationObject = mapper.map(sourceObject, destinationClass);
            destinationList.add(destinationObject);
        }
        return destinationList;
    }

    /**
     * 极致性能的复制出新类型对象到ArrayList.
     */
    public static <S, T> List<T> mapList(List<S> sourceList, Class<T> destinationClass, BeanMappingHandler<S, T> handler) {
        if (ObjectUtils.isEmpty(sourceList)) {
            return Collections.emptyList();
        }
        List<T> destinationList = new ArrayList<>();
        for (S sourceObject : sourceList) {
            T destinationObject = handler.map(sourceObject, destinationClass);
            destinationList.add(destinationObject);
        }
        return destinationList;
    }


    /**
     * 简单复制出新对象列表到数组
     * <p>
     * 通过source.getComponentType() 获得源Class
     */
    public static <S, D> D[] mapArray(final D[] destination, final S[] source, final Class<D> destinationClass) {
        return mapper.mapAsArray(destination, source, destinationClass);
    }

    /**
     * 极致性能的复制出新类型对象到数组
     * <p>
     * 预先通过BeanMapper.getType() 静态获取并缓存Type类型，在此处传入
     */
    public static <S, D> D[] mapArray(D[] destination, S[] source, Type<S> sourceType, Type<D> destinationType) {
        return mapper.mapAsArray(destination, source, sourceType, destinationType);
    }

    /**
     * 预先获取orika转换所需要的Type，避免每次转换.
     */
    public static <E> Type<E> getType(final Class<E> rawType) {
        return TypeFactory.valueOf(rawType);
    }

}
