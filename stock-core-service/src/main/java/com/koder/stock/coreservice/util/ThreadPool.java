package com.koder.stock.coreservice.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@Slf4j
public class ThreadPool {

    ExecutorService executorService = Executors.newFixedThreadPool(10);

    public void submitRunnable(AbstractTask task) {
        log.warn("Task [{}] running",task.getTaskName());
        executorService.submit(task);
    }

    @PreDestroy
    public void containerDown(){
        executorService.shutdownNow();
    }
}
