package se.hig.aod.projekt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class PartCons extends Part
{
    Part car;
    Part cdr;

    PartCons()
    {
    }

    PartCons(Part car, Part cdr)
    {
        this.car = car;
        this.cdr = cdr;
    }

    PartCons(Part[] list)
    {
        car = list[0];
        cdr = list.length > 1 ? new PartCons(Arrays.copyOfRange(list, 1, list.length)) : Lisp.NIL;
    }

    PartCons(Part[] list, Part end_cdr)
    {
        car = list[0];
        cdr = list.length > 1 ? new PartCons(Arrays.copyOfRange(list, 1, list.length), end_cdr) : end_cdr;
    }

    Part[] asArray()
    {
        if (equals(Lisp.NIL))
            return new Part[0];

        List<Part> array = new ArrayList<Part>();
        array.add(car != null ? car : Lisp.NIL);

        Part element = this;

        while ((element = ((PartCons) element).cdr) instanceof PartCons && !((PartCons) element).equals(Lisp.NIL))
            array.add(((PartCons) element).car);

        if (element instanceof PartAtom)
            array.add(element);

        return array.toArray(new Part[0]);
    }

    public String asConsString()
    {
        return "(" + car + " . " + cdr + ")";
    }

    public String asListString()
    {
        StringBuilder builder = new StringBuilder();

        builder.append("(");
        Part element = this;
        do
        {
            builder.append(((PartCons) element).car.asString());
            builder.append(" ");
        }
        while ((element = ((PartCons) element).cdr) instanceof PartCons && !((PartCons) element).equals(Lisp.NIL));

        // element is atom or NIL

        if (element.equals(Lisp.NIL))
            builder.deleteCharAt(builder.length() - 1);
        else
        {
            builder.append(". ");
            builder.append(element.asString());
        }

        builder.append(")");

        return builder.toString();
    }

    @Override
    public String asString()
    {
        return asListString();
    }

    @Override
    public boolean equals(Object other)
    {
        return other instanceof PartCons && car == ((PartCons) other).car && cdr == ((PartCons) other).cdr;
    }
}