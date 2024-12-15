public class ArraySum {
    private static final int ARRAY_SIZE = 100;
    private static final int NUM_THREADS = 4;

    public static void main(String[] args) throws InterruptedException {
        int[] array = new int[ARRAY_SIZE];
        for (int i = 0; i < array.length; i++) array[i] = i + 1;

        int partSize = array.length / NUM_THREADS;
        Thread[] threads = new Thread[NUM_THREADS];
        SumThread[] sumThreads = new SumThread[NUM_THREADS];

        for (int i = 0; i < NUM_THREADS; i++) {
            int start = i * partSize;
            int end = (i == NUM_THREADS - 1) ? array.length : (i + 1) * partSize;
            sumThreads[i] = new SumThread(array, start, end);
            threads[i] = new Thread(sumThreads[i]);
            threads[i].start();
        }

        int totalSum = 0;
        for (int i = 0; i < NUM_THREADS; i++) {
            threads[i].join();
            totalSum += sumThreads[i].getPartialSum();
        }
        System.out.println("Total sum: " + totalSum);
    }

    static class SumThread implements Runnable {
        private final int[] array;
        private final int start, end;
        private int partialSum = 0;

        SumThread(int[] array, int start, int end) {
            this.array = array;
            this.start = start;
            this.end = end;
        }

        @Override
        public void run() {
            for (int i = start; i < end; i++) partialSum += array[i];
            System.out.println("Partial sum of part [" + (start+1) + "..." + end + "]: " + partialSum);
        }

        public int getPartialSum() {
            return partialSum;
        }
    }
}
