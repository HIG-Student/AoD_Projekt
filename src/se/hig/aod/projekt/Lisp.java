package se.hig.aod.projekt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * A class that do magic on lisp expressions <br>
 * <br>
 * Inspired by: http://norvig.com/lispy.html
 *
 * @author Viktor Hanstorp (ndi14vhp@student.hig.se)
 */
public class Lisp
{
    class Part
    {
    }

    class Parts extends Part
    {
        public final Part[] parts;

        Parts(Part[] parts)
        {
            this.parts = parts;
        }

        Parts(Part head, Part[] parts)
        {
            this.parts = new Part[parts.length + 1];
            this.parts[0] = head;
            for (int i = 0; i < parts.length; i++)
                this.parts[i + 1] = parts[i];
        }

        @Override
        public String toString()
        {
            return Arrays.deepToString(parts);
        }
    }

    class PartValue<T> extends Part
    {
        public final T value;
        public final Class<?> classType;

        PartValue(T value)
        {
            this.value = value;
            this.classType = value.getClass();
        }

        @Override
        public String toString()
        {
            return value.toString();
        }
    }

    class PartSymbol extends Part
    {
        public final String value;

        PartSymbol(String value)
        {
            this.value = value;
        }

        @Override
        public String toString()
        {
            return value;
        }
    }

    class PartLambda<P, R> extends Part
    {
        public final Lambda<P, R> value;

        PartLambda(Lambda<P, R> value)
        {
            this.value = value;
        }

        @Override
        public String toString()
        {
            return "[lambda]";
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

        char peak()
        {
            if (getSize() == 0)
                throw new StackEmpty("No more chars");

            return source.charAt(index);
        }

        @SuppressWarnings("serial")
        class StackEmpty extends LispException
        {
            public StackEmpty(String message)
            {
                super(message);
            }
        }
    }

    class Environment
    {
        Environment outer = null;
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
        public R exec(P... args);
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

