### **Comparison Table**

| **Criterion**     | **Parallelism**                        | **Multithreading**                      |
|--------------------|----------------------------------------|------------------------------------------|
| **Distribution**   | Automatic                             | Manual                                   |
| **Management**     | JVM, Parallel Streams                 | ExecutorService                          |
| **Flexibility**    | Less control                          | More control                             |
| **Performance**    | High on large data                    | Limited by overhead costs                |

### **Execution Results**

| **Image**           | **Parallelism (Time)** | **Multithreading (Time)** |
|----------------------|------------------------|---------------------------|
| 200x300.jpeg         | 96 ms                 | 13 ms                     |
| 1280x720.jpeg        | 75 ms                 | 94 ms                     |
| 1920x1080.jpeg       | 133 ms                | 151 ms                    |
| 3840x2160.jpeg       | 521 ms                | 508 ms                    |

### **Conclusions**
1. **Parallelism**: Efficient for tasks where data blocks can be processed independently, leveraging automatic distribution.
2. **Multithreading**: Allows fine-grained control over execution, suitable for scenarios requiring explicit thread management.
3. **Practical Application**: Comparison of execution time and complexity helps students understand trade-offs between the two approaches.

### **Discussion Questions and Answers**

1. **How do Parallel Streams distribute tasks across cores?**  
   Parallel Streams in Java automatically divide the data into chunks and assign each chunk to a separate core for processing. The JVM uses the Fork/Join framework under the hood to manage the tasks dynamically, maximizing CPU utilization.

2. **What are the risks of manual thread management?**  
   Manual thread management carries risks such as:  
   - Deadlocks: Threads waiting indefinitely for resources locked by each other.  
   - Resource leaks: Improper release of system resources like file handles or memory.  
   - Complex debugging: Errors in multithreaded programs can be difficult to trace and reproduce.  
   - Overhead: Managing thread creation, execution, and termination can add complexity and reduce performance if not handled efficiently.

3. **Can both approaches be combined in one project?**  
   Yes, both Parallelism and Multithreading can be combined to optimize performance. For example, Parallel Streams can be used for data-intensive tasks, while manual thread management can handle tasks requiring finer control, such as task prioritization or inter-thread communication.

