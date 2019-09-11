import java.lang.StringBuilder
import java.util.*

val numbers = Stack<Int>()
val variables = mutableMapOf<String, Int>()
val operators = Stack<Char>()
val lowPriority = charArrayOf('-', '+')
val highPriority = charArrayOf('*', '/', '%')

fun isCorrectBracketSequence(command: String): Boolean {
    var balance = 0
    command.forEach { c ->
        when (c) {
            '(' -> balance++
            ')' -> balance--
            else -> {
            }
        }
        if (balance < 0)
            return false
    }
    return balance == 0
}

fun evaluate(operator: Char, b: Int, a: Int): Int = when (operator) {
    '+' -> a + b
    '-' -> a - b
    '*' -> a * b
    '/' -> a / b
    else -> a % b
}

fun evaluateExpression(command: String): Int? {
    numbers.clear()
    operators.clear()
    var number = 0
    val variableName = StringBuilder()
    try {
        command.forEachIndexed { i, c ->
            when (c.toLowerCase()) {
                in '0'..'9' -> {
                    number = number * 10 + (c - '0')
                    if (i == command.length - 1 || command[i + 1] !in ('0'..'9')) {
                        numbers.push(number)
                        number = 0
                    }
                }
                '(' -> operators.push('(')
                ')' -> {
                    while (operators.peek() != '(') {
                        numbers.push(evaluate(operators.pop(), numbers.pop(), numbers.pop()))
                    }
                    operators.pop()
                }
                in lowPriority -> {
                    while (!operators.isEmpty() && operators.peek() != '(') {
                        numbers.push(evaluate(operators.pop(), numbers.pop(), numbers.pop()))
                    }
                    operators.push(c)
                }
                in highPriority -> {
                    while (!operators.isEmpty() && operators.peek() in highPriority) {
                        numbers.push(evaluate(operators.pop(), numbers.pop(), numbers.pop()))
                    }
                    operators.push(c)
                }
                in 'a'..'z' -> {
                    variableName.append(c)
                    if (i == command.length - 1 || command[i + 1].toLowerCase() !in ('a'..'z')) {
                        if (variables[variableName.toString()] == null) {
                            println("there is no variable $variableName")
                            return null
                        }
                        numbers.push(variables[variableName.toString()])
                        variableName.clear()
                    }
                }
                else -> {
                    println("incorrect format")
                    return null
                }
            }
        }
        while (!operators.isEmpty()) {
            numbers.push(evaluate(operators.pop(), numbers.pop(), numbers.pop()))
        }
        return numbers.pop()
    } catch (e: EmptyStackException) {
        println("incorrect expression")
        return null
    }
}

fun setValue(command: String) {
    val variableName = command.substringBefore('=')
    if (variableName.isEmpty() || variableName.contains("[^a-zA-Z]".toRegex()))
        println("incorrect name of variable")
    evaluateExpression(command.substringAfter('='))?.let { variables[variableName] = it }
}

fun main(args: Array<String>) {
    var command: String
    while (true) {
        command = readLine().toString().replace(" ", "")
        if (command == "exit")
            break
        else if (!isCorrectBracketSequence(command)) {
            println("incorrect usage of brackets")
        } else if (command.startsWith("let")) {
            setValue(command.substring(3))
        } else {
            evaluateExpression(command)?.let { println(it) }
        }
    }
}
