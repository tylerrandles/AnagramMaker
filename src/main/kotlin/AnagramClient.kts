import java.nio.file.Files
import java.nio.file.Path

val words = Files.readAllLines(Path.of("corpus.csv")).map {
    it.split(",")
}.flatten().map {
    MutableCountedSet(it.toList())
}.toSet()

val given = run {
    print("Enter text: ")
    MutableCountedSet((readLine()?: "").toList()).also(::println)
}

val subset = mutableSetOf<MutableCountedSet<Char>>().apply {
    for (word in words) {
        if (word in given) {
            add(word)
        }
    }
}
