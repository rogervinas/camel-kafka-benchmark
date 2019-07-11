package com.rogervinas.camelkafkabenchmark.metrics;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static java.lang.System.currentTimeMillis;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;

class MetricsServiceTest {

  @Test
  void shouldReset() {
    // Act
    final MetricsService service = new MetricsService();
    service.inc();
    service.inc();
    service.inc();
    service.reset();

    // Assert
    final MetricsService.Metrics result = service.result();
    assertThat(result.threadCount).isZero();
    assertThat(result.totalCount).isZero();
    assertThat(result.mean).isNaN();
    assertThat(result.stddev).isNaN();
    assertThat(result.duration).isZero();
  }

  @ParameterizedTest
  @ValueSource(ints = {1, 5, 10, 15})
  void shouldWorkWithEquallyDistributedThreads(int numberOfThreads) {
    // Arrange
    final long startTime = currentTimeMillis();
    final int countsPerThread = 7;

    // Act
    final MetricsService service = new MetricsService();
    IntStream.rangeClosed(1, numberOfThreads).boxed()
        .map(i -> inc(service, countsPerThread))
        .forEach(this::wait);
    final long endTime = currentTimeMillis();

    // Assert
    final MetricsService.Metrics result = service.result();
    assertThat(result.threadCount).isEqualTo(numberOfThreads);
    assertThat(result.totalCount).isEqualTo(numberOfThreads * countsPerThread);
    assertThat(result.mean).isEqualTo(countsPerThread);
    assertThat(result.stddev).isEqualTo(0);
    assertThat(result.duration).isCloseTo(endTime - startTime, offset(10L));
  }

  @Test
  void shouldWorkWithUnequallyDistributedThreads() {
    // Arrange
    final long startTime = currentTimeMillis();
    final int incs1 = 7;
    final int incs2 = 11;
    final int incs3 = 3;
    final int incs4 = 23;

    // Act
    final MetricsService service = new MetricsService();
    wait(inc(service, incs1));
    wait(inc(service, incs2));
    wait(inc(service, incs3));
    wait(inc(service, incs4));
    final long endTime = currentTimeMillis();

    // Assert
    final MetricsService.Metrics result = service.result();
    assertThat(result.threadCount).isEqualTo(4);
    assertThat(result.totalCount).isEqualTo(incs1 + incs2 + incs3 + incs4);
    assertThat(result.mean).isEqualTo((incs1 + incs2 + incs3 + incs4) / 4.0);
    assertThat(result.stddev).isCloseTo(8.64, offset(0.1));
    assertThat(result.duration).isCloseTo(endTime - startTime, offset(10L));
  }

  private Future<?> inc(MetricsService service, int numberOfIncs) {
    return Executors.newSingleThreadExecutor().submit(() -> {
      for (int j=0; j<numberOfIncs; j++) {
        service.inc();
      }
    });
  }

  private void wait(Future<?> future) {
    try {
      future.get();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}