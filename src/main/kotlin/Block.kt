data class Block(
    val id: Long,
    val creatorId: Long,
    val creationTimes: Long,
    val magicNumber: Long,
    val hash: String,
    val messages: Messages,
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
                "Block data: $messages\n" +
                "Block was generating for $generatingTime seconds\n" +
                "$n\n"
    }
}