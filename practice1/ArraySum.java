public class ArraySum {
    private static final int ARRAY_SIZE = 1 << 28;
    private static final int NUM_THREADS = 16;
    private static final int THREAD_SLEEP_MS = 0;

    public static void main(String[] args) throws InterruptedException {
        long startTime, endTime;

        startTime = System.nanoTime();
        int[] array = new int[ARRAY_SIZE];
        for (int i = 0; i < array.length; i++) array[i] = i + 1;
        endTime = System.nanoTime();
        System.out.printf("Step 1: %-40s | Time: %10d μs%n", "Int array memory allocation", (endTime - startTime) / 1000);

        startTime = System.nanoTime();
        int partSize = array.length / NUM_THREADS;
        Thread[] threads = new Thread[NUM_THREADS];
        SumThread[] sumThreads = new SumThread[NUM_THREADS];
        endTime = System.nanoTime();
        System.out.printf("Step 2: %-40s | Time: %10d μs%n", "Thread container array memory allocation", (endTime - startTime) / 1000);

        startTime = System.nanoTime();
        for (int i = 0; i < NUM_THREADS; i++) {
            int start = i * partSize;
            int end = (i == NUM_THREADS - 1) ? array.length : (i + 1) * partSize;
            sumThreads[i] = new SumThread(array, start, end);
            threads[i] = new Thread(sumThreads[i]);
        }
        endTime = System.nanoTime();
        System.out.printf("Step 3: %-40s | Time: %10d μs%n", "Thread creation", (endTime - startTime) / 1000);

        for (int i = 0; i < NUM_THREADS; i++) {
            threads[i].start();
        }

        long totalSum = 0;

        startTime = System.nanoTime();
        for (int i = 0; i < NUM_THREADS; i++) {
            threads[i].join();
            totalSum += sumThreads[i].getPartialSum();
        }
        endTime = System.nanoTime();
        System.out.printf("Step 4: %-40s | Time: %10d μs%n", "Joining threads", (endTime - startTime) / 1000);

        System.out.printf("Step 5: %-40s | Result: %20d%n", "Total sum", totalSum);
    }

    static class SumThread implements Runnable {
        private final int[] array;
        private final int start, end;
        private long partialSum = 0;

        SumThread(int[] array, int start, int end) {
            this.array = array;
            this.start = start;
            this.end = end;
        }

        @Override
        public void run() {
            long endTime = System.nanoTime() + THREAD_SLEEP_MS * 1_000_000L;
            while (System.nanoTime() < endTime) {
                // Busy-wait: keep the CPU fully utilized
            }

            for (int i = start; i < end; i++) partialSum += array[i];
            System.out.printf("Partial sum of part [%10d...%10d]: %20d%n", start + 1, end, partialSum);
        }

        public long getPartialSum() {
            return partialSum;
        }
    }
}
