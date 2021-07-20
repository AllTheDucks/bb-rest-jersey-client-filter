package com.alltheducks.bbrest.paging;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Spliterators;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Spliterator.IMMUTABLE;
import static java.util.Spliterator.NONNULL;

public class PagingStreams {


    public static <T> Stream<T> getStream(final PageSource<T> pageSource) {
        final PagingIterator<T> it = new PagingIterator<>(pageSource);

        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(it, IMMUTABLE & NONNULL), false);
    }

    public static <T> Stream<T> getStream(final Function<Long, Response> request,
                                          final Class<T> entityType) {
        return getStream(request, entityType, PagedResult.class, PagedResult::getResults);
    }

    public static <T> Stream<T> getStream(final Function<Long, Response> request,
                                          final GenericType<T> entityType) {
        return getStream(request, entityType, PagedResult.class, PagedResult::getResults);
    }

    public static <T, U> Stream<T> getStream(final Function<Long, Response> request,
                                             final Class<T> entityType,
                                             final Class<U> pageClass,
                                             final Function<U, Iterable<T>> resultExtractor) {
        GenericType<U> responseType = new GenericType<>(new PagedResultType(entityType, pageClass));
        return getStream(request, responseType, resultExtractor);
    }

    public static <T, U> Stream<T> getStream(final Function<Long, Response> request,
                                             final GenericType<T> entityType,
                                             final Class<U> pageClass,
                                             final Function<U, Iterable<T>> resultExtractor) {
        final GenericType<U> responseType = new GenericType<>(new PagedResultType(entityType.getType(), pageClass));
        return getStream(request, responseType, resultExtractor);
    }

    public static <T, U> Stream<T> getStream(final Function<Long, Response> request,
                                             final GenericType<U> responseType,
                                             final Function<U, Iterable<T>> resultExtractor) {
        final AtomicLong pageCount = new AtomicLong(0);
        return getStream(() -> request.apply(pageCount.getAndIncrement()), responseType, resultExtractor);
    }

    public static <T, U> Stream<T> getStream(final Supplier<Response> request,
                                             final GenericType<U> responseType,
                                             final Function<U, Iterable<T>> resultExtractor) {
        return getStream(() -> {
            final Response response = request.get();

            final U result = response.readEntity(responseType);
            return resultExtractor.apply(result);
        });
    }

    private static class PagedResultType implements ParameterizedType {

        private final Type parameterType;
        private final Class pageClass;

        PagedResultType(final Type parameterType, final Class pageClass) {
            this.parameterType = parameterType;
            this.pageClass = pageClass;
        }

        @Override
        public Type[] getActualTypeArguments() {
            return new Type[]{parameterType};
        }

        @Override
        public Type getRawType() {
            return pageClass;
        }

        @Override
        public Type getOwnerType() {
            return pageClass;
        }
    }

    ;

}
