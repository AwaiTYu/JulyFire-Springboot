package awaitzhang.common.web

import jakarta.validation.constraints.Min

data class PageRequest(
    @field:Min(1, message = "页码必须大于0")
    val pageNum: Int = 1,

    @field:Min(1, message = "每页条数必须大于0")
    val pageSize: Int = 10
)
