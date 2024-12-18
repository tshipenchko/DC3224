# Practical Assignment for Students

## Practical Task #1: Multithreaded Application for Array Sum Calculation

### Objective:
- Learn the basics of multithreading.
- Implement a program that uses multiple threads to speed up calculations.
- Practice dividing tasks into parts and combining results.

---

### Part 1: Main Task

Write a Java program that:
1. Creates an array of 100 elements filled with numbers from 1 to 100.
2. Splits the array into four parts, each processed by a separate thread.
3. Calculates the sum of each part of the array in its own thread.
4. Combines the results from all threads and prints the total sum of the array.

### Requirements:
- Use `Thread` or `Runnable` classes.
- Print the following to the console:
  - The sum of each part of the array.
  - The total sum.

---

### Part 2: Extra Task

1. **Increase Complexity**:  
   - Increase the size of the array and the number of threads.  
   - Use system monitoring tools (e.g., `htop`) to observe CPU utilization during the computation.  

2. **Observation and Issue**:  
   - Simply increasing the array size did not result in fairly long computations to capture noticeable CPU utilization. The calculations completed too quickly for meaningful observation.

3. **Solution — Heavy Thread Sleep**:  
   - To simulate longer computations and forcefully load the CPU, introduce a **heavy thread sleep** using a busy-wait loop.  
   - Example: Replace `Thread.sleep` with a `while` loop to simulate thread execution while keeping the CPU active.  
   - This approach successfully demonstrated 100% CPU utilization for each thread during the computation.  

---

### Analysis of Thread Configurations

1. **16 Threads**:
   - All 16 threads show up to **50% CPU usage** because the system has only 8 physical cores.
   - CPU resources are divided between threads via time-sharing, leading to reduced load per thread.
   - Observed 18 threads (excluding the main thread) include JVM-related threads for garbage collection, JIT compilation, and other background operations.

2. **8 Threads**:
   - Each thread operates at **100% CPU usage**, making this configuration optimal for an 8-core CPU.
   - Despite optimal usage, context switching between threads introduces minor inefficiencies, preventing maximum computational power.

3. **4 Threads**:
   - With 4 threads, each runs at **100% CPU usage** with minimal context switching.
   - This configuration is efficient but underutilizes the multicore capabilities of the CPU.

4. **1 Thread**:
   - A single thread uses **100% CPU** but performs computations sequentially.
   - This setup does not benefit from the multicore architecture and has the longest execution time.

---

### Maximizing CPU Utilization

To achieve maximum computational power:
- **CPU Affinity**: Assign specific threads to specific CPU cores.
- **Thread Pinning**: Prevent threads from being stopped or moved between cores by the operating system.
