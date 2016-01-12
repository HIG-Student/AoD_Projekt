package se.hig.aod.projekt;

class PartLambda extends PartAtom
{
    public final Lambda<PartContainer, PartContainer> value;

    PartLambda(Lambda<PartContainer, PartContainer> value)
    {
        this.value = value;
    }

    @Override
    public String asString()
    {
        return "[lambda]";
    }
}