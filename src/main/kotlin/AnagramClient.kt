import java.nio.file.Files
import java.nio.file.Path
import java.util.*

/*
TODO
 1) input from user
 2) convert text to counted set
 3) create subset corpus to only words within givenCountedSet
 4) select random from subset
 5) remove selection from given
 6) remove selection from subset
 7) repeat
 */

fun getWords() = Files.readAllLines(Path.of("corpus.csv")).map {
    it.split(",")
}.flatten().map {
    MutableCountedSet(it.toList())
}.toSet()

fun getInput() = run {
    print("Enter text: ")
    val text = readLine()?: ""
    val letters = text.lowercase(Locale.getDefault()).split(" ").map(String::toList).flatten()
    MutableCountedSet(letters).also { println() }
}

fun getSubset(given: MutableCountedSet<Char>, dict: Set<MutableCountedSet<Char>>): MutableSet<MutableCountedSet<Char>> {
    return mutableSetOf<MutableCountedSet<Char>>().apply {
        for (word in dict)
            if (word.isSubsetOf(given))
                add(word)
    }
}

fun reduce() {
    var remaining = getInput()
    var words = getWords()
    while (remaining.isNotEmpty()) {
        words = getSubset(remaining, words)
        println("words remaining: ${ words.size }")
        val selection = words.random()
        println("selection: ${ selection.getBase() }")
        for (key in selection) {
            val count = selection[key]
            repeat(count) {
                remaining -= key
            }
        }
        println("remaining: $remaining")
        print("press any key to continue: ")
        readLine()
        println()
    }
}

fun main(args: Array<String>) {
    repeat(readLine()?.toInt()?: 1) {
        reduce()
    }
}
