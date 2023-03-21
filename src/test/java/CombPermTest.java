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

import groovy.util.GroovyCollections;
import groovy.util.PermutationGenerator;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CombPermTest {
    @Test
    public void combinations() {
        String[] letters = {"A", "B"};
        Integer[] numbers = {1, 2};
        Object[] collections = {letters, numbers};
        var expected = List.of(
            List.of("A", 1),
            List.of("B", 1),
            List.of("A", 2),
            List.of("B", 2)
        );
        var combos = GroovyCollections.combinations(collections);
        assertEquals(expected, combos);
    }

    @Test
    public void subsequences() {
        var numbers = List.of(1, 2, 3);
        var expected = Set.of(
            List.of(1), List.of(2), List.of(3),
            List.of(1, 2), List.of(1, 3), List.of(2, 3),
            List.of(1, 2, 3)
        );
        var result = GroovyCollections.subsequences(numbers);
        assertEquals(expected, result);
    }

    @Test
    public void permutations() {
        var numbers = List.of(1, 2, 3);
        var gen = new PermutationGenerator<>(numbers);
        var result = new HashSet<>();
        while (gen.hasNext()) {
            List<Integer> next = gen.next();
            result.add(next);
        }
        var expected = Set.of(
            List.of(1, 2, 3), List.of(1, 3, 2),
            List.of(2, 1, 3), List.of(2, 3, 1),
            List.of(3, 1, 2), List.of(3, 2, 1)
        );
        assertEquals(expected, result);
    }
}
