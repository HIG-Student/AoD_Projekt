package se.hig.aod.projekt;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;

//Legend:
//?: not started
//>: currently working on
//T: needs testing
//!: done!

//TODO: ? deffun
//TODO: ? defstruct
//TODO: > cons
//TODO: ? vector
//TODO: ? array
//TODO: ? cond
//TODO: ? lambda asString()

/**
 * A class that do magic on lisp expressions <br>
 * <br>
 * Inspired by: <a href="http://norvig.com/lispy.html">lispy</a> and <a href=
 * "http://www.michaelnielsen.org/ddi/lisp-as-the-maxwells-equations-of-software/"
 * >blog by Michael Nielsen</a>
 *
 * @author Viktor Hanstorp (ndi14vhp@student.hig.se)
 */
public class Lisp
{
    static abstract class Part
    {
        public abstract String asString();

        @Override
        public String toString()
        {
            return asString();
        }
    }

    static final PartCons NIL = new PartCons()
    {
        @Override
        public String asListString()
        {
            return "NIL";
        }

        @Override
        public String asConsString()
        {
            return "NIL";
        }
    };

    static class PartCons extends Part
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
            cdr = list.length > 1 ? new PartCons(Arrays.copyOfRange(list, 1, list.length)) : NIL;
        }

        PartCons(Part[] list, Part end_cdr)
        {
            car = list[0];
            cdr = list.length > 1 ? new PartCons(Arrays.copyOfRange(list, 1, list.length), end_cdr) : end_cdr;
        }

        Part[] asArray()
        {
            if (equals(NIL))
                return new Part[0];

            List<Part> array = new ArrayList<Part>();
            array.add(car != null ? car : NIL);

            Part element = this;

            while ((element = ((PartCons) element).cdr) instanceof PartCons && !((PartCons) element).equals(NIL))
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
            while ((element = ((PartCons) element).cdr) instanceof PartCons && !((PartCons) element).equals(NIL));

            // element is atom or NIL

            if (element.equals(NIL))
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

    // any object that is not a cons
    static abstract class PartAtom extends Part
    {

    }

    static class PartValue<T> extends PartAtom
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

    static class PartNumber extends PartValue<LispNumber>
    {
        public boolean isFloat()
        {
            return value.isfloat;
        }

        PartNumber(LispNumber value)
        {
            super(value);
        }
    }

    static class PartString extends PartValue<String>
    {
        PartString(String value)
        {
            super(value);
        }
    }

    static class PartBoolean extends PartValue<Boolean>
    {
        PartBoolean(Boolean value)
        {
            super(value);
        }
    }

    static class PartSymbol extends PartAtom
    {
        public final String value;

        PartSymbol(String value)
        {
            this.value = value;
        }

        @Override
        public String asString()
        {
            return value;
        }
    }

    static class PartLambda extends PartAtom
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

    /**
     * Using BigDecimal<br>
     * This allows users to have HUGE numbers and infinite precision for normal
     * arithmetics<br>
     * Note that using Math methods decreases the precision to the precision of
     * 'double'
     * 
     * @author Viktor Hanstorp (ndi14vhp@student.hig.se)
     */
    static class LispNumber implements Comparable<LispNumber>
    {
        private boolean isfloat;
        private BigDecimal value;

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

        @Override
        public String toString()
        {
            return value.toString() + ((isfloat && value.scale() == 0) ? ".0" : "");
        }
    }

    class CharactersToParseStack
    {
        public final String source;
        private int index = 0;

        CharactersToParseStack(String source)
        {
            this.source = source;
        }

        int getSize()
        {
            return source.length() - index;
        }

        char pop()
        {
            if (getSize() == 0)
                throw new StackEmpty("No more chars");

            return source.charAt(index++);
        }

        void unpop()
        {
            if (index == 0)
                throw new OutOfBounds("No more chars");

            index--;
        }

        char peak()
        {
            return peak(0);
        }

        char peak(int pos)
        {
            if (getSize() == 0)
                throw new StackEmpty("No more chars");

            if (index + pos < 0 || index + pos >= source.length())
                throw new OutOfBounds("Out of bounds");

            return source.charAt(index + pos);
        }

        boolean nextIs(char c)
        {
            if (getSize() == 0)
                throw new StackEmpty("No more chars");

            return peak() == c;
        }

        boolean nextIs(String s)
        {
            if (getSize() == 0)
                throw new StackEmpty("No more chars");

            if (getSize() < s.length())
                return false;

            for (int i = 0; i < s.length(); i++)
                if (s.charAt(i) != peak(i + 1))
                    return false;

            return true;
        }

        boolean nextIsSpace()
        {
            return isSpace(peak());
        }

        boolean isSpace(char c)
        {
            return c == ' ' || c == '\n' || c == '\r' || c == '\t';
        }

        boolean isParseEnd(char c)
        {
            return c == ')' || isSpace(c);
        }

        boolean isStringBreak(char c)
        {
            return c == '\n' || c == '\r';
        }

        boolean clean()
        {
            char character = peak();
            if (isSpace(character))
            {
                pop();
                clean();
                return true;
            }

            if (character == ';')
            {
                while (!isStringBreak(character = pop()))
                {
                }
                clean();
                return true;
            }

            if (character == '#' && peak(1) == '|')
            {
                while ((character = pop()) != '#')
                {
                    if (getSize() == 0)
                        throw new SyntaxError("Unclosed multiline-comment!");

                    if (character == '#' && peak(-1) == '|')
                        break;
                }
                clean();
                return true;
            }

            return false;
        }

        @SuppressWarnings("serial")
        class StackEmpty extends LispException
        {
            public StackEmpty(String message)
            {
                super(message);
            }
        }

        @SuppressWarnings("serial")
        class OutOfBounds extends LispException
        {
            public OutOfBounds(String message)
            {
                super(message);
            }
        }
    }

    class Environment
    {
        Environment outer = null;
        @SuppressWarnings("serial")
        HashMap<String, Part> env = new HashMap<String, Part>();

        public Environment()
        {

        }

        public Environment(Environment outer)
        {
            this.outer = outer;
        }

        public Part get(String key)
        {
            if (env.containsKey(key))
                return env.get(key);
            else
                if (outer != null)
                    return outer.get(key);
                else
                    throw new NoSuchVariable("Variable [" + key + "] is null!");
        }

        public Part set(String key, Part entry)
        {
            env.put(key, entry);
            return entry;
        }

        @SuppressWarnings("serial")
        class NoSuchVariable extends LispException
        {
            NoSuchVariable(String msg)
            {
                super(msg);
            }
        }
    }

    interface Lambda<P, R>
    {
        public R exec(@SuppressWarnings("unchecked") P... args);
    }

    interface LambdaWithOneParameter<P, R>
    {
        public R exec(P arg);
    }

    interface LambdaWithTwoParameters<P, R>
    {
        public R exec(P arg1, P arg2);
    }

    interface LambdaWithTwoDiffrentParameters<P1, P2, R>
    {
        public R exec(P1 arg1, P2 arg2);
    }

    interface Check
    {
        boolean check(Part p);
    }

    @SuppressWarnings({ "rawtypes" })
    Environment global_enviroment = new Environment()
    {
        Check isNumber = (p) -> p instanceof PartNumber;
        Check isAny = (p) -> true;

        PartValue<Boolean> boolNumberOp(Part[] parts, LambdaWithTwoParameters<LispNumber, Boolean> op)
        {
            try
            {
                if (parts.length < 2)
                    throw new IncorrectParameters();

                boolean result;
                LispNumber last;
                result = op.exec(((PartNumber) parts[0]).value, last = ((PartNumber) parts[1]).value);
                for (int i = 2; i < parts.length; i++)
                    result = result && op.exec(last, last = ((PartNumber) parts[i]).value);
                return new PartValue<Boolean>(result);
            }
            catch (IncorrectParameters | ClassCastException e)
            {
                throw new IncorrectParameters("num num [num...]");
            }
        }

        PartValue<LispNumber> numOp(Part[] parts, LambdaWithTwoParameters<LispNumber, LispNumber> op)
        {
            try
            {
                if (parts.length < 2)
                    throw new IncorrectParameters();

                LispNumber result;
                result = op.exec(((PartNumber) parts[0]).value, ((PartNumber) parts[1]).value);
                for (int i = 2; i < parts.length; i++)
                    result = op.exec(result, ((PartNumber) parts[i]).value);
                return new PartNumber(result);
            }
            catch (IncorrectParameters | ClassCastException e)
            {
                throw new IncorrectParameters("num num [num...]");
            }
        }

        PartValue<LispNumber> num(Part[] parts, LambdaWithOneParameter<LispNumber, LispNumber> op)
        {
            try
            {
                if (parts.length != 1)
                    throw new IncorrectParameters();
                return new PartNumber(op.exec(((PartNumber) parts[0]).value));
            }
            catch (IncorrectParameters | ClassCastException e)
            {
                throw new IncorrectParameters("num");
            }
        }

        boolean c(Part[] args, Check... checks)
        {
            if (args.length != checks.length)
                return false;

            for (int i = 0; i < checks.length; i++)
                if (!checks[i].check(args[i]))
                    return false;

            return true;
        }

        boolean fc(Part[] args, Check... checks)
        {
            if (!c(args, checks))
                throw new IncorrectParameters();
            return true;
        }

        Part e()
        {
            throw new IncorrectParameters();
        }

        void set(String key, Lambda<Part, Part> lambda)
        {
            set(key, new PartLambda(lambda));
        }

        {
            set("+", (args) -> numOp(args, (a, b) -> a.add(b)));
            set("-", (args) -> numOp(args, (a, b) -> a.sub(b)));
            set("*", (args) -> numOp(args, (a, b) -> a.mul(b)));
            set("/", (args) -> numOp(args, (a, b) -> a.div(b)));

            set(">", (args) -> boolNumberOp(args, (a, b) -> a.compareTo(b) > 0));
            set("<", (args) -> boolNumberOp(args, (a, b) -> a.compareTo(b) < 0));
            set(">=", (args) -> boolNumberOp(args, (a, b) -> a.compareTo(b) >= 0));
            set("<=", (args) -> boolNumberOp(args, (a, b) -> a.compareTo(b) <= 0));
            set("=", (args) -> boolNumberOp(args, (a, b) -> a.compareTo(b) == 0));

            set("eq", (args) -> fc(args, isAny, isAny) ? new PartValue<Boolean>(args[0] == args[1]) : null);

            set("max", (args) -> numOp(args, (a, b) -> a.max(b)));
            set("min", (args) -> numOp(args, (a, b) -> a.min(b)));

            set("abs", (args) -> num(args, (a) -> a.abs()));
            set("sin", (args) -> num(args, (a) -> a.sin()));
            set("cos", (args) -> num(args, (a) -> a.cos()));
            set("tan", (args) -> num(args, (a) -> a.tan()));
            set("asin", (args) -> num(args, (a) -> a.asin()));
            set("acos", (args) -> num(args, (a) -> a.acos()));
            set("atan", (args) -> num(args, (a) -> a.atan()));
            set("atan2", (args) -> fc(args, isAny, isAny) ? numOp(args, (a, b) -> a.atan2(b)) : null);

            set("ceil", (args) -> num(args, (a) -> a.ceil()));
            set("floor", (args) -> num(args, (a) -> a.floor()));
            set("round", (args) -> num(args, (a) -> a.round()));
            set("sqrt", (args) -> num(args, (a) -> a.sqrt()));

            set("exp", (args) -> fc(args, isAny, isAny) ? numOp(args, (a, b) -> a.exp(b)) : null);

            env.put("nil", NIL);
        }
    };

    /**
     * Exception generated by the lisp parser
     *
     * @author Viktor Hanstorp (ndi14vhp@student.hig.se)
     */
    @SuppressWarnings("serial")
    public class LispException extends RuntimeException
    {
        LispException()
        {
        }

        LispException(String msg)
        {
            super(msg);
        }
    }

    /**
     * Indicates that there is something wrong with the syntax
     *
     * @author Viktor Hanstorp (ndi14vhp@student.hig.se)
     */
    @SuppressWarnings("serial")
    public class SyntaxError extends LispException
    {
        SyntaxError(String message)
        {
            super(message);
        }

        SyntaxError()
        {
        }
    }

    /**
     * Indicates that the supplied parameters are incorrect
     *
     * @author Viktor Hanstorp (ndi14vhp@student.hig.se)
     */
    @SuppressWarnings("serial")
    public class IncorrectParameters extends LispException
    {
        IncorrectParameters()
        {
        }

        IncorrectParameters(String message)
        {
            super(message);
        }
    }

    private Part break_to_parts(CharactersToParseStack stack)
    {
        try
        {
            stack.clean();
        }
        catch (CharactersToParseStack.OutOfBounds | CharactersToParseStack.StackEmpty e)
        {
            throw new SyntaxError("Unexpected ending; ')' expected");
        }

        char character = stack.pop();

        if (character == ')')
            throw new SyntaxError("Unexpected ')'");

        if (character == '\'')
        {
            return new PartCons(new PartSymbol("quote"), new PartCons(break_to_parts(stack), NIL));
        }

        if (character == '(')
        {
            stack.clean();

            if (stack.nextIs(')'))
            {
                stack.pop();
                return NIL;
            }

            List<Part> parts = new ArrayList<Part>();
            parts.add(break_to_parts(stack));

            do
            {
                stack.clean();
                
                if (stack.nextIs(')'))
                {
                    stack.pop();
                    return new PartCons(parts.toArray(new Part[0]));
                }
                
                if (stack.nextIs('.'))
                {
                    stack.pop();
                    if (stack.nextIsSpace() || stack.nextIs('('))
                    // cons: (1 . 2) or (1 .(2 3))
                    {
                        Part cdr = break_to_parts(stack);
                        stack.clean();
                        if (!stack.nextIs(')'))
                        {
                            throw new SyntaxError("Invalid cons");
                        }
                        stack.pop();
                        return new PartCons(parts.toArray(new Part[0]), cdr);
                    }
                    else
                    // atom: '.3' or '.b'
                    {
                        stack.unpop();
                        parts.add(break_to_parts(stack));
                    }
                }
                else
                    parts.add(break_to_parts(stack));
            }
            while (true);
        }
        else
        {
            // TODO:
            // http://www.lispworks.com/documentation/HyperSpec/Body/02_dh.htm
            // ,
            // http://stackoverflow.com/questions/4873810/what-does-mean-in-lisp
            // Parse '#...' syntax, ex: '#(1 2 3)'

            StringBuilder tokenBuilder = new StringBuilder();
            tokenBuilder.append(character);

            char open = 0;
            if (character == '"')
                // ' is not string-quote, it is only '(quote exp)'
                open = character;

            boolean isString = open != 0;

            // TODO: Allow escaped quotes " \" "

            while (open != 0 || !stack.isParseEnd(stack.peak()))
            {
                tokenBuilder.append(stack.pop());

                if (open != 0 && open == stack.peak())
                {
                    tokenBuilder.append(stack.pop());
                    break;
                }
            }

            String token = tokenBuilder.toString();

            if (isString)
                return new PartString(token);

            try
            {
                return new PartNumber(new LispNumber(token));
            }
            catch (NumberFormatException notNumber)
            {
                return new PartSymbol(token);
            }
        }
    }

    private Part eval(Part part)
    {
        return eval(part, global_enviroment);
    }

    interface FunctionBody
    {
        Part run(Part[] args);
    }

    @SuppressWarnings("serial")
    HashMap<String, FunctionBody> functions = new HashMap<String, FunctionBody>()
    {
        @SuppressWarnings("unchecked")
        public <T> PartValue<T> is(Part part)
        {
            try
            {
                return (PartValue<T>) part;
            }
            catch (ClassCastException e)
            {
                throw new IncorrectParameters();
            }
        }

        public void add(String name, FunctionBody body)
        {
            put(name, body);
        }

        {
            add("quote", (args) -> is(args[0]));
        }
    };

    private Part eval(Part part, Environment env)
    {
        if (part instanceof PartSymbol)
        {
            return env.get(((PartSymbol) part).value);
        }

        if (!(part instanceof PartCons)) // PartAtom
        {
            return part;
        }

        if (part.equals(NIL))
            return NIL;

        Part[] parts = ((PartCons) part).asArray();

        if (parts[0] instanceof PartSymbol)
        {
            String symbol = ((PartSymbol) parts[0]).value;
            Part[] args = parts.length == 0 ? new Part[0] : Arrays.copyOfRange(parts, 1, parts.length);

            if (symbol.equals("quote"))
            {
                if (args.length != 1)
                    throw new SyntaxError("Quote requires one and only one value");
                return args[0];
            }

            if (symbol.equals("if"))
            {
                if (args.length == 2 || args.length == 3)
                {
                    if (eval(args[0], env).equals(NIL)) // is_false
                    {
                        if (args.length == 3) // else
                        {
                            return eval(args[2], env);
                        }
                    }
                    else
                        return eval(args[1], env);
                }
                else
                    throw new SyntaxError(symbol + " test then [else]");
            }

            /*
             * 
             * setq {var form}
             * 
             * The special form (setq var1 form1 var2 form2 ...) is the ``simple
             * variable assignment statement'' of Lisp. First form1 is evaluated
             * and the result is stored in the variable var1, then form2 is
             * evaluated and the result stored in var2, and so forth. The
             * variables are represented as symbols, of course, and are
             * interpreted as referring to static or dynamic instances according
             * to the usual rules. Therefore setq may be used for assignment of
             * both lexical and special variables.
             * 
             * setq returns the last value assigned, that is, the result of the
             * evaluation of its last argument. As a boundary case, the form
             * (setq) is legal and returns nil. There must be an even number of
             * argument forms. For example, in
             */

            // TODO: ?
            if (symbol.equals("setq"))
            {
                if (args.length % 2 != 0)
                    throw new SyntaxError();

                Part returns = NIL;
                for (int i = 0; i < args.length; i += 2)
                {
                    if (!(args[i] instanceof PartSymbol))
                        throw new SyntaxError(symbol + " {var form}*");

                    returns = env.set(((PartSymbol) args[i]).value, eval(args[i + 1], env));
                }

                return returns;
            }

            // TODO: ?
            if (symbol.equals("set") || symbol.equals("define"))
            {
                if (args.length != 2)
                    throw new SyntaxError(symbol + " requires two and only two values");
                if (!(args[0] instanceof PartSymbol))
                    throw new SyntaxError(symbol + " requires a symbol");

                return env.set(((PartSymbol) args[0]).value, eval(args[1], env));
            }

            if (symbol.equals("lambda"))
            {
                if (args.length != 2 || !(args[0] instanceof PartCons))
                    throw new SyntaxError(symbol + " (var...) exp");

                for (Part p : ((PartCons) args[0]).asArray())
                    if (!(p instanceof PartSymbol))
                        throw new SyntaxError("lambda requires symbols as parameters");

                Part[] parameters = ((PartCons) args[0]).asArray();

                return new PartLambda((lambda_args) ->
                {
                    Environment local_env = new Environment(env);
                    for (int i = 0; i < parameters.length; i++)
                    {
                        local_env.set(((PartSymbol) parameters[i]).value, lambda_args.length > i ? eval(lambda_args[i], local_env) : NIL);
                    }

                    return eval(args[1], local_env);
                });
            }

            if (symbol.equals("defun"))
            {
                if (args.length != 3 || !(args[0] instanceof PartSymbol) || !(args[1] instanceof PartCons))
                    throw new SyntaxError(symbol + " name {symbol...} exp");

                for (Part p : ((PartCons) args[1]).asArray())
                    if (!(p instanceof PartSymbol))
                        throw new SyntaxError("defun requires symbols as parameters");

                Part[] parameters = ((PartCons) args[1]).asArray();

                env.set(((PartSymbol) args[0]).value, new PartLambda((lambda_args) ->
                {
                    Environment local_env = new Environment(env);
                    for (int i = 0; i < parameters.length; i++)
                    {
                        local_env.set(((PartSymbol) parameters[i]).value, lambda_args.length > i ? eval(lambda_args[i], local_env) : NIL);
                    }

                    return eval(args[1], local_env);
                }));

                return NIL; // Unsure
            }

            if (symbol.equals("progn"))
            {
                Part results = NIL;

                for (Part arg : args)
                    results = eval(arg, env);

                return results;
            }

            if (symbol.equals("eval"))
            {
                if (args.length != 1)
                    throw new SyntaxError(symbol + " exp");

                return eval(eval(args[0], env), env);
            }

            if (symbol.equals("list"))
            {
                Part[] entities = new Part[args.length];
                for (int i = 0; i < args.length; i++)
                    entities[i] = eval(args[i], env);
                return entities.length == 0 ? NIL : new PartCons(entities);
            }

            if (symbol.equals("cons"))
            {
                if (args.length != 2)
                    throw new SyntaxError(symbol + " exp");
                return new PartCons(eval(args[0], env), eval(args[1], env));
            }

            if (symbol.equals("car"))
            {
                if (args.length != 1)
                    throw new SyntaxError(symbol + " cons");
                Part list = eval(args[0], env);
                if (!(list instanceof PartCons))
                    throw new SyntaxError(symbol + " cons");

                return ((PartCons) list).equals(NIL) ? NIL : ((PartCons) list).car;
            }

            if (symbol.equals("cdr"))
            {
                if (args.length != 1)
                    throw new SyntaxError(symbol + " cons");
                Part list = eval(args[0], env);
                if (!(list instanceof PartCons))
                    throw new SyntaxError(symbol + " cons");

                return ((PartCons) list).equals(NIL) ? NIL : ((PartCons) list).cdr;
            }

            if (symbol.equals("typep"))
            {
                if (args.length != 2 || !(args[1] instanceof PartSymbol))
                    throw new SyntaxError(symbol + " obj type");
                Part obj = eval(args[0], env);

                boolean yes = false;
                switch (((PartSymbol) args[1]).value)
                {
                case "null":
                    yes = obj instanceof PartCons && ((PartCons) obj).equals(NIL);
                    break;
                case "atom":
                    yes = !(obj instanceof PartCons) || ((PartCons) obj).equals(NIL);
                    break;
                case "cons":
                    yes = obj instanceof PartCons && !((PartCons) obj).equals(NIL);
                    break;
                case "list":
                    yes = obj instanceof PartCons;
                    break;
                case "number":
                    yes = obj instanceof PartNumber;
                    break;
                case "integer":
                    yes = obj instanceof PartNumber && !((PartNumber) obj).isFloat();
                    break;
                case "float":
                    yes = obj instanceof PartNumber && ((PartNumber) obj).isFloat();
                    break;
                case "string":
                    yes = obj instanceof PartString;
                    break;
                case "function":
                    yes = obj instanceof PartLambda;
                    break;
                case "symbol":
                    yes = obj instanceof PartSymbol;
                    break;
                }

                return new PartValue<Boolean>(yes);
            }
        }

        Part[] exps = new Part[parts.length];
        for (int i = 0; i < parts.length; i++)
            exps[i] = eval(parts[i], env);

        if (!(exps[0] instanceof PartLambda))
            throw new LispException("[" + parts[0] + "] is not a function");

        Lambda<Part, Part> function = ((PartLambda) exps[0]).value;
        Part[] arguments = Arrays.copyOfRange(exps, 1, exps.length);

        try
        {
            return (Part) function.exec(arguments);
        }
        catch (IncorrectParameters e)
        {
            throw new IncorrectParameters("Incorrect parameters was sent to the method [" + parts[0] + "]: " + Arrays.deepToString(arguments) + "\nUsage: " + parts[0] + " " + e.getMessage());
        }
        catch (LispException | ClassCastException e)
        {
            throw new IncorrectParameters("Incorrect parameters was sent to the method [" + parts[0] + "]: " + Arrays.deepToString(arguments));
        }
    }

    private Part parse(String str)
    {
        return break_to_parts(new CharactersToParseStack(str));
    }

    /**
     * Runs the given lisp code
     * 
     * @param code
     *            the code (lisp) to run
     * @return the output
     */
    public String run(String code)
    {
        return eval(parse(code)).toString();
    }

    Part runAndReturnPart(String code)
    {
        return eval(parse(code));
    }

    public static void main(String[] args)
    {
        Lisp lisp = new Lisp();

        System.out.println(lisp.run("'(+ 1 1)"));

        System.out.println(lisp.run("(list 1 2)"));
        System.out.println(lisp.run("(list (list 'a 'b) (list 'c 'd 'e) '(f . g))"));

        /*
         * System.out.println(lisp.run("(set a (lambda (a b c) (+ a b c)))"));
         * System.out.println(lisp.run("(a 1 2 7)"));
         */

        // System.out.println(lisp.run("(eval (list 'cdr (car '((quote (a . b)) c))))"));

        // System.out.println(lisp.run("(+ a 1)"));

        // System.out.println(lisp.run("(car (quote (4 3 3 5 0)))"));
        // System.out.println(lisp.run("(car ())"));

        // System.out.println(lisp.eval(lisp.parse("(<= 4 4)")));

        // PartFloat(5)));

        /*
         * if (true) return;
         * 
         * Part root = lisp.parse("(defun queue-full-p (queue)\r\n" +
         * "  \"Return T if QUEUE is full.\"\r\n" +
         * "  (check-type queue queue)\r\n" + "  (= (queue-get-ptr queue) \r\n"
         * + "     (queue-next queue (queue-put-ptr queue))))");
         * 
         * System.out.println(root);
         */
    }
}
