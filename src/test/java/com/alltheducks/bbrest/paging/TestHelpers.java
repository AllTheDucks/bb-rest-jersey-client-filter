package com.alltheducks.bbrest.paging;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class TestHelpers {

    @SafeVarargs
    static <T> PageSource<T> pageSource(Iterable<T>... pages) {
        return (page) -> page < pages.length ? pages[(int)page] : Collections.emptyList();
    }

    static <T> CountingPageSource<T> counting(final PageSource<T> pageSource) {
        return new CountingPageSource<>(pageSource);
    }

    static <T> PageSource<T> delay(long delayMillis, PageSource<T> pageSource) {
        return (page) -> {
            try {
                Thread.sleep(delayMillis);
            } catch (InterruptedException ignored) {
            }

            return pageSource.fetch(page);
        };
    }

    static List<Integer> intRange(int start, int end) {
        return IntStream.rangeClosed(start, end).boxed().collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    static <T> List<T>[] partition(List<T> list, int size) {
        final AtomicInteger counter = new AtomicInteger(0);

        return list.stream()
                .collect(Collectors.groupingBy(it -> counter.getAndIncrement() / size))
                .values().toArray(new List[list.size() / size + 1]);
    }

    static List<TestObject> objects(String... names) {
        return Stream.of(names).map(TestObject::new).collect(Collectors.toList());
    }

    static class TestObject {
        private String name;

        TestObject(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    static class CountingPageSource<T> implements PageSource<T> {

        private long count;

        private final PageSource<T> internalPageSource;

        public CountingPageSource(PageSource<T> internalPageSource) {
            this.internalPageSource = internalPageSource;
        }

        @Override
        public Iterable<T> fetch(long page) {
            count++;
            return internalPageSource.fetch(page);
        }

        public long getCount() {
            return count;
        }
    }

}
