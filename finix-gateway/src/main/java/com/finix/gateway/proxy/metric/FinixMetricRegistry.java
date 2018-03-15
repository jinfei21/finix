package com.finix.gateway.proxy.metric;

import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistryListener;
import com.codahale.metrics.Timer;

public class FinixMetricRegistry implements MetricRegistry {
	
    private final com.codahale.metrics.MetricRegistry metricRegistry;
    
    public FinixMetricRegistry(com.codahale.metrics.MetricRegistry metricRegistry) {
        this.metricRegistry = metricRegistry;
    }
	
    public FinixMetricRegistry() {
        this(new com.codahale.metrics.MetricRegistry());
    }

	@Override
	public MetricRegistry scope(String name) {
		return new ScopedMetricRegistry(name, this);
	}

	@Override
	public <T extends Metric> T register(String name, T metric) throws IllegalArgumentException {
        return metricRegistry.register(name, metric);
	}

	@Override
	public boolean deregister(String name) {
        return metricRegistry.remove(name);
	}
    @Override
    public Counter counter(String name) {
        return metricRegistry.counter(name);
    }

    @Override
    public Histogram histogram(String name) {
        return metricRegistry.histogram(name);
    }

    @Override
    public Meter meter(String name) {
        return metricRegistry.meter(name);
    }

    @Override
    public Timer timer(String name) {
        Map<String, Metric> metrics = metricRegistry.getMetrics();

        Metric metric = metrics.get(name);

        if (metric instanceof Timer) {
            return (Timer) metric;
        }

        if (metric == null) {
            try {
                return register(name, newTimer());
            } catch (IllegalArgumentException e) {
                Metric added = metrics.get(name);
                if (added instanceof Timer) {
                    return (Timer) added;
                }
            }
        }
        throw new IllegalArgumentException(name + " is already used for a different type of metric");
    }

    private Timer newTimer() {
        return new SampleCountFromSnapshotTimer(new SlidingWindowHistogramReservoir());
    }

    @Override
    public void addListener(MetricRegistryListener listener) {
        this.metricRegistry.addListener(listener);
    }

    @Override
    public void removeListener(MetricRegistryListener listener) {
        this.metricRegistry.removeListener(listener);
    }

    @Override
    public SortedSet<String> getNames() {
        return metricRegistry.getNames();
    }

    @Override
    public SortedMap<String, Gauge> getGauges() {
        return metricRegistry.getGauges();
    }

    @Override
    public SortedMap<String, Gauge> getGauges(MetricFilter filter) {
        return metricRegistry.getGauges(filter);
    }

    @Override
    public SortedMap<String, Counter> getCounters() {
        return metricRegistry.getCounters();
    }

    @Override
    public SortedMap<String, Counter> getCounters(MetricFilter filter) {
        return metricRegistry.getCounters(filter);
    }

    @Override
    public SortedMap<String, Histogram> getHistograms() {
        return metricRegistry.getHistograms();
    }

    @Override
    public SortedMap<String, Histogram> getHistograms(MetricFilter filter) {
        return metricRegistry.getHistograms(filter);
    }

    @Override
    public SortedMap<String, Meter> getMeters() {
        return metricRegistry.getMeters();
    }

    @Override
    public SortedMap<String, Meter> getMeters(MetricFilter filter) {
        return metricRegistry.getMeters(filter);
    }

    @Override
    public SortedMap<String, Timer> getTimers() {
        return metricRegistry.getTimers();
    }

    @Override
    public SortedMap<String, Timer> getTimers(MetricFilter filter) {
        return metricRegistry.getTimers(filter);
    }

    /**
     * Concatenates elements to form a dotted name, omitting null values and empty strings.
     *
     * @param name     the first element of the name
     * @param names    the remaining elements of the name
     * @return {@code name} and {@code names} concatenated by dots
     */
    public static String name(String name, String... names) {
        StringBuilder builder = new StringBuilder();
        append(builder, name);
        if (names != null) {
            for (String s : names) {
                append(builder, s);
            }
        }
        return builder.toString();
    }

    /**
     * Concatenates a class name and elements to form a dotted name, omitting null values and empty strings.
     *
     * @param klass    the first element of the name
     * @param names    the remaining elements of the name
     * @return {@code klass} and {@code names} concatenated by dots
     */
    public static String name(Class<?> klass, String... names) {
        return name(klass.getName(), names);
    }

    private static void append(StringBuilder builder, String part) {
        if (part != null && !part.isEmpty()) {
            if (builder.length() > 0) {
                builder.append('.');
            }
            builder.append(part);
        }
    }
    
    private static class SampleCountFromSnapshotTimer extends Timer {
        public SampleCountFromSnapshotTimer(SlidingWindowHistogramReservoir slidingWindowHistogramReservoir) {
            super(slidingWindowHistogramReservoir);
        }

        @Override
        public long getCount() {
            return (long) getSnapshot().size();
        }
    }

}
