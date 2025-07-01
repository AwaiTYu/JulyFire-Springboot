package awaitzhang.common.util

/**
 * 雪花算法工具类：用于生成全局唯一的 Long 类型 ID
 * 适用于用户ID、订单ID等高并发场景
 */
object SnowflakeIdGenerator {

    // 设置初始时间戳（一般固定为某个历史时间点）
    private const val START_TIMESTAMP = 1700000000000L // 2023-11-15 00:00:00

    // 各部分位数
    private const val DATA_CENTER_ID_BITS = 5      // 数据中心 ID 占用位数
    private const val MACHINE_ID_BITS = 5          // 机器 ID 占用位数
    private const val SEQUENCE_BITS = 12           // 序列号位数（每毫秒支持生成 2^12 = 4096 个 ID）

    // 最大值计算（-1L xor (-1L << n) 相当于 n 位二进制全为1）
    private const val MAX_DATA_CENTER_ID = -1L xor (-1L shl DATA_CENTER_ID_BITS)
    private const val MAX_MACHINE_ID = -1L xor (-1L shl MACHINE_ID_BITS)

    // 左移位数（构造最终 ID 用）
    private const val MACHINE_ID_SHIFT = SEQUENCE_BITS
    private const val DATA_CENTER_ID_SHIFT = SEQUENCE_BITS + MACHINE_ID_BITS
    private const val TIMESTAMP_LEFT_SHIFT = SEQUENCE_BITS + MACHINE_ID_BITS + DATA_CENTER_ID_BITS

    private const val SEQUENCE_MASK = -1L xor (-1L shl SEQUENCE_BITS)

    // 默认数据中心和机器编号（可通过配置读取）
    private var dataCenterId = 1L
    private var machineId = 1L

    // 自增序列（同一毫秒内调用次数）
    private var sequence = 0L
    private var lastTimestamp = -1L

    /**
     * 生成下一个唯一 ID
     */
    @Synchronized
    fun nextId(): Long {
        var timestamp = currentTime()

        // 系统时间回拨，抛出异常
        if (timestamp < lastTimestamp) {
            throw RuntimeException("时钟回拨，拒绝生成 ID")
        }

        if (timestamp == lastTimestamp) {
            // 同一毫秒内自增
            sequence = (sequence + 1) and SEQUENCE_MASK
            if (sequence == 0L) {
                // 序列溢出，等到下一个毫秒
                timestamp = waitUntilNextMillis()
            }
        } else {
            // 新的毫秒重置序列
            sequence = 0L
        }

        lastTimestamp = timestamp

        // 组装 ID：时间戳 + 数据中心 + 机器 + 序列
        return ((timestamp - START_TIMESTAMP) shl TIMESTAMP_LEFT_SHIFT) or
                (dataCenterId shl DATA_CENTER_ID_SHIFT) or
                (machineId shl MACHINE_ID_SHIFT) or
                sequence
    }

    /**
     * 当前系统时间戳（毫秒）
     */
    private fun currentTime(): Long = System.currentTimeMillis()

    /**
     * 等待下一毫秒
     */
    private fun waitUntilNextMillis(): Long {
        var timestamp = currentTime()
        while (timestamp <= lastTimestamp) {
            timestamp = currentTime()
        }
        return timestamp
    }
}
