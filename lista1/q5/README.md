# Experimento
Para realizarmos a comparação, realizamos experimentos em uma VM com 8vCPUs e 16GB de RAM. Consideramos utilizar apenas 3 fatores: estrutura a ser experimentada, número de threads e proporção leitura/escrita (put/get ou add/get). Observamos a diferença de desempenho com base no tempo de execução da operação, medido em nanossegundos. Dito isso, o experimento consistiu invocação de 2^16 operações (65536) leitura/escrita na estrutura de dados a ser testada, sendo essas operações invocadas por diferentes números de threads (como mencionado.   

# Letra A:

ConcurrentHashMap e Collections.synchronizedMap()

Para as estruturas acima destacadas, constatamos que estatisticamente ConcurrentHashMap obteve melhor desempenho que Collections.synchronizedMap(). Abaixo, uma comparação dos cenários experimentados.

### 1 Thread
##### 50/50 read/write 
* SynchronizedMap tem melhor desempenho.
##### 10/90 read/write
* ConcurrentHashMap tem melhor desempenho.
##### 90/10 read/write
* ConcurrentHashMap tem melhor desempenho.

### 128 Threads
##### 50/50 read/write
* ConcurrentHashMap tem melhor desempenho.
##### 10/90 read/write
* SynchronizedMap tem melhor desempenho.
##### 90/10 read/write
* ConcurrentHashMap tem melhor desempenho.

### 1024 Threads
##### 50/50 read/write
* ConcurrentHashMap tem melhor desempenho.
##### 10/90 read/write
* ConcurrentHashMap tem melhor desempenho.
##### 90/10 read/write
* ConcurrentHashMap tem melhor desempenho.

# Letra B:

CopyOnWriteArrayList e Collections.synchronizedList()

Para as estruturas acima destacadas, constatamos que estatisticamente Collections.synchronizedList() obteve melhor desempenho que CopyOnWriteArrayList. Abaixo, uma comparação dos cenários experimentados. 

### 1024/128 Threads
##### 50/50 read/write
* SynchronizedList tem melhor desempenho.
##### 10/90 read/write
* SynchronizedList tem melhor desempenho.
##### 90/10 read/write
* CopyOnWriteArrayList um melhor desempenho.

### 1 Thread
##### 50/50 read/write
* Não houve evidência estatísticas que apontasse o melhor.
##### 10/90 read/write
* SynchronizedList tem melhor desempenho.
##### 90/10 read/write
* CopyOnWriteArrayList um melhor desempenho.

Como a estrutura CopyOnWriteArrayList permite fazer read sem dar lock, para os casos com maior taxa de leitura (read 90/write 10) o desempenho da estrutura é bem melhor quando comparada a synchronizedList que aplica o lock para fazer tanto leitura quanto escrita.
