package com.anti.fraud.common.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MonitorConfig {

    @Bean
    public Timer simulationTimer(MeterRegistry registry) {
        return Timer.builder("simulation.execution.time")
                .description("模拟演练执行时间")
                .tags("type", "simulation")
                .register(registry);
    }

    @Bean
    public io.micrometer.core.instrument.Counter simulationCounter(MeterRegistry registry) {
        return io.micrometer.core.instrument.Counter.builder("simulation.execution.count")
                .description("模拟演练执行次数")
                .tags("type", "simulation")
                .register(registry);
    }

    @Bean
    public io.micrometer.core.instrument.Gauge simulationScoreGauge(MeterRegistry registry) {
        return io.micrometer.core.instrument.Gauge.builder("simulation.average.score", () -> {
            // 这里可以实现从数据库或缓存中获取平均得分的逻辑
            return 0.0;
        })
                .description("模拟演练平均得分")
                .tags("type", "simulation")
                .register(registry);
    }
}
