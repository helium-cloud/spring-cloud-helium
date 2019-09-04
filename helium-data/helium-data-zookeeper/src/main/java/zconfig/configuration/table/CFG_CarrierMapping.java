package zconfig.configuration.table;


import zconfig.configuration.args.ConfigTableField;
import zconfig.configuration.args.ConfigTableItem;

/**
 * Created by liufeng on 2016/1/18.
 */

public class CFG_CarrierMapping extends ConfigTableItem
{
    @ConfigTableField(value = "MappingStart", isKeyField = true)
    private long mappingStart;

    @ConfigTableField("MappingEnd")
    private long mappingEnd;

    @ConfigTableField("CarrierName")
    private String carrierName = "";

    @ConfigTableField("LogicalPoolCapacity")
    private int logicalPoolCapacity;

    @ConfigTableField("LogicalPoolStart")
    private int logicalPoolStart;

    @ConfigTableField("LogicalPoolCount")
    private int logicalPoolCount;

    public long getMappingStart()
    {
        return mappingStart;
    }

    public long getMappingEnd()
    {
        return mappingEnd;
    }

    public String getCarrierName()
    {
        return carrierName;
    }

    public int getLogicalPoolCapacity()
    {
        return logicalPoolCapacity;
    }

    public int getLogicalPoolStart()
    {
        return logicalPoolStart;
    }

    public int getLogicalPoolCount()
    {
        return logicalPoolCount;
    }
}
