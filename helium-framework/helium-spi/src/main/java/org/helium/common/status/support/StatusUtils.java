//
//package org.helium.common.status.support;
//
//import Status;
//
//import java.util.Map;
//
///**
// * StatusManager
// */
//public class StatusUtils {
//
//    public static Status getSummaryStatus(Map<String, Status> statuses) {
//        Status.Level level = Status.Level.OK;
//        StringBuilder msg = new StringBuilder();
//        for (Map.Entry<String, Status> entry : statuses.entrySet()) {
//            String key = entry.getKey();
//            Status status = entry.getValue();
//            Status.Level l = status.getLevel();
//            if (Status.Level.ERROR.equals(l)) {
//                level = Status.Level.ERROR;
//                if (msg.length() > 0) {
//                    msg.append(",");
//                }
//                msg.append(key);
//            } else if (Status.Level.WARN.equals(l)) {
//                if (!Status.Level.ERROR.equals(level)) {
//                    level = Status.Level.WARN;
//                }
//                if (msg.length() > 0) {
//                    msg.append(",");
//                }
//                msg.append(key);
//            }
//        }
//        return new Status(level, msg.toString());
//    }
//
//}