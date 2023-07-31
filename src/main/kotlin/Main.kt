import java.security.MessageDigest
import kotlin.concurrent.Volatile
import kotlin.concurrent.thread
import kotlin.random.Random

data class Block(
    val id: Long,
    val creatorId: Long,
    val creationTimes: Long,
    val magicNumber: Long,
    val hash: String,
    val previousBlockHash: String,
    val generatingTime: Long,
    val n: String,
) {
    override fun toString(): String {
        return "Block:\n" +
                "Created by miner # $creatorId\n" +
                "Id: $id\n" +
                "Timestamp: $creationTimes\n" +
                "Magic number: $magicNumber\n" +
                "Hash of the previous block:\n" +
                "$previousBlockHash\n" +
                "Hash of the block:\n" +
                "$hash\n" +
                "Block was generating for $generatingTime seconds\n" +
                "$n\n"
    }
}

class Blockchain {
    private var id: Long = 0L
    private val blocks: MutableList<Block> = mutableListOf()
    private val messages: MutableList<String> = mutableListOf()
    private var previousBlockHash: String = "0"

    @Volatile
    private var n: Int = 0

    @Suppress("Unused")
    @Synchronized
    fun addMessage(message: String) {
        messages.add(message)
    }

    fun addBlock(creatorId: Long) {
        var magicNumber = Random.nextLong()
        var hash: String
        var nStatus: String
        var generatingTime = timeStamp()
        val currentN = n

        do {
            hash = applySha256(magicNumber.toString())
            magicNumber = Random.nextLong()
        } while (hash.substring(0, n) != "0".repeat(n))

        generatingTime = (timeStamp() - generatingTime) / 1000

        synchronized(this) {
            if (currentN != n)
                return
            @Suppress("KotlinConstantConditions")
            nStatus = if (generatingTime < 15) {
                n++
                "N was increased to $n"
            } else if (generatingTime >= 15) {
                n--
                "N was decreased by 1"
            } else if (generatingTime + 1 >= 15) {
                "N stays the same"
            } else {
                "N stays the same"
            }

            blocks.add(
                Block(
                    id = id,
                    creatorId = creatorId,
                    creationTimes = timeStamp(),
                    magicNumber = magicNumber,
                    hash = hash,
                    previousBlockHash = previousBlockHash,
                    generatingTime = generatingTime,
                    n = nStatus
                )
            )
            id++
            previousBlockHash = hash

            println(blocks.last())
        }
    }

    private fun timeStamp(): Long = System.currentTimeMillis()

    private fun applySha256(input: String): String {
        return try {
            val digest = MessageDigest.getInstance("SHA-256")
            val hash = digest.digest(input.toByteArray(charset("UTF-8")))
            val hexString = StringBuilder()
            for (elem in hash) {
                val hex = Integer.toHexString(0xff and elem.toInt())
                if (hex.length == 1) hexString.append('0')
                hexString.append(hex)
            }
            hexString.toString()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    fun validate(): Boolean {
        return blocks.zipWithNext()
            .all { it.first.hash == it.second.previousBlockHash }
    }

    fun showBlockchain() {
        blocks.forEach(::println)
    }
}

fun mining(blockchain: Blockchain, minerId: Long) {
    while (true) {
        blockchain.addBlock(minerId)
    }
}

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