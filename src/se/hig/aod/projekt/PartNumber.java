package se.hig.aod.projekt;

import java.math.BigDecimal;

class PartNumber extends PartValue<LispNumber>
{
    public boolean isFloat()
    {
        return value.isfloat;
    }

    PartNumber(LispNumber value)
    {
        super(value);
    }

    PartNumber(Integer value)
    {
        super(new LispNumber(new BigDecimal(value), false));
    }

    PartNumber(Float value)
    {
        super(new LispNumber(new BigDecimal(value), true));
    }
    
    PartNumber(Double value)
    {
        super(new LispNumber(new BigDecimal(value), true));
    }
}