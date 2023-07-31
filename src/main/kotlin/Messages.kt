import java.util.concurrent.ConcurrentLinkedQueue

class Messages {
    private val messages = ConcurrentLinkedQueue<String>()

    @Synchronized
    fun addMessage(message: String) {
        messages.add(message)
    }

    fun removeAll(messagesToRemove: Messages) {
        messages.removeAll(messagesToRemove.messages)
    }

    override fun toString(): String {
        return if (messages.isEmpty()) "no data" else "\n" + messages.joinToString("\n").trimEnd()
    }
}