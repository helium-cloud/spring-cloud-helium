package org.helium.perfmon.monitor;

import org.helium.perfmon.observation.Observable;
import org.helium.perfmon.observation.ObserverReportColumn;
import org.helium.framework.entitys.perfmon.Category;
import org.helium.framework.entitys.perfmon.ReportColumn;

import java.util.stream.Collectors;

/**
 * Created by Coral on 2015/8/17.
 */
public class EntityUtil {

    public static Category convert(Observable observable) {
        Category result = new Category();
        result.setName(observable.getObserverName());
        result.setInstance(observable.getObserverUnits().size());
        result.setColumns(observable.getObserverColumns().stream().map(EntityUtil::convert).collect(Collectors.toList()));
        return result;
    }

    public static ReportColumn convert(ObserverReportColumn column) {
        ReportColumn result = new ReportColumn();
        result.setName(column.getName());
        return result;
    }
}
