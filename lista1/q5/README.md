# Letra A:

ConcurrentHashMap e Collections.synchronizedMap()

### 1024 Threads

Avaliando estatisticamente, no espaço amostral de tamanho 2^16 (com exceção das operações com latência de 0ns), temos que a latência da estrutura concurrentHashMap é menor que a da estrutura synchronizedMap para percentil 50 e 95 nos casos de write 50%/read 50% e  write 10%/read 90%, enquanto no caso de write 90%/read 10% eles são equivalentes para percentil 95, porém o concurrentHashMap continua tendo menor valor em percentil 50.
Analisando graficamente, podemos enxergar também que o concurrentHashMap tem um maior desempenho em relação a estrutura syncronizedMap.

### 128 Threads

Para os casos com 128 threads, temos que a concurrentHashMap tem um melhor desempenho para os casos de read 90/write 10 e read 50/write 50 enquanto o synchronizedMap tem um melhor desempenho read 10/write 90 para o percentil 50.

### 1 Thread

Para os casos com 1 thread, temos que a synchronizedMap tem menor latência para percentil 50 em todos os cenários de read/write avaliados no experimento.

# Letra B:

CopyOnWriteArrayList e Collections.synchronizedList()

### 1024/128 Threads

Como a estrutura CopyOnWriteArrayList permite fazer read sem dar lock, para os casos com maior taxa de leitura (read 90/write 10) o desempenho da estrutura é bem melhor quando comparada a synchronizedList que aplica o lock para fazer tanto leitura quanto escrita.

### 1 Thread

Para os caso com 1 thread apenas, podemos enxergar graficamente que o desempenho das duas estruturas é bem similar nos casos de read 90/write 10 e read 50/write 50, já no caso de read 10/write 90 a synchronizedList possue um desempenho melhor. 
