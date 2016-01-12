package se.hig.aod.projekt;

class PartValue<T> extends PartAtom
{
    public final T value;
    public final Class<?> classType;

    PartValue(T value)
    {
        this.value = value;
        this.classType = value.getClass();
    }

    @Override
    public String asString()
    {
        return value.toString();
    }
}