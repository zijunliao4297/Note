package idv.zijunliao.note.data.use_case

sealed class Sort(val order: Order) {
    class Title(order: Order) : Sort(order)
    class Timestamp(order: Order) : Sort(order)
    class Color(order: Order) : Sort(order)

    fun reorder(): Sort {
        return when (this) {
            is Color -> Color(order.inverse())
            is Timestamp -> Timestamp(order.inverse())
            is Title -> Title(order.inverse())
        }
    }
}

sealed class Order {
    object Increasing : Order()
    object Decreasing : Order()

    fun inverse() = when (this) {
        Increasing -> Decreasing
        Decreasing -> Increasing
    }
}