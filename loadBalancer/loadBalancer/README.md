# Load Balancer

A simple layer 7 (HTTP) application load balancer, built from scratch in Java
following [Coding Challenges' Load Balancer](https://codingchallenges.fyi/challenges/challenge-load-balancer)
challenge.

It distributes incoming HTTP requests across a pool of backend servers using
round robin, periodically health-checks the backends, and automatically
routes around ones that go offline (and back in once they recover).

## Architecture

| Class | Responsibility |
|---|---|
| `Backend` | A backend's host/port and health flag |
| `ServerPool` | Owns the set of backends, exposes the currently healthy ones |
| `LoadBalancingStrategy` / `RoundRobinStrategy` | Picks the next backend for a request (Strategy pattern) |
| `HealthCheckProbe` / `HttpHealthCheckProbe` | Checks whether a single backend is alive |
| `HealthChecker` | Runs the probe against every backend on a schedule, updates health state |
| `RawHttpRequest` | Reads a full HTTP request (headers + body) off a socket |
| `ProxyForwarder` | Sends a request to a chosen backend and streams the response back |
| `ConnectionHandler` | Per-connection flow: read request → pick backend → forward, retrying another healthy backend on failure |
| `LoadBalancerServer` | Accepts connections on a thread pool and dispatches to `ConnectionHandler` |
| `LoadBalancerConfig` | Parses CLI arguments |
| `Main` | Composition root — wires everything together and starts it |

`BackendServer` / `BackendServerMain` is a small standalone HTTP server (`be`)
used only for testing the load balancer; it responds `200 OK` with a fixed
body to any request.

## Build

```bash
javac -d out src/*.java
```

## Run

Start a couple of test backends:

```bash
java -cp out BackendServerMain 8080
java -cp out BackendServerMain 8081
```

Start the load balancer:

```bash
java -cp out Main --port 9000 --backend localhost:8080 --backend localhost:8081
```

Then hit it:

```bash
curl http://localhost:9000/
```

### CLI options

| Flag | Default | Description |
|---|---|---|
| `--port` | `8000` | Port the load balancer listens on |
| `--backend host:port` | `localhost:8080`, `localhost:8081` | Backend to add to the pool; repeatable |
| `--health-check-path` | `/` | Path used for health check requests |
| `--health-check-interval` | `10` | Seconds between health checks |
| `--health-check-timeout` | `2` | Seconds before a health check is considered failed |

## Testing it manually

1. **Round robin** — curl repeatedly and watch the LB log alternate
   `Routing request to: ...` between backends.
2. **Failover** — kill one backend process; requests should keep succeeding,
   routed only to the remaining healthy backends.
3. **Recovery** — restart the killed backend; after one health-check
   interval it should rejoin rotation (`passed health check, back in
   rotation` in the log).
4. **Concurrency** — fire several requests in parallel with curl:
   ```bash
   printf 'url = "http://localhost:9000"\n%.0s' {1..8} > urls.txt
   curl --parallel --parallel-immediate --parallel-max 8 --config urls.txt -w '%{http_code}\n' -o /dev/null
   ```
5. **No healthy backends** — kill every backend and curl; the LB responds
   `503 Service Unavailable` instead of hanging or erroring out.
