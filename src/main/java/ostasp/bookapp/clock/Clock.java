package ostasp.bookapp.clock;

import java.time.Duration;
import java.time.LocalDateTime;

public interface Clock {

    LocalDateTime now();

    class MockTestClock implements Clock {

        private LocalDateTime time;

        public MockTestClock(LocalDateTime time) {
            this.time = time;
        }

        public MockTestClock() {
            this(LocalDateTime.now());
        }

        @Override
        public LocalDateTime now() {
            return time;
        }

        public void tick(Duration duration) {
            time = time.plus(duration);
        }
    }
}
