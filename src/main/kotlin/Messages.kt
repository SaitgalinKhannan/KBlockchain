import java.util.*

class Messages {
    private var messages = Collections.synchronizedList(mutableListOf<String>())

    @Synchronized
    fun addMessage(message: String) {
        messages.add(message)
    }

    fun removeAll(messagesToRemove: Messages) {
        messages.removeAll(messagesToRemove.messages)
    }

    fun copyOfMessages(): Messages {
        val list = Collections.synchronizedList(mutableListOf<String>())
        list.addAll(messages)
        val copyOfMessages = Messages()
        copyOfMessages.messages = list
        return copyOfMessages
    }

    override fun toString(): String {
        return if (messages.isEmpty()) "no data" else "\n" + messages.joinToString("\n").trimEnd()
    }
}