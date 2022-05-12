/*
 * Copyright 2022 tison <wander4096@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.tisonkun.hlc;

import java.time.Instant;
import java.util.List;
import java.util.function.Supplier;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class HybridLogicalClockTest {

    private static final HLTimestamp zero = HLTimestamp.create(0, 0, 0);

    private record TestCase(Instant now, HLTimestamp event, HLTimestamp expected) {
        private static Supplier<Instant> clock(List<TestCase> tests) {
            final var iterator = tests.stream().map(TestCase::now).iterator();
            return iterator::next;
        }
    }

    private static HLTimestamp resolve(State state, HLTimestamp event) {
        if (event.equals(zero)) {
            return state.getTimestamp();
        } else {
            return state.update(event);
        }
    }

    private static Instant ts(long seconds, long nanos) {
        return Instant.ofEpochSecond(seconds, nanos);
    }

    private static HLTimestamp hlts(long seconds, long nanos, long logical) {
        return HLTimestamp.create(seconds, nanos, logical);
    }

    @Test
    public void allonsy() {
        final var tests = List.of(
            // Test cases in the form (wall, event_ts, outcome).
            // Specifying event_ts as zero corresponds to calling getTimestamp, otherwise update.
            new TestCase(ts(1,0), zero, hlts(1,0,0)),
            new TestCase(ts(1,0), zero, hlts(1,0,1)), // clock didn't move
            new TestCase(ts(0,9), zero, hlts(1,0,2)), // clock moved back
            new TestCase(ts(2,0), zero, hlts(2,0,0)), // finally, ahead again
            new TestCase(ts(3,0), hlts(1,2,3), hlts(3,0,0)), // event happens, but wall ahead
            new TestCase(ts(3,0), hlts(1,2,3), hlts(3,0,1)), // event happens, wall ahead but unchanged
            new TestCase(ts(3,0), hlts(3,0,1), hlts(3,0,2)), // event happens at wall, which is still unchanged
            new TestCase(ts(3,0), hlts(3,0,99), hlts(3,0,100)), // event with larger logical, wall unchanged
            new TestCase(ts(3,5), hlts(4,4,100), hlts(4,4,101)), // event with larger wall, our wall behind
            new TestCase(ts(5,0), hlts(4,5,0), hlts(5,0,0)), // event behind wall, but ahead of previous state
            new TestCase(ts(4,9), hlts(5,0,99), hlts(5,0,100)),
            new TestCase(ts(0,0), hlts(5,0,50), hlts(5,0,101)) // event at state, lower logical than state
        );

        // create state from fake clock
        final var state = State.create(TestCase.clock(tests));

        for (var test : tests) {
            Assertions.assertThat(resolve(state, test.event)).isEqualTo(test.expected);
        }
    }

}
