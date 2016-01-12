package se.hig.aod.projekt;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Using BigDecimal<br>
 * This allows users to have HUGE numbers and infinite precision for normal
 * arithmetics<br>
 * Note that using Math methods decreases the precision to the precision of
 * 'double'
 * 
 * @author Viktor Hanstorp (ndi14vhp@student.hig.se)
 */
class LispNumber implements Comparable<LispNumber>
{
    boolean isfloat;
    BigDecimal value;

    LispNumber(BigDecimal value, boolean isfloat)
    {
        this.value = value;
        this.isfloat = isfloat;
    }

    public LispNumber(String source)
    {
        try
        {
            value = new BigDecimal(Integer.parseInt(source));
            isfloat = false;
        }
        catch (NumberFormatException notInt)
        {
            try
            {
                value = new BigDecimal(Float.parseFloat(source));
                isfloat = true;
            }
            catch (NumberFormatException notFloat)
            {
                try
                {
                    value = new BigDecimal(source);
                    isfloat = true;
                }
                catch (NumberFormatException notDecimal)
                {
                    throw new NumberFormatException("[" + source + "] is not a number");
                }
            }
        }
    }

    public BigDecimal floatiness(LispNumber other)
    {
        if (isfloat)
            return other.value.round(new MathContext(MathContext.UNLIMITED.getPrecision(), RoundingMode.DOWN));
        else
            return other.value;
    }

    public LispNumber add(LispNumber other)
    {
        return new LispNumber(value.add(floatiness(other)), isfloat || other.isfloat);
    }

    public LispNumber sub(LispNumber other)
    {
        return new LispNumber(value.subtract(floatiness(other)), isfloat || other.isfloat);
    }

    public LispNumber mul(LispNumber other)
    {
        return new LispNumber(value.multiply(floatiness(other)), isfloat || other.isfloat);
    }

    public LispNumber abs()
    {
        return new LispNumber(value.abs(), isfloat);
    }

    public LispNumber max(LispNumber other)
    {
        return new LispNumber(value.max(other.value), isfloat || other.isfloat);
    }

    public LispNumber min(LispNumber other)
    {
        return new LispNumber(value.min(other.value), isfloat || other.isfloat);
    }

    public LispNumber div(LispNumber other)
    {
        BigDecimal decimal = value.divide(floatiness(other));
        boolean newIsFloat = isfloat || other.isfloat;

        if (newIsFloat)
            decimal = decimal.round(new MathContext(MathContext.UNLIMITED.getPrecision(), RoundingMode.DOWN));

        return new LispNumber(decimal, newIsFloat);
    }

    public LispNumber sin()
    {
        return new LispNumber(new BigDecimal(Math.sin(value.doubleValue())), true);
    }

    public LispNumber cos()
    {
        return new LispNumber(new BigDecimal(Math.cos(value.doubleValue())), true);
    }

    public LispNumber tan()
    {
        return new LispNumber(new BigDecimal(Math.tan(value.doubleValue())), true);
    }

    public LispNumber asin()
    {
        return new LispNumber(new BigDecimal(Math.asin(value.doubleValue())), true);
    }

    public LispNumber acos()
    {
        return new LispNumber(new BigDecimal(Math.acos(value.doubleValue())), true);
    }

    public LispNumber atan()
    {
        return new LispNumber(new BigDecimal(Math.atan(value.doubleValue())), true);
    }

    public LispNumber atan2(LispNumber other)
    {
        return new LispNumber(new BigDecimal(Math.atan2(value.doubleValue(), other.value.doubleValue())), true);
    }

    public LispNumber ceil()
    {
        return new LispNumber(value.round(new MathContext(MathContext.UNLIMITED.getPrecision(), RoundingMode.CEILING)), true);
    }

    public LispNumber floor()
    {
        return new LispNumber(value.round(new MathContext(MathContext.UNLIMITED.getPrecision(), RoundingMode.FLOOR)), true);
    }

    public LispNumber round()
    {
        return new LispNumber(value.round(new MathContext(MathContext.UNLIMITED.getPrecision(), RoundingMode.HALF_UP)), true);
    }

    public LispNumber exp(LispNumber other)
    {
        return new LispNumber(new BigDecimal(Math.pow(value.doubleValue(), other.value.doubleValue())), true);
    }

    public LispNumber sqrt()
    {
        return new LispNumber(new BigDecimal(Math.sqrt(value.doubleValue())), true);
    }

    @Override
    public int compareTo(LispNumber other)
    {
        return value.compareTo(other.value);
    }

    public int compareTo(Integer other)
    {
        return value.compareTo(new BigDecimal(other));
    }

    public int compareTo(Float other)
    {
        return value.compareTo(new BigDecimal(other));
    }

    @Override
    public String toString()
    {
        return value.toString() + ((isfloat && value.scale() == 0) ? ".0" : "");
    }
}