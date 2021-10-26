package ru.innotech.education.rxjava;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.List.of;
import static org.assertj.core.api.Assertions.assertThat;

class AppendOnlyListTest {

    @Test
    void testAdd() {
        final AppendOnlyList<Integer> list = new AppendOnlyList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.addAll(of(4, 5, 6));
        assertThat(list).containsExactly(1, 2, 3, 4, 5, 6);
    }

    @Test
    void testRemove() {
        final AppendOnlyList<Integer> list = new AppendOnlyList<>(of(1, 2, 3, 4, 5, 6));
        list.remove(2);
        list.removeAll(of(4, 5));
        assertThat(list).containsExactly(1, 3, 6);
    }

    @Test
    void testContains() {
        final AppendOnlyList<Integer> list = new AppendOnlyList<>(of(1, 2, 3, 4, 5, 6));
        assertThat(list.contains(2)).isTrue();
        assertThat(list.contains(7)).isFalse();
        assertThat(list.containsAll(of(2, 4, 6))).isTrue();
        assertThat(list.containsAll(of(2, 7, 8))).isFalse();
    }

    @Test
    void testIterator() {
        final AppendOnlyList<Integer> list = new AppendOnlyList<>();
        list.addAll(of(1, 2, 3, 4, 5, 6));
        list.removeAll(of(2, 4, 6));

        List<Integer> newList = new ArrayList<>();
        for (final Integer i : list) {
            newList.add(i);
        }
        assertThat(newList).containsExactly(1, 3, 5);

        newList = list.stream().collect(Collectors.toList());
        assertThat(newList).containsExactly(1, 3, 5);
    }

    @Test
    void testChangeInsideLoop() {
        final AppendOnlyList<Integer> list = new AppendOnlyList<>(of(1, 2, 3, 4, 5, 6));
        final List<Integer> newList = new ArrayList<>();
        for (final Integer i: list) {
            if (i == 2) {
                list.remove(1);
            }
            if (i == 3) {
                list.add(7);
            }
            if (i == 4) {
                list.remove(4);
            }
            newList.add(i);
        }
        assertThat(newList).containsExactly(1, 2, 3, 4, 5, 6, 7);
        assertThat(new ArrayList<>(list)).containsExactly(2, 3, 5, 6, 7);
    }
}