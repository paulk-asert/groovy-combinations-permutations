/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import net.openhft.chronicle.testframework.Combination
import net.openhft.chronicle.testframework.Permutation
import net.openhft.chronicle.testframework.function.NamedConsumer
import org.codehaus.groovy.runtime.FormatHelper
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

import java.util.concurrent.CopyOnWriteArrayList
import java.util.function.Predicate
import java.util.function.Supplier
import java.util.stream.Stream

class ListDemoTest {
    private static final Predicate<Integer> ODD = n -> n % 2 == 1

    private static final OPERATIONS = [
        NamedConsumer.of(List::clear, "clear()"),
        NamedConsumer.of(list -> list.add(1), "add(1)"),
        NamedConsumer.of(list -> list.removeElement(1), "remove(1)"),
        NamedConsumer.of(list -> list.addAll([2, 3, 4, 5]), "addAll(2,3,4,5)"),
        NamedConsumer.of(list -> list.removeIf(ODD), "removeIf(ODD)")
    ]

    private static final CONSTRUCTORS = [
        ArrayList, LinkedList, CopyOnWriteArrayList, Stack, Vector
    ].collect(clazz -> clazz::new as Supplier)

    @TestFactory
    Stream<DynamicTest> validate() {
        DynamicTest.stream(Combination.of(OPERATIONS)
            .flatMap(Permutation::of),
            FormatHelper::toString,
            operations -> {
                ArrayList first = []
                LinkedList second = []
                operations.forEach { op ->
                    op.accept(first)
                    op.accept(second)
                }
                assert first == second
            })
    }

    @TestFactory
    Stream<DynamicTest> validateMany() {
        DynamicTest.stream(Combination.of(OPERATIONS)
            .flatMap(Permutation::of),
            FormatHelper::toString,
            operations -> {
                var lists = CONSTRUCTORS.stream()
                    .map(Supplier::get)
                    .toList()

                operations.forEach(lists::forEach)

                Combination.of(lists)
                    .filter(set -> set.size() == 2)
                    .map(ArrayList::new)
                    .forEach { p1, p2 -> assert p1 == p2 }
            })
    }

    @Test
    void numberOfPairCombinations() {
        assert Combination.of(CONSTRUCTORS)
            .filter(l -> l.size() == 2)
            .peek { println it*.get()*.class*.simpleName }
            .count() == 10
    }

    @Test
    void numberOfPermutations() {
        assert Combination.of(1, 2, 3, 4, 5, 6)
            .flatMap(Permutation::of)
            .peek { println it }
            .count() == 1957
    }
}
