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
        AtomicInteger pageCount = new AtomicInteger(0);
        return () -> {
            int page = pageCount.getAndIncrement();
            return page < pages.length ? pages[page] : Collections.emptyList();
        };
    }

    static <T> RequestCountingPageSource<T> counting(final PageSource<T> pageSource) {
        return new RequestCountingPageSource<>(pageSource);
    }

    static <T> PageSource<T> delay(long delayMillis, PageSource<T> pageSource) {
        return () -> {
            try {
                Thread.sleep(delayMillis);
            } catch (InterruptedException ignored) {
            }

            return pageSource.nextPage();
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

    static class RequestCountingPageSource<T> implements PageSource<T> {

        private long requestCount;

        private final PageSource<T> internalPageSource;

        public RequestCountingPageSource(PageSource<T> internalPageSource) {
            this.internalPageSource = internalPageSource;
        }

        @Override
        public Iterable<T> nextPage() {
            requestCount++;
            return internalPageSource.nextPage();
        }

        public long getRequestCount() {
            return requestCount;
        }
    }

}
