
graph_tail_map <- function(concurrentHash, synchronizedMap, title, x_limit_inf, x_limit_sup, annotate_y) {
  cmp <- rbind(
    data.frame("execution_time"=concurrentHash, Type="concurrentHash"),
    data.frame("execution_time"=synchronizedMap, Type="synchronizedMap")
  )
  concurrentHash.color <- "blue"
  concurrentHash.p999 <- quantile(concurrentHash, 0.9999)
  concurrentHash.p50 <- quantile(concurrentHash, 0.5)
  
  synchronizedMap.color <- "red"
  synchronizedMap.p999 <- quantile(synchronizedMap, 0.9999)
  synchronizedMap.p50 <- quantile(synchronizedMap, 0.5)

  size = 0.5
  alpha = 0.5
  angle = 90
  p <- ggplot(cmp, aes(execution_time, color=Type)) +
    stat_ecdf(size=size) +
    # P50
    annotate(geom="text", x=concurrentHash.p50, y=annotate_y, label="Median", angle=angle, color=concurrentHash.color) +
    geom_vline(xintercept=concurrentHash.p50, linetype="dotted", size=size, alpha=alpha, color=concurrentHash.color) +
    annotate(geom="text", x=synchronizedMap.p50, y=annotate_y, label="Median", angle=angle, color=synchronizedMap.color) + 
    geom_vline(xintercept=synchronizedMap.p50, linetype="dotted", size=size, alpha=alpha, color=synchronizedMap.color) +
    
    # P999
    annotate(geom="text", x=concurrentHash.p999, y=annotate_y, label="99.99th", angle=angle, color=concurrentHash.color) +
    geom_vline(xintercept=concurrentHash.p999, linetype="dotted", size=size, alpha=alpha, color=concurrentHash.color) +
    annotate(geom="text", x=synchronizedMap.p999, y=annotate_y, label="99.99th", angle=angle, color=synchronizedMap.color) + 
    geom_vline(xintercept=synchronizedMap.p999, linetype="dotted", size=size, alpha=alpha, color=synchronizedMap.color) +
    
    #scale_x_continuous(breaks=seq(0, max(cmp$latency), 10)) +
    #coord_cartesian(ylim = c(0.99, 1)) +
    xlim(x_limit_inf, x_limit_sup) +
    theme(legend.position="top") +
    scale_color_manual(breaks = c("concurrentHash", "synchronizedMap"), values=c("blue", "red")) +
    theme_bw() +
    ggtitle(title) +
    xlab("tempo de execução (ns)") +
    ylab("frequência") 
  
  print(p)
}

graph_tail_list <- function(copyOnWriteList, synchronizedList, title, x_limit_inf, x_limit_sup, annotate_y) {
  cmp <- rbind(
    data.frame("execution_time"=copyOnWriteList, Type="copyOnWriteList"),
    data.frame("execution_time"=synchronizedList, Type="synchronizedList")
  )
  copyOnWriteList.color <- "green"
  copyOnWriteList.p999 <- quantile(copyOnWriteList, 0.9999)
  copyOnWriteList.p50 <- quantile(copyOnWriteList, 0.5)

  synchronizedList.color <- "purple"
  synchronizedList.p999 <- quantile(synchronizedList, 0.9999)
  synchronizedList.p50 <- quantile(synchronizedList, 0.5)

  size = 0.5
  alpha = 0.5
  angle = 90
  p <- ggplot(cmp, aes(execution_time, color=Type)) +
    stat_ecdf(size=size) +
    # P50
    annotate(geom="text", x=copyOnWriteList.p50, y=annotate_y, label="Median", angle=angle, color=copyOnWriteList.color) +
    geom_vline(xintercept=copyOnWriteList.p50, linetype="dotted", size=size, alpha=alpha, color=copyOnWriteList.color) +
    annotate(geom="text", x=synchronizedList.p50, y=annotate_y, label="Median", angle=angle, color=synchronizedList.color) + 
    geom_vline(xintercept=synchronizedList.p50, linetype="dotted", size=size, alpha=alpha, color=synchronizedList.color) +
    
    # P999
    annotate(geom="text", x=copyOnWriteList.p999, y=annotate_y, label="99.99th", angle=angle, color=copyOnWriteList.color) + 
    geom_vline(xintercept=copyOnWriteList.p999, linetype="dotted", size=size, alpha=alpha, color=copyOnWriteList.color) +
    annotate(geom="text", x=synchronizedList.p999, y=annotate_y, label="99.99th", angle=angle, color=synchronizedList.color) + 
    geom_vline(xintercept=synchronizedList.p999, linetype="dotted", size=size, alpha=alpha, color=synchronizedList.color) +
    
    #scale_x_continuous(breaks=seq(0, max(cmp$latency), 10)) +
    #coord_cartesian(ylim = c(0.99, 1)) +
    xlim(x_limit_inf, x_limit_sup) +
    theme(legend.position="top") +
    scale_color_manual(breaks = c("copyOnWriteList", "synchronizedList"), values=c("green", "purple")) +
    theme_bw() +
    ggtitle(title) +
    xlab("tempo de execução (ns)") +
    ylab("frequência") 
  
  print(p)
}