    @SuppressWarnings({ "unchecked", "rawtypes" })
    Environment global_enviroment = new Environment()
    {
        Check isInt = (p) -> (p instanceof PartValue) && ((PartValue) p).classType.equals(Integer.class);
        Check isFloat = (p) -> (p instanceof PartValue) && ((PartValue) p).classType.equals(Float.class);
        Check isNumber = (p) -> (p instanceof PartValue) && Number.class.isAssignableFrom(((PartValue) p).classType);
        Check isAny = (p) -> true;

        PartValue<Boolean> boolObjectOp(Part[] parts, LambdaWithTwoParameters<Object, Boolean> op)
        {
            boolean result;
            Object last;
            result = op.exec(toObject(parts[0]), last = toObject(parts[1]));
            for (int i = 2; i < parts.length; i++)
                result = result && op.exec(last, last = toObject(parts[i]));
            return new PartValue<Boolean>(result);
        }

        PartValue<Boolean> boolNumberOp(Part[] parts, LambdaWithTwoParameters<Float, Boolean> op)
        {
            boolean result;
            float last;
            result = op.exec(toFloat(parts[0]), last = toFloat(parts[1]));
            for (int i = 2; i < parts.length; i++)
                result = result && op.exec(last, last = toFloat(parts[i]));
            return new PartValue<Boolean>(result);
        }

        PartValue<Integer> intOp(Part[] parts, LambdaWithTwoParameters<Integer, Integer> op)
        {
            Integer result;
            result = op.exec(toInt(parts[0]), toInt(parts[1]));
            for (int i = 2; i < parts.length; i++)
                result = op.exec(result, toInt(parts[i]));
            return new PartValue<Integer>(result);
        }

        PartValue<Float> numOp(Part[] parts, LambdaWithTwoParameters<Float, Float> op)
        {
            Float result;
            result = op.exec(toFloat(parts[0]), toFloat(parts[1]));
            for (int i = 2; i < parts.length; i++)
                result = op.exec(result, toFloat(parts[i]));
            return new PartValue<Float>(result);
        }

        boolean allIsInt(Part[] objs)
        {
            for (int i = 0; i < objs.length; i++)
                if (!isInt.check(objs[i]))
                    return false;

            return true;
        }

        boolean allIsNumber(Part[] objs)
        {
            for (int i = 0; i < objs.length; i++)
                if (!isNumber.check(objs[i]))
                    return false;

            return true;
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

        Float toFloat(Part v)
        {
            return ((Number) ((PartValue) v).value).floatValue();
        }

        Integer toInt(Part v)
        {
            return ((Number) ((PartValue) v).value).intValue();
        }

        Object toObject(Part v)
        {
            return ((PartValue) v).value;
        }

        Part e()
        {
            throw new IncorrectParameters();
        }

        {
            set("+", new PartLambda<Part, Part>((args) -> allIsInt(args) ? intOp(args, (a, b) -> a + b) : numOp(args, (a, b) -> a + b)));
            set("-", new PartLambda<Part, Part>((args) -> allIsInt(args) ? intOp(args, (a, b) -> a - b) : numOp(args, (a, b) -> a - b)));
            set("*", new PartLambda<Part, Part>((args) -> allIsInt(args) ? intOp(args, (a, b) -> a * b) : numOp(args, (a, b) -> a * b)));
            set("/", new PartLambda<Part, Part>((args) -> allIsInt(args) ? intOp(args, (a, b) -> a / b) : numOp(args, (a, b) -> a / b)));

            set(">", new PartLambda<Part, Part>((args) -> boolNumberOp(args, (a, b) -> a > b)));
            set("<", new PartLambda<Part, Part>((args) -> boolNumberOp(args, (a, b) -> a < b)));
            set(">=", new PartLambda<Part, Part>((args) -> boolNumberOp(args, (a, b) -> a >= b)));
            set("<=", new PartLambda<Part, Part>((args) -> boolNumberOp(args, (a, b) -> a <= b)));
            set("=", new PartLambda<Part, Part>((args) -> allIsNumber(args) ? boolNumberOp(args, (a, b) -> a.equals(b)) : boolObjectOp(args, (a, b) -> a.equals(b))));

            set("abs", new PartLambda<Part, Part>((args) -> (fc(args, isNumber) && c(args, isInt)) ? new PartValue<Integer>((int) Math.abs(toInt(args[0]))) : new PartValue<Float>(Math.abs(toFloat(args[0])))));
            set("eq", new PartLambda<Part, Part>((args) -> fc(args, isAny, isAny) ? new PartValue<Boolean>(args[0] == args[1]) : null));
            set("max", new PartLambda<Part, Part>((args) -> allIsInt(args) ? intOp(args, (a, b) -> Math.max(a, b)) : numOp(args, (a, b) -> Math.max(a, b))));
            set("min", new PartLambda<Part, Part>((args) -> allIsInt(args) ? intOp(args, (a, b) -> Math.min(a, b)) : numOp(args, (a, b) -> Math.min(a, b))));
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

    private boolean isSpace(char c)
    {
        return c == ' ' || c == '\n' || c == '\r' || c == '\t';
    }

    private boolean isParseEnd(char c)
    {
        return c == ')' || isSpace(c);
    }

    private boolean isStringBreak(char c)
    {
        return c == '\n' || c == '\r';
    }

    private Part break_to_parts(CharactersToParseStack stack)
    {
        if (stack.getSize() == 0)
            throw new SyntaxError("Unexpected ending; ')' expected");

        char character;
        while (isSpace(character = stack.pop()))
        {
        }

        // Comments
        // TODO:
        // http://stackoverflow.com/questions/6365334/lisp-commenting-convention
        // multi-line comments: '#| ... |#' and '#|| ... ||#'

        if (character == ';')
        {
            while (!isStringBreak(character = stack.pop()))
            {
            }
            while (isSpace(character))
            {
                character = stack.pop();
            }
        }

        if (character == ')')
            throw new SyntaxError("Unexpected ')'");
        else
            if (character == '\'')
            {
                return new Parts(new PartSymbol("quote"), new Part[] { break_to_parts(stack) });
            }
            else

                if (character == '(')
                {
                    if (stack.peak() == ')')
                    {
                        stack.pop();
                        return new Parts(new Part[0]);
                    }

                    List<Part> parts = new ArrayList<Part>();
                    do
                    {
                        parts.add(break_to_parts(stack));

                        if (stack.pop() == ')')
                            return new Parts(parts.toArray(new Part[0]));
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
                    if (character == '"' || character == '\'')
                        open = character;

                    boolean isString = open != 0;

                    // TODO: Allow escaped quotes

                    while (open != 0 || !isParseEnd(stack.peak()))
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
                        return new PartValue<String>(token);

                    try
                    {
                        return new PartValue<Integer>(Integer.parseInt(token));
                    }
                    catch (NumberFormatException intException)
                    {
                        try
                        {
                            return new PartValue<Float>(Float.parseFloat(token));
                        }
                        catch (NumberFormatException floatException)
                        {
                            return new PartSymbol(token);
                        }
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

        if (!(part instanceof Parts)) // PartValue
        {
            return part;
        }

        // Parts
        Part[] parts = ((Parts) part).parts;

        if (parts.length == 0)
            return part;

        if (parts[0] instanceof PartSymbol)
        {
            String symbol = ((PartSymbol) parts[0]).value;

            if (symbol.equals("quote"))
            {
                if (parts.length != 2)
                    throw new SyntaxError("Quote requires one and only one value");
                return parts[1];
            }

            if (symbol.equals("if"))
            {
                if (parts.length == 3 || parts.length == 4)
                {
                    Part test = eval(parts[1], env);
                    if (test instanceof Parts && ((Parts) test).parts.length == 0)
                    {
                        if (parts.length == 4)
                        {
                            return eval(parts[3], env);
                        }
                    }
                    else
                        return eval(parts[2], env);
                }
                else
                    throw new SyntaxError(symbol + " test then [else]");
            }

            // TODO: ?
            if (symbol.equals("set") || symbol.equals("define"))
            {
                if (parts.length != 3)
                    throw new SyntaxError(symbol + " requires two and only two values");
                if (!(parts[1] instanceof PartSymbol))
                    throw new SyntaxError(symbol + " requires a symbol");

                return env.set(((PartSymbol) parts[1]).value, eval(parts[2], env));
            }

            if (symbol.equals("lambda"))
            {
                if (parts.length != 3 || !(parts[1] instanceof Parts))
                    throw new SyntaxError(symbol + " (var...) exp");

                for (Part p : ((Parts) parts[1]).parts)
                    if (!(p instanceof PartSymbol))
                        throw new SyntaxError("lambda requires symbols as parameters");

                Part[] parameters = ((Parts) parts[1]).parts;

                return new PartLambda<Part, Part>((args) ->
                {
                    Environment local_env = new Environment(env);
                    for (int i = 0; i < parameters.length; i++)
                    {
                        local_env.set(((PartSymbol) parameters[i]).value, args.length > i ? eval(args[i], local_env) : new Parts(new Part[0]));
                    }

                    return eval(parts[2], local_env);
                });
            }

            if (symbol.equals("eval"))
            {
                if (parts.length != 2)
                    throw new SyntaxError(symbol + " exp");

                return eval(eval(parts[1], env), env);
            }

            if (symbol.equals("list"))
            {
                Part[] entities = new Part[parts.length - 1];
                for (int i = 1; i < parts.length; i++)
                    entities[i - 1] = eval(parts[i], env);
                return new Parts(entities);
            }

            if (symbol.equals("car"))
            {
                if (parts.length != 2)
                    throw new SyntaxError(symbol + " ([exp...])");
                Part list = eval(parts[1], env);
                if (!(list instanceof Parts))
                    throw new SyntaxError(symbol + " ([exp...])");

                return ((Parts) list).parts.length == 0 ? new Parts(new Part[0]) : ((Parts) list).parts[0];
            }

            if (symbol.equals("cdr"))
            {
                if (parts.length != 2)
                    throw new SyntaxError(symbol + " ([exp...])");
                Part list = eval(parts[1], env);
                if (!(list instanceof Parts))
                    throw new SyntaxError(symbol + " ([exp...])");

                return ((Parts) list).parts.length == 0 ? new Parts(new Part[0]) : new Parts(Arrays.copyOfRange(((Parts) list).parts, 1, ((Parts) list).parts.length));
            }

            if (symbol.equals("null"))
            {
                if (parts.length != 2)
                    throw new SyntaxError(symbol + " obj");
                Part p = eval(parts[1], env);
                return new PartValue<Boolean>(p instanceof Parts && ((Parts) p).parts.length == 0);
            }

            if (symbol.equals("atom"))
            {
                if (parts.length != 2)
                    throw new SyntaxError(symbol + " obj");
                Part p = eval(parts[1], env);
                return new PartValue<Boolean>(!(p instanceof Parts) || ((Parts) p).parts.length == 0);
            }

            if (symbol.equals("consp"))
            {
                if (parts.length != 2)
                    throw new SyntaxError(symbol + " obj");
                Part p = eval(parts[1], env);
                return new PartValue<Boolean>(p instanceof Parts && ((Parts) p).parts.length != 0);
            }

            if (symbol.equals("listp"))
            {
                if (parts.length != 2)
                    throw new SyntaxError(symbol + " obj");
                Part p = eval(parts[1], env);
                return new PartValue<Boolean>(p instanceof Parts);
            }

            if (symbol.equals("numberp"))
            {
                if (parts.length != 2)
                    throw new SyntaxError(symbol + " obj");
                Part p = eval(parts[1], env);
                return new PartValue<Boolean>(p instanceof PartValue && Number.class.isAssignableFrom(((PartValue<?>) p).classType));
            }

            if (symbol.equals("integerp"))
            {
                if (parts.length != 2)
                    throw new SyntaxError(symbol + " obj");
                Part p = eval(parts[1], env);
                return new PartValue<Boolean>(p instanceof PartValue && Integer.class.isAssignableFrom(((PartValue<?>) p).classType));
            }

            if (symbol.equals("floatp"))
            {
                if (parts.length != 2)
                    throw new SyntaxError(symbol + " obj");
                Part p = eval(parts[1], env);
                return new PartValue<Boolean>(p instanceof PartValue && Float.class.isAssignableFrom(((PartValue<?>) p).classType));
            }

            if (symbol.equals("stringp"))
            {
                if (parts.length != 2)
                    throw new SyntaxError(symbol + " obj");
                Part p = eval(parts[1], env);
                return new PartValue<Boolean>(p instanceof PartValue && String.class.isAssignableFrom(((PartValue<?>) p).classType));
            }

            if (symbol.equals("functionp"))
            {
                if (parts.length != 2)
                    throw new SyntaxError(symbol + " obj");
                Part p = eval(parts[1], env);
                return new PartValue<Boolean>(p instanceof PartLambda);
            }

            if (symbol.equals("symbolp"))
            {
                if (parts.length != 2)
                    throw new SyntaxError(symbol + " obj");
                Part p = eval(parts[1], env);
                return new PartValue<Boolean>(p instanceof PartSymbol);
            }
        }

        Part[] exps = new Part[parts.length];
        for (int i = 0; i < parts.length; i++)
            exps[i] = eval(parts[i], env);

        if (!(exps[0] instanceof PartLambda))
            throw new LispException("[" + parts[0] + "] is not a function");

        Part[] arguments = Arrays.copyOfRange(exps, 1, exps.length);
        try
        {
            return (Part) ((PartLambda) exps[0]).value.exec(arguments);
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

    public static void main(String[] args)
    {
        Lisp lisp = new Lisp();

        System.out.println(lisp.run("(list)"));
        System.out.println(lisp.run("(list (list 'a 'b) (list 'c 'd 'e))"));

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

        if (true)
            return;

        Part root = lisp.parse("(defun queue-full-p (queue)\r\n" +
                "  \"Return T if QUEUE is full.\"\r\n" +
                "  (check-type queue queue)\r\n" +
                "  (= (queue-get-ptr queue) \r\n" +
                "     (queue-next queue (queue-put-ptr queue))))");

        System.out.println(root);
    }
}
