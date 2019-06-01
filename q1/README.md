# Questão 1:

## Comando para Compilar
```bash
gcc -o q1-impl q1-impl.c  -pthread
gcc -o q1-thread-safe-impl q1-thread-safe-impl.c  -pthread
```

## Comando para executar Run
```bash
./q1-impl
./q1-thread-safe-impl
```

## Comandos Perf usados
```bash
sudo perf record executavel
sudo perf script -i perf.data
sudo perf stat -d executavel
sudo perf stat -d --repeat=numero executavel
sudo perf sched -i perf.data latency --sort max
sudo perf sched -i perf.data latency -p
sudo perf sched -i perf.data map
sudo perf sched -i perf.data timehist -MVw
```
