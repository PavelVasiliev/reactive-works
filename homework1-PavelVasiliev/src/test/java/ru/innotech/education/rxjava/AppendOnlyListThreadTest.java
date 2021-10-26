package ru.innotech.education.rxjava;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;
import static org.assertj.core.api.Assertions.assertThat;

class AppendOnlyListThreadTest {
    private static final int READ_DURATION = 12;
    private static final int READ_THREAD_COUNT = 5;
    private static final int WRITE_DURATION = 9;
    private static final int WRITE_THREAD_COUNT = 10;
    private static final int REMOVE_DURATION = 15;
    private static final int REMOVE_THREAD_COUNT = 3;
    private static final int ITERATIONS = 250;

    @Test
    void testParallel() {
        final AppendOnlyList<Integer> list = new AppendOnlyList<>();

        final List<Thread> readThreads = buildThreads(new Read(list, ITERATIONS), READ_THREAD_COUNT, "read");
        final List<Thread> writeThreads = buildThreads(new Write(list, ITERATIONS), WRITE_THREAD_COUNT, "write");
        final List<Thread> removeThread = buildThreads(new Remove(list, ITERATIONS), REMOVE_THREAD_COUNT, "remove");

        readThreads.forEach(Thread::start);
        writeThreads.forEach(Thread::start);
        removeThread.forEach(Thread::start);

        join(readThreads);
        join(writeThreads);
        join(removeThread);

        int itemsInserted = ITERATIONS * WRITE_THREAD_COUNT;
        int itemsRemoved = ITERATIONS * REMOVE_THREAD_COUNT;
        assertThat(list.size()).isEqualTo(itemsInserted - itemsRemoved);
    }

    @SneakyThrows
    private void join(List<Thread> threads) {
        for (final Thread thread : threads) {
            thread.join();
        }
    }

    private List<Thread> buildThreads(Runnable runnable, int threadCount, String name) {
        return range(0, threadCount)
                .mapToObj(i -> new Thread(runnable, name + "-" + i))
                .collect(toList());
    }

    @RequiredArgsConstructor
    private static class Remove
            implements Runnable {
        private final AppendOnlyList<Integer> list;
        private final int iterations;

        @Override
        public void run() {
            for (int i = 0; i < iterations; i++) {
                synchronized (this) {
                    list.stream()
                            .findFirst()
                            .ifPresent(item -> {
                                list.remove(item);
                                sleep();
                            });
                }
            }
        }

        @SneakyThrows
        void sleep() {
            Thread.sleep(REMOVE_DURATION);
        }
    }

    @RequiredArgsConstructor
    private static class Write
            implements Runnable {
        private final AppendOnlyList<Integer> list;
        private final int iterations;
        private int counter = 0;

        @Override
        public void run() {
            for (int i = 0; i < iterations; i++) {
                list.add(counter++);
                sleep();
            }
        }

        @SneakyThrows
        void sleep() {
            Thread.sleep(WRITE_DURATION);
        }
    }

    @RequiredArgsConstructor
    private static class Read
            implements Runnable {
        private final AppendOnlyList<Integer> list;
        private final int iterations;

        @Override
        public void run() {
            for (int i = 0; i < iterations; i++) {
                final List<Integer> newList = new ArrayList<>();
                for (final Integer item : list) {
                    newList.add(item);
                }
                System.out.println("Thread '" + Thread.currentThread().getName() + "' iteration " + i + ", size " + newList.size());
                sleep();
            }
        }

        @SneakyThrows
        void sleep() {
            Thread.sleep(READ_DURATION);
        }
    }
}