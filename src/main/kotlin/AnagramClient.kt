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
 10MAY21
 work on set union, difference, intersection

 */

/*
 3.1)
 */
fun getWords() = Files.readAllLines(Path.of("corpus.csv")).map {
    it.split(",")
}.flatten().map {
    MutableCountedSet(it.toList())
}.toSet()

/*
 1), 2)
 */
fun getInput() = run {
    print("Enter text: ")
    val text = readLine()?: ""
    val letters = text.lowercase(Locale.getDefault()).split(" ").map(String::toList).flatten()
    MutableCountedSet(letters).also { println() }
}

/*
 3.2)
 */
fun getSubset(given: MutableCountedSet<Char>, dict: Set<MutableCountedSet<Char>>): MutableSet<MutableCountedSet<Char>> {
    return mutableSetOf<MutableCountedSet<Char>>().apply {
        for (word in dict)
            if (word.isSubsetOf(given))
                add(word)
    }
}

/*
 4)
 */
fun reduce() {
    var remaining = getInput()
    var words = getWords()
    /*
     TODO
      reduce the cognitive complexity
     */
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

fun test() {
    val watermelon = MutableCountedSet("watermelon".toList())
    val freshwater = MutableCountedSet("freshwater".toList())
    val `watermelon - freshwater` = watermelon - freshwater
    val `freshwater - watermelon` = freshwater - watermelon
    println("watermelon: $watermelon")
    println("watermelon - freshwater: $`watermelon - freshwater`")
    println("freshwater: $freshwater")
    println("freshwater - watermelon: $`freshwater - watermelon`")

}

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        test()
    } else {
        repeat(readLine()?.toInt() ?: 1) {
            reduce()
        }
    }
}
