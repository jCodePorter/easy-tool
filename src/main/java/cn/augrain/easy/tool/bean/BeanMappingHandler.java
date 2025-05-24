package cn.augrain.easy.tool.bean;

public interface BeanMappingHandler<S, T> {
    T map(S source, Class<T> destinationClass);
}
