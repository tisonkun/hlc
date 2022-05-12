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
import java.util.function.Supplier;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * State is a hybrid logical clock.
 */
@EqualsAndHashCode
@ToString
public final class State {

    private final Supplier<Instant> now;
    private volatile HLTimestamp timestamp;

    public State(HLTimestamp timestamp, Supplier<Instant> now) {
        this.timestamp = timestamp;
        this.now = now;
    }

    /**
     * Creates a standard hybrid logical clock, using {@link Instant#now()} as
     * supplier of the physical clock's wall time.
     * @return hybrid logical clock
     */
    public static State create() {
        return create(Instant::now);
    }

    /**
     * Creates a hybrid logical clock with the supplied wall time. This is useful
     * for tests or settings in which an alternative clock is used.
     * @param now supplier for instant now
     * @return hybrid logical clock
     */
    public static State create(Supplier<Instant> now) {
        return new State(HLTimestamp.create(0, 0, 0), now);
    }

    /**
     * Generates a timestamp from the clock.
     * @return hybrid logical timestamp
     */
    public HLTimestamp getTimestamp() {
        final var ts = this.timestamp;
        final var wall = this.now.get();
        if (ts.wall().compareTo(wall) < 0) {
            this.timestamp = new HLTimestamp(wall, 0);
        } else {
            this.timestamp = new HLTimestamp(ts.wall(), ts.logical() + 1);
        }
        return this.timestamp;
    }

    /**
     * Assigns a timestamp to an event which happened at the given timestamp
     * on a remote system.
     * @param event timestamp on a remote system
     * @return hybrid logical timestamp
     */
    public HLTimestamp update(HLTimestamp event) {
        final var ts = this.timestamp;
        final var wall = this.now.get();
        if (wall.compareTo(event.wall()) > 0 && wall.compareTo(ts.wall()) > 0) {
            this.timestamp = new HLTimestamp(wall, 0);
        } else if (event.wall().compareTo(ts.wall()) > 0) {
            this.timestamp = new HLTimestamp(event.wall(), event.logical() + 1);
        } else if (ts.wall().compareTo(event.wall()) > 0) {
            this.timestamp = new HLTimestamp(ts.wall(), ts.logical() + 1);
        } else {
            var logical = Math.max(event.logical(), ts.logical());
            this.timestamp = new HLTimestamp(ts.wall(), logical + 1);
        }
        return this.timestamp;
    }

}
