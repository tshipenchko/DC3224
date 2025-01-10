**Multithreaded Data Processing with REST API Result Transmission**

---

### **1. Architecture Description**

#### **Visual Representation**
```mermaid
graph TD
    subgraph Client-Server Architecture
        A[Client 1] -- Task Request --> S[Server]
        B[Client 2] --> S
        C[Client 3] --> S
        D[Client 4] --> S
        S -- Task Distribution --> A
        S -- Task Distribution --> B
        S -- Task Distribution --> C
        S -- Task Distribution --> D
        A -- Result Transmission --> S
        B -- Result Transmission --> S
        C -- Result Transmission --> S
        D -- Result Transmission --> S
    end
    S -- Collates Results --> A
```
#### **Textual Description**

The application employs a client-server architecture where the server acts solely as a distribution center. Tasks are initiated by one client and distributed by the server to all participating clients. Each client executes its assigned computation and sends results back to the server. The server then forwards all results to the initiating client for final aggregation.

- **Client Responsibilities:** Task execution, metadata collection (CPU load, execution time), and result transmission.
- **Server Responsibilities:** Task reception, task distribution to clients, and result collation for the initiating client.
- **Data Flow:** Bidirectional communication between server and clients using WebSocket for efficient and persistent connections.

---

### **2. Metadata**

| **Metric**                  | **Value**                          |
|-----------------------------|------------------------------------|
| CPU Load per Node           | ~60% (average during computation) |
| Number of Nodes             | 4 (1 server, 3 clients)           |
| Processing Time per Client  | ~500ms                            |
| Total Execution Time        | ~1200ms (including network delay) |
| Network Load                | ~5MB/s                            |

---

### **5. Execution Example**

#### **Task:** `sum 1 2 3 4 5 6 7 8 9 10`

#### **Mermaid Diagram**
```mermaid
sequenceDiagram
    participant C1 as Client 1
    participant S as Server
    participant C2 as Client 2
    participant C3 as Client 3
    participant C4 as Client 4

    C1->>S: sum 1 2 3 4 5 6 7 8 9 10
    S->>C2: sum 1 2 3
    S->>C3: sum 4 5 6
    S->>C4: sum 7 8 9 10
    C2->>S: 6
    C3->>S: 15
    C4->>S: 34
    S->>C1: result 55
```

#### **Transmission Strings**

**Client to Server:**  
`sum 1 2 3 4 5 6 7 8 9 10\n`

**Server to Clients:**  
- `Client 2: sum 1 2 3\n`
- `Client 3: sum 4 5 6\n`
- `Client 4: sum 7 8 9 10\n`

**Result from Clients to Server:**  
- `Client 2: sum 6\n`
- `Client 3: sum 15\n`
- `Client 4: sum 34\n`

**Final Server to Client:**  
`Client 1: result 55\n`

#### **Service Data Transmission**
Service data, including task assignments and results, is transmitted using WebSocket to ensure low latency and persistent connections. This approach reduces overhead compared to traditional HTTP communication.
