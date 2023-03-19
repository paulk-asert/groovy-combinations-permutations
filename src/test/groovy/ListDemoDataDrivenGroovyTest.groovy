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

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

import java.util.function.Predicate
import java.util.stream.Stream

class ListDemoDataDrivenGroovyTest {
    private static final Predicate<Integer> ODD = n -> n % 2 == 1

    private static final OPERATIONS = [
        'clear()'        : List::clear,
        'add(1)'         : { list -> list.add(1) },
        'remove(1)'      : { list -> list.removeElement(1) },
        'addAll(2,3,4,5)': { list -> list.addAll(Arrays.asList(2, 3, 4, 5)) },
        'removeIf(ODD)'  : { list -> list.removeIf(ODD) }
    ]

    private static Stream<Arguments> operationPermutations() {
        OPERATIONS.entrySet().permutations().collect(e -> Arguments.of(e.key, e.value)).stream()
    }

    @ParameterizedTest(name = "{index} {0}")
    @MethodSource("operationPermutations")
    void validate(List<String> names, List<Closure> operations) {
        ArrayList first = []
        LinkedList second = []
        operations.each { op ->
            op(first)
            op(second)
        }
        assert first == second
    }
}
