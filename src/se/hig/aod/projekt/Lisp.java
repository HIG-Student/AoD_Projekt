package se.hig.aod.projekt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

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
//TODO: T cond
//TODO: ? block
//TODO: T set,setq,setf
//TODO: T caadadadadr
//TODO: ? lambda asString()     # native code?
//TODO: T REPL
//TODO: ? Errors
//TODO: ? scopes
//TODO: > let

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
        Check isNumber = (p) -> p.getPart() instanceof PartNumber;
        Check isAny = (p) -> true;

        PartContainer boolNumberOp(PartContainer[] parts, LambdaWithTwoParameters<LispNumber, Boolean> op)
        {
            try
            {
                if (parts.length < 2)
                    throw new IncorrectParameters();

                boolean result;
                LispNumber last;
                result = op.exec(((PartNumber) parts[0].getPart()).value, last = ((PartNumber) parts[1].getPart()).value);
                for (int i = 2; i < parts.length; i++)
                    result = result && op.exec(last, last = ((PartNumber) parts[i].getPart()).value);

                return new PartContainer(result ? t : NIL);
            }
            catch (IncorrectParameters | ClassCastException e)
            {
                throw new IncorrectParameters("num num [num...]");
            }
        }

        PartContainer numOp(PartContainer[] parts, LambdaWithTwoParameters<LispNumber, LispNumber> op)
        {
            try
            {
                if (parts.length < 2)
                    throw new IncorrectParameters();

                LispNumber result;
                result = op.exec(((PartNumber) parts[0].getPart()).value, ((PartNumber) parts[1].getPart()).value);
                for (int i = 2; i < parts.length; i++)
                    result = op.exec(result, ((PartNumber) parts[i].getPart()).value);

                return new PartContainer(new PartNumber(result));
            }
            catch (IncorrectParameters | ClassCastException e)
            {
                throw new IncorrectParameters("num num [num...]");
            }
        }

        PartContainer num(PartContainer[] parts, LambdaWithOneParameter<LispNumber, LispNumber> op)
        {
            try
            {
                if (parts.length != 1)
                    throw new IncorrectParameters();
                return new PartContainer(new PartNumber(op.exec(((PartNumber) parts[0].getPart()).value)));
            }
            catch (IncorrectParameters | ClassCastException e)
            {
                throw new IncorrectParameters("num");
            }
        }

        boolean c(PartContainer[] args, Check... checks)
        {
            if (args.length != checks.length)
                return false;

            for (int i = 0; i < checks.length; i++)
                if (!checks[i].check(args[i]))
                    return false;

            return true;
        }

        boolean fc(PartContainer[] args, Check... checks)
        {
            if (!c(args, checks))
                throw new IncorrectParameters();
            return true;
        }

        PartContainer e()
        {
            throw new IncorrectParameters();
        }

        void set(String key, Lambda<PartContainer, PartContainer> lambda)
        {
            set(key, new PartContainer(new PartLambda(lambda)));
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

            set("eq", (args) -> fc(args, isAny, isAny) ? new PartContainer(args[0] == args[1] ? t : NIL) : null);

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

            env.put("nil", new PartContainer(NIL));
            env.put("t", new PartContainer(t));
            env.put("pi", new PartContainer(new PartNumber(Math.PI)));
            env.put("e", new PartContainer(new PartNumber(Math.E)));
        }
    };

    private PartContainer break_to_parts(CharactersToParseStack stack)
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
            return new PartContainer(new PartCons(new PartContainer(new PartSymbol("quote")), new PartContainer(new PartCons(break_to_parts(stack), new PartContainer(NIL)))));
        }

        if (character == '(')
        {
            stack.clean();

            if (stack.nextIs(')'))
            {
                stack.pop();
                return new PartContainer(NIL);
            }

            List<PartContainer> parts = new ArrayList<PartContainer>();
            parts.add(break_to_parts(stack));

            do
            {
                stack.clean();

                if (stack.nextIs(')'))
                {
                    stack.pop();
                    return new PartContainer(new PartCons(parts.toArray(new PartContainer[0])));
                }

                if (stack.nextIs('.'))
                {
                    stack.pop();
                    if (stack.nextIsSpace() || stack.nextIs('('))
                    // cons: (1 . 2) or (1 .(2 3))
                    {
                        PartContainer cdr = break_to_parts(stack);
                        stack.clean();
                        if (!stack.nextIs(')'))
                        {
                            throw new SyntaxError("Invalid cons");
                        }
                        stack.pop();
                        return new PartContainer(new PartCons(parts.toArray(new PartContainer[0]), cdr));
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
            boolean stringIsClosed = false;

            while (stack.getSize() != 0 && (open != 0 || !stack.isParseEnd(stack.peak())))
            {
                tokenBuilder.append(stack.pop());

                if (open != 0 && (open == stack.peak() && '\\' != stack.peak(-1)))
                // TODO: case: "foo\\"bar"
                {
                    tokenBuilder.append(stack.pop());
                    stringIsClosed = true;
                    break;
                }
            }

            if (isString && !stringIsClosed)
                throw new SyntaxError("Unescaped string!");

            String token = tokenBuilder.toString();

            if (isString)
                return new PartContainer(new PartString(token));

            try
            {
                return new PartContainer(new PartNumber(new LispNumber(token)));
            }
            catch (NumberFormatException notNumber)
            {
                return new PartContainer(new PartSymbol(token));
            }
        }
    }

    private PartContainer eval(PartContainer part)
    {
        return eval(part, global_enviroment);
    }

    /*
     * interface FunctionBody { PartContainer run(Part[] args); }
     * 
     * 
     * @SuppressWarnings("serial") HashMap<String, FunctionBody> functions = new
     * HashMap<String, FunctionBody>() {
     * 
     * @SuppressWarnings("unchecked") public <T> PartValue<T> is(PartContainer
     * part) { try { return (PartValue<T>) part; } catch (ClassCastException e)
     * { throw new IncorrectParameters(); } }
     * 
     * public void add(String name, FunctionBody body) { put(name, body); }
     * 
     * { add("quote", (args) -> is(args[0])); } };
     */

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

    private PartContainer eval(PartContainer container, Environment env)
    {
        Part part = container.getPart();

        if (part instanceof PartSymbol)
        {
            return env.get(((PartSymbol) part).value);
        }

        if (!(part instanceof PartCons)) // PartAtom
        {
            return container;
        }

        if (part.equals(NIL))
            return container; // hmm?

        PartContainer[] container_parts = ((PartCons) part).asArray();
        Part[] parts = PartContainer.asParts(container_parts);

        if (parts[0] instanceof PartSymbol)
        {
            String symbol = ((PartSymbol) parts[0]).value;
            PartContainer[] container_args = container_parts.length == 0 ? new PartContainer[0] : Arrays.copyOfRange(container_parts, 1, container_parts.length);
            Part[] args = PartContainer.asParts(container_args);

            if (symbol.equals("nil"))
            {
                if (args.length != 0)
                    throw new SyntaxError("nil can't have any value");
                return new PartContainer(NIL);
            }

            if (symbol.equals("quote"))
            {
                if (args.length != 1)
                    throw new SyntaxError("Quote requires one and only one value");
                return container_args[0];
            }

            if (symbol.equals("print"))
            {
                if (args.length != 1)
                    throw new SyntaxError("Print requires one and only one value");

                System.out.println(eval(container_args[0], env));

                return container_args[0];
            }

            if (symbol.equals("if"))
            {
                if (args.length == 2 || args.length == 3)
                {
                    if (eval(container_args[0], env).getPart().equals(NIL)) // is_false
                    {
                        if (args.length == 3) // else
                        {
                            return eval(container_args[2], env);
                        }
                    }
                    else
                        return eval(container_args[1], env);
                }
                else
                    throw new SyntaxError(symbol + " test then [else]");
            }

            if (symbol.equals("cond"))
            {
                if (args.length == 0)
                    return new PartContainer(NIL);

                for (Part p : args)
                    if (!(p instanceof PartCons))
                        throw new SyntaxError("cond: arguments must be cons");

                PartContainer return_value = new PartContainer(NIL);
                for (Part cond_case : args)
                {
                    if (cond_case.equals(NIL))
                        continue;

                    PartContainer[] cond_case_parts = ((PartCons) cond_case).asArray();

                    // (cond (quote a) ())

                    return_value = eval(cond_case_parts[0], env);
                    if (!return_value.getPart().equals(NIL))
                    {
                        for (int i = 1; i < cond_case_parts.length; i++)
                            return_value = eval(cond_case_parts[i], env);

                        return return_value;
                    }
                }
                return return_value;
            }

            // TODO: define

            if (symbol.equals("let"))
            {
                if (args.length == 0)
                    throw new SyntaxError("let ({var | (var [init])}*) {form}*)");

                if (!(args[0] instanceof PartCons))
                    throw new SyntaxError("let need a cons as first parameter");

                Environment local_env = new Environment(env);

                PartContainer[] container_parms = ((PartCons) args[0]).asArray();
                Part[] params = PartContainer.asParts(container_parms);

                HashMap<String, PartContainer> temp_env = new HashMap<String, PartContainer>();

                for (int i = 0; i < params.length; i++)
                {
                    Part param = params[i];

                    if (param instanceof PartSymbol)
                        temp_env.put(((PartSymbol) param).value, new PartContainer(NIL));
                    else
                        if (param instanceof PartCons && ((PartCons) param).car.getPart() instanceof PartSymbol)
                            temp_env.put(((PartSymbol) ((PartCons) param).car.getPart()).value, eval(((PartCons) param).cdr, local_env));
                        else
                            throw new SyntaxError("let have incorrect parameter list");
                }

                for (Entry<String, PartContainer> e : temp_env.entrySet())
                {
                    local_env.set(e.getKey(), e.getValue());
                }

                PartContainer returns = new PartContainer(NIL);
                for (int i = 1; i < args.length; i++)
                {
                    returns = eval(container_args[i], local_env);
                }

                return returns;
            }

            if (symbol.equals("let*"))
            {
                if (args.length == 0)
                    throw new SyntaxError("let* ({var | (var [init])}*) {form}*)");

                if (!(args[0] instanceof PartCons))
                    throw new SyntaxError("let* need a cons as first parameter");

                Environment local_env = new Environment(env);

                PartContainer[] container_parms = ((PartCons) args[0]).asArray();
                Part[] params = PartContainer.asParts(container_parms);

                for (int i = 0; i < params.length; i++)
                {
                    Part param = params[i];

                    if (param instanceof PartSymbol)
                        local_env.set(((PartSymbol) param).value, new PartContainer(NIL));
                    else
                        if (param instanceof PartCons && ((PartCons) param).car.getPart() instanceof PartSymbol)
                            local_env.set(((PartSymbol) ((PartCons) param).car.getPart()).value, eval(((PartCons) param).cdr, local_env));
                        else
                            throw new SyntaxError("let* have incorrect parameter list");
                }

                PartContainer returns = new PartContainer(NIL);
                for (int i = 1; i < args.length; i++)
                {
                    returns = eval(container_args[i], local_env);
                }

                return returns;
            }

            if (symbol.equals("set"))
            {
                if (args.length % 2 != 0)
                    throw new SyntaxError();

                PartContainer returns = new PartContainer(NIL);
                for (int i = 0; i < args.length; i += 2)
                {
                    Part key = eval((container_args[i]), env).getPart();
                    if (!(key instanceof PartSymbol))
                        throw new SyntaxError(symbol + " {var form}*");

                    returns = env.set(((PartSymbol) key).value, eval(container_args[i + 1], env));
                }

                return returns;
            }

            if (symbol.equals("setq"))
            {
                if (args.length % 2 != 0)
                    throw new SyntaxError();

                PartContainer returns = new PartContainer(NIL);
                for (int i = 0; i < args.length; i += 2)
                {
                    if (!(args[i] instanceof PartSymbol))
                        throw new SyntaxError(symbol + " {var form}*");

                    returns = env.set(((PartSymbol) args[i]).value, eval(container_args[i + 1], env));
                }

                return returns;
            }

            if (symbol.equals("setf"))
            {
                if (args.length % 2 != 0)
                    throw new SyntaxError(symbol + " {var form}*");

                PartContainer returns = new PartContainer(NIL);
                for (int i = 0; i < args.length; i += 2)
                {
                    if (args[i] instanceof PartSymbol)
                        env.set(((PartSymbol) args[i]).value, returns = eval(container_args[i + 1], env));
                    else
                    {
                        PartContainer new_value = eval(container_args[i + 1], env);
                        (returns = eval(container_args[i], env)).setPart(new_value.getPart());
                        new_value.setPart(null); // clean up
                    }
                }
                return returns;
            }

            if (symbol.equals("lambda"))
            {
                if (args.length != 2 || !(args[0] instanceof PartCons))
                    throw new SyntaxError(symbol + " (var...) exp");

                PartContainer[] parameters = ((PartCons) args[0]).asArray();

                for (PartContainer p : parameters)
                    if (!(p.getPart() instanceof PartSymbol))
                        throw new SyntaxError("lambda requires symbols as parameters");

                return new PartContainer(new PartLambda((lambda_args) ->
                {
                    Environment local_env = new Environment(env);
                    for (int i = 0; i < parameters.length; i++)
                    {
                        local_env.set(((PartSymbol) parameters[i].getPart()).value, lambda_args.length > i ? eval(lambda_args[i], local_env) : new PartContainer(NIL));
                    }

                    return eval(container_args[1], local_env);
                }));
            }

            if (symbol.equals("defun"))
            {
                if (args.length != 3 || !(args[0] instanceof PartSymbol) || !(args[1] instanceof PartCons))
                    throw new SyntaxError(symbol + " name (symbol...) exp");

                PartContainer[] parameters = ((PartCons) args[1]).asArray();

                for (PartContainer p : parameters)
                    if (!(p.getPart() instanceof PartSymbol))
                        throw new SyntaxError("defun requires symbols as parameters");

                env.set(((PartSymbol) args[0]).value, new PartContainer(new PartLambda((lambda_args) ->
                {
                    Environment local_env = new Environment(env);
                    for (int i = 0; i < parameters.length; i++)
                    {
                        local_env.set(((PartSymbol) parameters[i].getPart()).value, lambda_args.length > i ? eval(lambda_args[i], local_env) : new PartContainer(NIL));
                    }

                    return eval(container_args[2], local_env);
                })));

                return container_args[0]; // Undefined
            }

            if (symbol.equals("progn"))
            {
                PartContainer results = new PartContainer(NIL);

                for (PartContainer arg : container_args)
                    results = eval(arg, env);

                return results;
            }

            if (symbol.equals("eval"))
            {
                if (args.length != 1)
                    throw new SyntaxError(symbol + " exp");

                return eval(eval(container_args[0], env), env);
            }

            if (symbol.equals("list"))
            {
                PartContainer[] entities = new PartContainer[args.length];
                for (int i = 0; i < args.length; i++)
                    entities[i] = eval(container_args[i], env);
                return new PartContainer(entities.length == 0 ? NIL : new PartCons(entities));
            }

            if (symbol.equals("append"))
            {
                if (args.length < 2)
                    throw new SyntaxError(symbol + " list list [list*]");

                List<PartContainer> appended = new ArrayList<PartContainer>();

                for (int i = 0; i < args.length; i++)
                {
                    PartContainer element = eval(container_args[i], env);
                    if (!element.getPart().equals(NIL))
                        if (element.getPart() instanceof PartCons)
                            appended.addAll(Arrays.asList(((PartCons) element.getPart()).asArray()));
                        else
                            if (i != args.length - 1)
                                throw new SyntaxError("only the last argument in append may be a non-list");
                            else
                                return new PartContainer(new PartCons(appended.toArray(new PartContainer[0]), element));
                }
                return new PartContainer(new PartCons(appended.toArray(new PartContainer[0])));
            }

            if (nths.containsKey(symbol))
            {
                if (args.length != 1)
                    throw new SyntaxError(symbol + " list");

                Integer index = nths.get(symbol);
                PartContainer[] list = ((PartCons) eval(container_args[0], env).getPart()).asArray();

                if (index > list.length - 1)
                    return new PartContainer(NIL);
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
                PartContainer[] list = ((PartCons) eval(container_args[1], env).getPart()).asArray();

                if (index > list.length - 1)
                    return new PartContainer(NIL);
                else
                    return list[index];
            }

            if (symbol.equals("cons"))
            {
                if (args.length != 2)
                    throw new SyntaxError(symbol + " exp");
                return new PartContainer(new PartCons(eval(container_args[0], env), eval(container_args[1], env)));
            }

            if (symbol.equals("car"))
            {
                if (args.length != 1)
                    throw new SyntaxError(symbol + " cons");
                PartContainer list = eval(container_args[0], env);
                if (!(list.getPart() instanceof PartCons))
                    throw new SyntaxError(symbol + " cons");

                return ((PartCons) list.getPart()).equals(NIL) ? new PartContainer(NIL) : ((PartCons) list.getPart()).car;
            }

            if (symbol.equals("cdr"))
            {
                if (args.length != 1)
                    throw new SyntaxError(symbol + " cons");
                PartContainer list = eval(container_args[0], env);
                if (!(list.getPart() instanceof PartCons))
                    throw new SyntaxError(symbol + " cons");

                return ((PartCons) list.getPart()).equals(NIL) ? new PartContainer(NIL) : ((PartCons) list.getPart()).cdr;
            }

            if (symbol.startsWith("c") && symbol.endsWith("r") && symbol.matches("^c[ad]+r$"))
            {
                if (args.length != 1)
                    throw new SyntaxError(symbol + " cons");

                PartContainer result = eval(container_args[0], env);

                for (int i = symbol.length() - 2; i > 0; i--)
                {
                    if (result.getPart().equals(NIL))
                        break;
                    if (!(result.getPart() instanceof PartCons))
                        throw new SyntaxError(symbol + ": can't get car: not a list");
                    else
                        if (symbol.charAt(i) == 'a')
                            result = ((PartCons) result.getPart()).car;
                        else

                            result = ((PartCons) result.getPart()).cdr;
                }
                return result;
            }

            if (symbol.equals("typep"))
            {
                if (args.length != 2 || !(args[1] instanceof PartSymbol))
                    throw new SyntaxError(symbol + " obj type");
                Part obj = eval(container_args[0], env).getPart();

                boolean yes = false;
                switch (((PartSymbol) args[1]).value)
                {
                case "null":
                    yes = ((PartCons) obj).equals(NIL);
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

                return new PartContainer(yes ? t : NIL);
            }
        }

        PartContainer[] exps = new PartContainer[parts.length];
        for (int i = 0; i < parts.length; i++)
            exps[i] = eval(container_parts[i], env);

        if (!(exps[0].getPart() instanceof PartLambda))
            throw new LispException("[" + parts[0] + "] is not a function");

        Lambda<PartContainer, PartContainer> function = ((PartLambda) exps[0].getPart()).value;
        PartContainer[] arguments = Arrays.copyOfRange(exps, 1, exps.length);

        try
        {
            return (PartContainer) function.exec(arguments);
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
        CharactersToParseStack stack = new CharactersToParseStack(str);
        Part result;
        do
        {
            result = eval(break_to_parts(stack)).getPart();
            stack.clean();
        }
        while (stack.getSize() != 0);

        return result;
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
        return parse(code).toString();
    }

    Part runAndReturnPart(String code)
    {
        return parse(code);
    }

    public static void main(String[] args)
    {
        Lisp lisp = new Lisp();

        System.out.println(lisp.run("'(1 1 1 1 1)"));
    }
}
