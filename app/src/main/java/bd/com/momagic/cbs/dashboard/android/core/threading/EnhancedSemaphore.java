package bd.com.momagic.cbs.dashboard.android.core.threading;

import lombok.Getter;

import java.util.concurrent.Semaphore;

public class EnhancedSemaphore extends Semaphore {

    @Getter
    private final int permits;

    public EnhancedSemaphore(int permits) {
        super(permits);

        this.permits = permits;
    }

    public EnhancedSemaphore(int permits, boolean fair) {
        super(permits, fair);

        this.permits = permits;
    }
}
