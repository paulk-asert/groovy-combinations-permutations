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

import io.github.pavelicii.allpairs4j.AllPairs
import io.github.pavelicii.allpairs4j.Parameter
import org.junit.jupiter.api.Test

import java.util.function.Predicate

class ListDemoGroovyAllPairsTest {
    private static final Predicate<Integer> ODD = n -> n % 2 == 1

    private static final GROW_OPS = [
        'add(1)': { list -> list.add(1) },
        'addAll([2, 3, 4, 5])': { list -> list.addAll([2, 3, 4, 5]) },
        'maybe add(1)': { list -> if (new Random().nextBoolean()) list.add(1) },
    ].entrySet().toList()

    private static final SHRINK_OPS = [
        'clear()': List::clear,
        'remove(1)': { list -> list.removeElement(1) },
//        'removeAt(0)': { list -> list.removeAt(0) },
        'removeIf(ODD)': { list -> list.removeIf(ODD) }
    ].entrySet().toList()

    private static final READ_OPS = [
        'isEmpty()': List::isEmpty,
        'size()': List::size,
        'contains(1)': { list -> list.contains(1) },
    ].entrySet().toList()

    @Test
    void checkPermutationsCount() {
        assert [GROW_OPS, SHRINK_OPS, READ_OPS].combinations().size() == 27
    }

    @Test
    void validate() {
        var allPairs = new AllPairs.AllPairsBuilder()
            .withTestCombinationSize(2)
            .withParameter(new Parameter("Grow op", GROW_OPS))
            .withParameter(new Parameter("Shrink op", SHRINK_OPS))
            .withParameter(new Parameter("Read op", READ_OPS))
            .build()
        allPairs.eachWithIndex { namedOps, index ->
            print "$index: "
            ArrayList first = []
            LinkedList second = []
            var log = []
            namedOps.each{ k, v ->
                try {
                    log << "$k=$v.key"
                    var op = v.value
                    op(first)
                    op(second)
                } catch(ex) {
                    println 'Failed on last op of: ' + log.join(', ')
                    throw ex
                }
            }
            println log.join(', ')
            assert first == second
        }
    }
}
