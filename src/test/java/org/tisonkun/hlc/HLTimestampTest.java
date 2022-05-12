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

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class HLTimestampTest {

    @Test
    public void compare() {
        final var early = HLTimestamp.create(1, 0, 0);
        final var middle = HLTimestamp.create(1, 1, 0);
        final var late = HLTimestamp.create(1, 1, 1);
        Assertions.assertThat(early).isLessThan(middle);
        Assertions.assertThat(middle).isLessThan(late);
        Assertions.assertThat(early).isLessThan(late);
    }

}
