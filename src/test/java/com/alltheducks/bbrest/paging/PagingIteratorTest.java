package com.alltheducks.bbrest.paging;

import org.glassfish.jersey.internal.guava.Lists;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.alltheducks.bbrest.paging.TestHelpers.*;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;

public class PagingIteratorTest {

    @Test
    public void noPages() {
        final PageSource<Integer> source = pageSource();

        final PagingIterator<Integer> iterator = new PagingIterator<>(source);
        final ArrayList<Integer> result = Lists.newArrayList(iterator);

        assertEquals(0, result.size());
    }

    @Test
    public void onePage() {
        final PageSource<Integer> source = pageSource(intRange(1, 3));

        final PagingIterator<Integer> iterator = new PagingIterator<>(source);
        final ArrayList<Integer> result = Lists.newArrayList(iterator);

        assertEquals(3, result.size());
        assertEquals(result, asList(1, 2, 3));
    }

    @Test
    public void twoPages() {
        final PageSource<Integer> source = pageSource(intRange(1, 3), intRange(4, 6));

        final PagingIterator<Integer> iterator = new PagingIterator<>(source);
        final ArrayList<Integer> result = Lists.newArrayList(iterator);

        assertEquals(6, result.size());
        assertEquals(result, asList(1, 2, 3, 4, 5, 6));
    }

    @Test
    public void differingPageSizes() {
        final PageSource<Integer> source = pageSource(intRange(1, 3), intRange(4, 10), intRange(11, 12));

        final PagingIterator<Integer> iterator = new PagingIterator<>(source);
        final ArrayList<Integer> result = Lists.newArrayList(iterator);

        assertEquals(12, result.size());
        assertEquals(result, intRange(1, 12));
    }

    @Test
    public void oneHundredPages() {
        final PageSource<Integer> source = pageSource(partition(intRange(1, 1000), 10));

        final PagingIterator<Integer> iterator = new PagingIterator<>(source);
        final ArrayList<Integer> result = Lists.newArrayList(iterator);

        assertEquals(1000, result.size());
        assertEquals(result, intRange(1, 1000));
    }

    @Test
    public void fivePagesWithDelay() {
        final PageSource<Integer> source = delay(100, pageSource(partition(intRange(1, 45), 10)));

        final PagingIterator<Integer> iterator = new PagingIterator<>(source);
        final ArrayList<Integer> result = Lists.newArrayList(iterator);

        assertEquals(45, result.size());
        assertEquals(result, intRange(1, 45));
    }

    @Test
    public void withStrings() {
        final PageSource<String> source = pageSource(asList("One", "Two", "Three"), asList("Four", "Five", "Six"));

        final PagingIterator<String> iterator = new PagingIterator<>(source);
        final ArrayList<String> result = Lists.newArrayList(iterator);

        assertEquals(6, result.size());
        assertEquals(result, asList("One", "Two", "Three", "Four", "Five", "Six"));
    }

    @Test
    public void withObjects() {
        final PageSource<TestObject> source = pageSource(objects("One", "Two", "Three"), objects("Four", "Five", "Six"));

        final PagingIterator<TestObject> iterator = new PagingIterator<>(source);
        final ArrayList<TestObject> result = Lists.newArrayList(iterator);

        assertEquals(6, result.size());
        final List<String> objectNames = result.stream().map(TestObject::getName).collect(Collectors.toList());
        assertEquals(objectNames, asList("One", "Two", "Three", "Four", "Five", "Six"));
    }

    @Test
    public void testNumberOfPagesRequested() {
        final RequestCountingPageSource<Integer> source = counting(pageSource(intRange(1, 3)));

        final PagingIterator<Integer> iterator = new PagingIterator<>(source);
        Lists.newArrayList(iterator);

        assertEquals(2, source.getRequestCount());
    }

    @Test
    public void testNumberOfPagesRequested_oneHundredPages() {
        final RequestCountingPageSource<Integer> source = counting(pageSource(partition(intRange(1, 1000), 10)));

        final PagingIterator<Integer> iterator = new PagingIterator<>(source);
        Lists.newArrayList(iterator);

        assertEquals(101, source.getRequestCount());
    }






}