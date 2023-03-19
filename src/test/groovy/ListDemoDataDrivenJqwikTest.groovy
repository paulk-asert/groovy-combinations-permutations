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

import net.jqwik.api.Arbitrary
import net.jqwik.api.Arbitraries
import net.jqwik.api.ForAll
import net.jqwik.api.Property
import net.jqwik.api.Provide
import net.jqwik.api.state.Action
import net.jqwik.api.state.ActionChain
import net.jqwik.api.state.Transformer

import java.util.function.Predicate

class ListDemoDataDrivenJqwikTest {
    private static final Predicate<Integer> ODD = n -> n % 2 == 1

    private static final OPERATIONS = [
        'clear()'        : List::clear,
        'add(1)'         : { list -> list.add(1) },
        'remove(1)'      : { list -> list.removeElement(1) },
//        'removeAt(0)'    : { list -> list.removeAt(0) },
        'addAll(2,3,4,5)': { list -> list.addAll(Arrays.asList(2, 3, 4, 5)) },
        'removeIf(ODD)'  : { list -> list.removeIf(ODD) }
    ].entrySet().toList()

    class MutateAction implements Action.Independent<Tuple2<List, List>> {
        Arbitrary<Transformer<Tuple2<List, List>>> transformer() {
            Arbitraries.of(OPERATIONS).map(operation ->
                Transformer.mutate(operation.key) { list1, list2 ->
                    var op = operation.value
                    op(list1)
                    op(list2)
                    assert list1 == list2
                })
        }
    }

    @Provide
    Arbitrary<ActionChain> myListActions() {
        ActionChain.startWith{ Tuple2.tuple([1] as ArrayList, [1] as LinkedList) }
            .withAction(new MutateAction())
            .withMaxTransformations(6)
    }

    @Property(seed='100001')
    void confirmSimilarListBehavior(@ForAll("myListActions") ActionChain chain) {
        chain.run()
    }
}
