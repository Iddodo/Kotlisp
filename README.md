# Kotlisp
This is a project for a basic Lisp interpreter written in Kotlin.

It's still in the works, but the POC of evaluating complex expressions is there (can be seen in tests).

Currently the next goals are:
1. Implement parser (can skip lambdas as a first milestone).
2. Add more basic language bindings (e.g. control flow).
3. Possibly unite the current concepts of `LispLexicalScope` and `ArgumentContext`.
4. Implement proper lambdas (can skip verifying recursion as a first milestone).
5. Add mutable state to evaluator object (to allow dynamic definition of functions and variables).
6. Create basic working REPL for interpreter.
7. Research methodology needed to run interpreter given input file.

Other probable goals that are currently marked as optional:
- plus operator for `LispLexicalScope` rather than working with lists.
- compilation to bytecode/LLVM

Things that need to be fixed in parser:
1. recur whitespace (maybe tone down usage of `when` for more concrete logic)
1. Make `nil` parseable (to make this easier, add keyword checking capabilities to char iterator)
1. Check quote on numeric character occurrence
1. Configure parser to accept dotted notation of form `(value . nil)`

Things that need to be fixed in general:
1. Change arguments to be stored as a list of pairs (probably not a good idea to store them in a map, since order might not be preserved)
