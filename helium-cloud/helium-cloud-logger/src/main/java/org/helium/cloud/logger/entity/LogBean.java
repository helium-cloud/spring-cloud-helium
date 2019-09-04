package org.helium.cloud.logger.entity;

import com.alibaba.fastjson.annotation.JSONField;
import org.helium.logging.args.LogArgs;
import org.helium.logging.spi.LogEvent;

import java.util.Date;

public class LogBean extends LogArgs {

    @JSONField(name="level")
    private String level;

    @JSONField(name="name")
    private String name;

    @JSONField(name="event")
    private LogEvent event;

    @JSONField(name="@timestamp")
    private Date timestamp;

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LogEvent getEvent() {
        return event;
    }

    public void setEvent(LogEvent event) {
        this.event = event;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
