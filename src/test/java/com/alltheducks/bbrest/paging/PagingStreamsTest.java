package com.alltheducks.bbrest.paging;

import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.alltheducks.bbrest.paging.TestHelpers.*;
import static org.junit.jupiter.api.Assertions.*;

public class PagingStreamsTest {

    @Test
    public void streamCount() {
        final Stream<Integer> s = PagingStreams.getStream(pageSource(intRange(1, 10)));

        assertEquals(10, s.count());
    }

    @Test
    public void streamCollectToList() {
        final Stream<Integer> s = PagingStreams.getStream(pageSource(intRange(1, 10)));

        assertEquals(intRange(1, 10), s.collect(Collectors.toList()));
    }

}