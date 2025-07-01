package awaitzhang.common.result

data class PageResult<T>(
    val total: Long,
    val list: List<T>
) {
    companion object {
        fun <T> of(total: Long, list: List<T>) = PageResult(total, list)
    }
}