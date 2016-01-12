package se.hig.aod.projekt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

//Legend:
//?: not started
//>: currently working on
//T: needs testing
//!: done!

//TODO: T deffun                # block?
//TODO: ? defstruct
//TODO: ! cons
//TODO: ? vector
//TODO: ? array
//TODO: ? cond
//TODO: ? block
//TODO: ? setf
//TODO: ? caadadadadr
//TODO: ? lambda asString()     # native code?
//TODO: T REPL
//TODO: ? Errors

/**
 * A class that do magic on lisp expressions <br>
 * <br>
 * Inspired by: <br>
 * <a href="http://norvig.com/lispy.html">lispy</a><br>
 * <a href=
 * "http://www.michaelnielsen.org/ddi/lisp-as-the-maxwells-equations-of-software/"
 * >blog by Michael Nielsen</a><br>
 * <a href="https://en.wikipedia.org/wiki/Lisp_%28programming_language%29">
 * wikipedia</a><br>
 * <a href="http://clhs.lisp.se/">clhs.lisp.se</a><br>
 * <a href="https://www.cs.cmu.edu/Groups/AI/util/html/cltl/clm/index.html"
 * >www.cs.cmu.edu/Groups/AI/util/html/cltl/clm/index.html</a>
 *
 * @author Viktor Hanstorp (ndi14vhp@student.hig.se)
 */
public class Lisp
{
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

    static final PartAtom t = new PartAtom()
    {
        @Override
        public String asString()
        {
            return "t";
        }
    };

    Environment global_enviroment = new Environment()
    {
        Check isNumber = (p) -> p instanceof PartNumber;
        Check isAny = (p) -> true;

        Part boolNumberOp(Part[] parts, LambdaWithTwoParameters<LispNumber, Boolean> op)
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

                return result ? t : NIL;
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

            set("eq", (args) -> fc(args, isAny, isAny) ? (args[0] == args[1] ? t : NIL) : null);

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
            env.put("t", t);
            env.put("pi", new PartNumber(Math.PI));
            env.put("e", new PartNumber(Math.E));
        }
    };

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
                open = character;

            boolean isString = open != 0;

            while (open != 0 || !stack.isParseEnd(stack.peak()))
            {
                tokenBuilder.append(stack.pop());

                if (open != 0 && (open == stack.peak() && '\\' != stack.peak(-1))) // TODO:
                                                                                   // case:
                                                                                   // "foo\\"bar"
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

    @SuppressWarnings("serial")
    HashMap<String, Integer> nths = new HashMap<String, Integer>()
    {
        {
            put("first", 0);
            put("second", 1);
            put("third", 2);
            put("fourth", 3);
            put("fifth", 4);
            put("sixth", 5);
            put("seventh", 6);
            put("eighth", 7);
            put("ninth", 8);
            put("tenth", 9);
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

            if (symbol.equals("print"))
            {
                if (args.length != 1)
                    throw new SyntaxError("Print requires one and only one value");

                System.out.println(eval(args[0], env));

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

            if (symbol.equals("cond"))
            {
                if (args.length == 0)
                    return NIL;

                for (Part p : args)
                    if (!(p instanceof PartCons))
                        throw new SyntaxError("cond: arguments must be cons");

                Part return_value = NIL;
                for (Part cond_case : args)
                {
                    if (cond_case.equals(NIL))
                        continue;

                    Part[] cond_case_parts = ((PartCons) cond_case).asArray();

                    // (cond (quote a) ())

                    return_value = eval(cond_case_parts[0], env);
                    if (!return_value.equals(NIL))
                    {
                        for (int i = 1; i < cond_case_parts.length; i++)
                            return_value = eval(cond_case_parts[i], env);

                        return return_value;
                    }
                }
                return return_value;
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
                    throw new SyntaxError(symbol + " name (symbol...) exp");

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

                    return eval(args[2], local_env);
                }));

                return args[0]; // Undefined
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

            if (symbol.equals("append"))
            {
                if (args.length < 2)
                    throw new SyntaxError(symbol + " list list [list*]");

                List<Part> appended = new ArrayList<Part>();

                for (int i = 0; i < args.length; i++)
                {
                    Part element = eval(args[i], env);
                    if (!element.equals(NIL))
                        if (element instanceof PartCons)
                            appended.addAll(Arrays.asList(((PartCons) element).asArray()));
                        else
                            if (i != args.length - 1)
                                throw new SyntaxError("only the last argument in append may be a non-list");
                            else
                                return new PartCons(appended.toArray(new Part[0]), element);
                }
                return new PartCons(appended.toArray(new Part[0]));
            }

            if (nths.containsKey(symbol))
            {
                if (args.length != 1)
                    throw new SyntaxError(symbol + " list");

                Integer index = nths.get(symbol);
                Part[] list = ((PartCons) eval(args[0], env)).asArray();

                if (index > list.length - 1)
                    return NIL;
                else
                    return list[index];
            }

            if (symbol.equals("nth") || nths.containsKey(symbol))
            {
                if (args.length != 2)
                    throw new SyntaxError(symbol + " uint list");

                if (!(args[0] instanceof PartNumber) || ((PartNumber) args[0]).value.isfloat || ((PartNumber) args[0]).value.compareTo(0) < 0)
                    throw new SyntaxError("index must be positive integer");

                Integer index = ((PartNumber) args[0]).value.value.intValue();
                Part[] list = ((PartCons) eval(args[1], env)).asArray();

                if (index > list.length - 1)
                    return NIL;
                else
                    return list[index];
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
