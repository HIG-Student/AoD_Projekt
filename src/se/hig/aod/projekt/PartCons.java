package se.hig.aod.projekt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class PartCons extends Part
{
    PartContainer car;
    PartContainer cdr;

    PartCons()
    {
    }

    PartCons(PartContainer car, PartContainer cdr)
    {
        this.car = car;
        this.cdr = cdr;
    }

    PartCons(PartContainer[] list)
    {
        car = list[0];
        cdr = new PartContainer(list.length > 1 ? new PartCons(Arrays.copyOfRange(list, 1, list.length)) : Lisp.NIL);
    }

    PartCons(PartContainer[] list, PartContainer end_cdr)
    {
        car = list[0];
        cdr = list.length > 1 ? new PartContainer(new PartCons(Arrays.copyOfRange(list, 1, list.length), end_cdr)) : end_cdr;
    }

    PartContainer[] asArray()
    {
        if (equals(Lisp.NIL))
            return new PartContainer[0];

        List<PartContainer> array = new ArrayList<PartContainer>();
        array.add(car);

        if (cdr.getPart() instanceof PartCons)
            array.addAll(Arrays.asList(((PartCons) cdr.getPart()).asArray()));
        else
            array.add(cdr);

        return array.toArray(new PartContainer[0]);
    }

    PartCons[] asConsArray()
    {
        if (equals(Lisp.NIL))
            return new PartCons[0];

        List<PartCons> array = new ArrayList<PartCons>();
        array.add(this);

        Part element = this;

        while ((element = ((PartCons) element).cdr.getPart()) instanceof PartCons && !element.equals(Lisp.NIL))
        {
            array.add((PartCons) element);
        }

        return array.toArray(new PartCons[0]);
    }

    public String asConsString()
    {
        return "(" + car.getPart().asString() + " . " + cdr.getPart().asString() + ")";
    }

    public String asListString()
    {
        StringBuilder builder = new StringBuilder();

        PartCons[] array = asConsArray();
        
        builder.append("(");

        builder.append(array[0].car.getPart().asString());
        
        for (int i = 1; i < array.length; i++)
        {
            builder.append(" ");
            builder.append(array[i].car.getPart().asString());
        }

        if (!array[array.length - 1].cdr.getPart().equals(Lisp.NIL))
        {
            builder.append(" . ");
            builder.append(array[array.length - 1].cdr.getPart().asString());
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
        if (other == null || !(other instanceof PartCons))
            return false;

        if (car != null && ((PartCons) other).car != null && car.equals(((PartCons) other).car))
            if (cdr != null && ((PartCons) other).cdr != null && cdr.equals(((PartCons) other).cdr))
                return true;

        return car == null && cdr == null && ((PartCons) other).car == null && ((PartCons) other).cdr == null;
    }
}