package com.finix.framework.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.finix.framework.core.Binder;
import com.finix.framework.core.BinderFactory;
import com.finix.framework.core.BinderSupporter;

public class DefaultFiltersSupport {

    public static List<Filter> getDefaultFilters() {
        List<Filter> filters = new ArrayList<>();
        Map<String, Binder<?>> binderDefines = BinderFactory.getInstance().getBinderDefines(Filter.class);
        for (Binder<?> binderDefine : binderDefines.values()) {
            Filter filter = (Filter) BinderSupporter.newInstance(binderDefine.getBinderClass());
            filters.add(filter);
        }
        return filters;
    }
}