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

        do {
            hash = applySha256(magicNumber.toString())
            magicNumber = Random.nextLong()
        } while (hash.substring(0, n) != "0".repeat(n))

        generatingTime = (timeStamp() - generatingTime) / 1000

        synchronized(this) {
            nStatus = if (generatingTime < 15.0) {
                n++
                "N was increased to $n"
            } else if (generatingTime >= 15.0) {
                n--
                "N was decreased by 1"
            } else if (generatingTime + 1.0 >= 15.0) {
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

fun main() {
    val blockchain = Blockchain()

    val mainer1 = thread {
        blockchain.addBlock(1)
    }
    val mainer2 = thread {
        blockchain.addBlock(2)
    }
    val mainer3 = thread {
        blockchain.addBlock(3)
    }
    val mainer4 = thread {
        blockchain.addBlock(4)
    }
    val mainer5 = thread {
        blockchain.addBlock(5)
    }
    val mainer6 = thread {
        blockchain.addBlock(6)
    }
    val mainer7 = thread {
        blockchain.addBlock(7)
    }
    val mainer8 = thread {
        blockchain.addBlock(8)
    }
    val mainer9 = thread {
        blockchain.addBlock(9)
    }
    val mainer10 = thread {
        blockchain.addBlock(10)
    }

    mainer1.join()
    mainer2.join()
    mainer3.join()
    mainer4.join()
    mainer5.join()
    mainer6.join()
    mainer7.join()
    mainer8.join()
    mainer9.join()
    mainer10.join()

    blockchain.showBlockchain()

    println(blockchain.validate())
}