# Perf Analysis

```bash
sudo perf sched -i perf.data latency --sort max
sudo perf sched -i perf.data latency -p
```
![q1impl-perfsched-latency](q1impl-perfsched-latency.png)
![threadsafe-perfsched-latency](q1impl-perfsched-latency.png)

```bash
sudo perf sched -i perf.data map
sudo perf sched -i perf.data timehist -MVw
```
![q1impl-perfsched-map&timehist](q1impl-perfsched-map&timehist.png)
![threadsafe-perfsched-map&timehist](q1impl-perfsched-map&timehist.png)

```bash
sudo perf stat -d executavel
sudo perf stat -d --repeat=100 executavel
sudo perf stat -d --repeat=1000 executavel
```
![perfstat-d1.0](perfstat-d1.0.png)
![perfstat-d1.1](perfstat-d1.1.png)
![perfstat-d100](perfstat-d100.png)
![perfstat-d1000](perfstat-d1000.png)

## Outros comandos
```bash
sudo perf record executavel
sudo perf script -i perf.data
```
