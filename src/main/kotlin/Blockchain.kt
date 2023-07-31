import java.security.MessageDigest
import kotlin.random.Random

class Blockchain {
    private var id: Long = 0L
    private val blocks: MutableList<Block> = mutableListOf()
    private val messages = Messages()
    private var previousBlockHash: String = "0"

    @Volatile
    private var n: Int = 0

    fun sendMessage(message: String) {
        messages.addMessage(message)
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

            val localMessages = if (id == 0L) Messages() else messages

            blocks.add(
                Block(
                    id = id,
                    creatorId = creatorId,
                    creationTimes = timeStamp(),
                    magicNumber = magicNumber,
                    hash = hash,
                    messages = localMessages.copyOfMessages(),
                    previousBlockHash = previousBlockHash,
                    generatingTime = generatingTime,
                    n = nStatus
                )
            )

            if (id != 0L) {
                messages.removeAll(localMessages)
            }

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
        blockchain.sendMessage("${Thread.currentThread().name}: Hello!")
        blockchain.addBlock(minerId)
    }
}