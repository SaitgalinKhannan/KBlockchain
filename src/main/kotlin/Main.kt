import kotlin.concurrent.thread

fun main() {
    val blockchain = Blockchain()
    val miners = mutableListOf<Thread>()

    for (i in 0..<10) {
        miners.add(thread {
            mining(blockchain, i.toLong())
        })
    }

    miners.forEach {
        it.join()
    }

    blockchain.showBlockchain()
    println(blockchain.validate())
}