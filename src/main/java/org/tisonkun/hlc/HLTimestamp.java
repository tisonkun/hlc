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
import java.util.Comparator;

/**
 * The HLTimespec type stores a hybrid logical timestamp.
 *
 * Such a timestamp comprises an "ordinary" wall time and a logical component.
 * Timestamps are compared by wall time first, logical second.
 */
public record HLTimestamp(Instant wall, long logical) implements Comparable<HLTimestamp> {

    /**
     * Creates a new hybrid logical timestamp with the given seconds, nanoseconds, and logical ticks.
     * @param seconds second part
     * @param nanoAdjustment nanosecond part
     * @param logical ticks
     * @return hybrid logical timestamp
     */
    public static HLTimestamp create(long seconds, long nanoAdjustment, long logical) {
        return new HLTimestamp(Instant.ofEpochSecond(seconds, nanoAdjustment), logical);
    }

    @Override
    public int compareTo(HLTimestamp o) {
        return Comparator.comparing(HLTimestamp::wall, Instant::compareTo)
            .thenComparingLong(HLTimestamp::logical)
            .compare(this, o);
    }

}
