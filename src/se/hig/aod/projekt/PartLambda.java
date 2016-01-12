package se.hig.aod.projekt;


class PartLambda extends PartAtom
{
    public final Lambda<Part, Part> value;

    PartLambda(Lambda<Part, Part> value)
    {
        this.value = value;
    }

    @Override
    public String asString()
    {
        return "[lambda]";
    }
}