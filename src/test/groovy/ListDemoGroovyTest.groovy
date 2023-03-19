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

import org.junit.jupiter.api.Test

import java.util.concurrent.CopyOnWriteArrayList
import java.util.function.Predicate
import java.util.function.Supplier

class ListDemoGroovyTest {
    private static final Predicate<Integer> ODD = n -> n % 2 == 1

    private static final OPERATIONS = [
        List::clear,
        { list -> list.add(1) },
        { list -> list.removeElement(1) },
        { list -> list.addAll(Arrays.asList(2, 3, 4, 5)) },
        { list -> list.removeIf(ODD) }
    ]

    private static final CONSTRUCTORS = [
        ArrayList, LinkedList, CopyOnWriteArrayList, Stack, Vector
    ].collect { clazz -> clazz::new as Supplier }

    @Test
    void validate() {
        OPERATIONS.eachPermutation { opList ->
            ArrayList first = []
            LinkedList second = []
            opList.each { op ->
                op(first)
                op(second)
            }
            assert first == second
        }
    }

    @Test
    void validateMany() {
        OPERATIONS.eachPermutation { opList ->
            def pairs= CONSTRUCTORS*.get().subsequences().findAll { it.size() == 2 }
            pairs.each { first, second ->
                opList.each { op ->
                    op(first)
                    op(second)
                }
                assert first == second
            }
        }
    }

    @Test
    void numberOfPairCombinations() {
        assert (1..5).subsequences()
            .findAll(l -> l.size() == 2)
            .size() == 10
    }

    @Test
    void numberOfPermutations() {
        var perms = (1..6).subsequences()*.permutations().sum() << []
        assert perms.size() == 1957
    }
}
